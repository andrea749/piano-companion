package com.andrea.pianocompanionroom.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.convertMidiToSong
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UploadMidiViewModel @Inject constructor(var songsRepository: SongsRepository): ViewModel() {
    suspend fun saveSong(songInput: InputStream) {
        val song = convertMidiToSong(songInput)
        songsRepository.insertSong(song)
    }
}