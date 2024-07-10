package com.andrea.pianocompanionroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.Song
import com.andrea.pianocompanionroom.data.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MidiRoomViewModel @Inject constructor(
    private var songsRepository: SongsRepository,
    ): ViewModel() {

    val uiState = MutableStateFlow(MidiRoomUiState.Success(emptyList()))

    init {
        viewModelScope.launch {
            getAllSongs().collect { songList ->
                uiState.value = MidiRoomUiState.Success(songList)
            }
        }
    }

    private fun getAllSongs(): Flow<List<Song>> {
        return songsRepository.getAllSongsStream()
    }
}


// MidiRoomScreen UI states
sealed class MidiRoomUiState {
    data class Success(val songs: List<Song>): MidiRoomUiState()
    data class Error(val exception: Throwable): MidiRoomUiState()
}