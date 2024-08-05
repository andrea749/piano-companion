package com.andrea.pianocompanionroom.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.RemoteMusicDbRepository
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.convertMidiToSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MidiUploadViewModel @Inject constructor(
    private var songsRepository: SongsRepository,
    private var contentResolver: ContentResolver,
    private var remoteMusicDbRepository: RemoteMusicDbRepository,
    ): ViewModel() {

    var songName = mutableStateOf("")
    var artistName = mutableStateOf("")
    var songUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)

    private suspend fun insertSong(songInput: InputStream, albumUrl: String) {
        val song = convertMidiToSong(songInput, songName.value, artistName.value, albumUrl)
        songsRepository.insertSong(song)
        songUri.value = Uri.EMPTY
    }

    fun saveSong() {
        viewModelScope.launch {
            val url = getAlbumUrl()
            contentResolver.openInputStream(songUri.value)?.use {
                insertSong(it, url)
            }
        }
    }

    private suspend fun getAlbumUrl(): String {
        // get first artist's MBID
        val artistMbid = remoteMusicDbRepository.getArtistInfo(artistName.value).artists.first().id
        // query for song, including extra params to narrow down results
        val queryString = "${songName.value} AND artist:${artistName.value} AND arid:$artistMbid"
        val queryResults = remoteMusicDbRepository.queryForRecording(queryString)
        var songMbid = ""
        // take the first recording that has an artist that matches the MBID so that covers or
        // features are skipped
        queryResults.recordings.forEach { recording ->
            if (recording.artistCredit.first().artist.id == artistMbid) {
                songMbid = recording.id
                return@forEach
            }
        }
        // prepare link to save in db
        if (songMbid.isNotEmpty()) {
            // get all releases associated with the song and take the ID of the first one
            val releases = remoteMusicDbRepository.getRecordingInfo(songMbid)
            val releaseId = releases.releases.first().id
            return "https://coverartarchive.org/release/$releaseId/front"
        }
        return ""
    }
}
