package com.andrea.pianocompanionroom.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.convertMidiToSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MidiUploadViewModel @Inject constructor(
    private var songsRepository: SongsRepository, private var contentResolver: ContentResolver,
): ViewModel() {

    var songName = mutableStateOf("")
    var artistName = mutableStateOf("")
    var songUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)

    private suspend fun insertSong(songInput: InputStream) {
        val song = convertMidiToSong(songInput, songName.value, artistName.value)
        songsRepository.insertSong(song)
        songUri.value = Uri.EMPTY
    }

    fun saveSong() {
        contentResolver.openInputStream(songUri.value)?.use {
            viewModelScope.launch {
                insertSong(it)
            }
        }
    }
}
