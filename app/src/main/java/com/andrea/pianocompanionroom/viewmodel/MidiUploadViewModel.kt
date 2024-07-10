package com.andrea.pianocompanionroom.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.Song
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.convertMidiToSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MidiUploadViewModel @Inject constructor(
    private var songsRepository: SongsRepository, private var contentResolver: ContentResolver,
): ViewModel() {
//    val uiState = mutableStateOf(MidiUploadUiState.NoPick)

    private suspend fun insertSong(songInput: InputStream) {
        val song = convertMidiToSong(songInput)
        songsRepository.insertSong(song)
    }

    fun saveSong(songUri: Uri) {
        contentResolver.openInputStream(songUri)?.use {
            viewModelScope.launch {
                insertSong(it)
            }
        }
    }
}
