package com.andrea.pianocompanionroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.convertMidiToSong
import com.andrea.pianocompanionroom.data.parseMidiFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UploadMidiViewModel @Inject constructor(var songsRepository: SongsRepository): ViewModel() {
    suspend fun saveSong(songInput: InputStream) {
        val song = convertMidiToSong(songInput)
        songsRepository.insertSong(song)
    }
}