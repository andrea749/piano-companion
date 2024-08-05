package com.andrea.pianocompanionroom.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.model.Song
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
    val searchUiState = mutableStateOf("")

    init {
        viewModelScope.launch {
            getAllSongs().collect { songList ->
                uiState.value = MidiRoomUiState.Success(songList)
            }
        }
    }

    fun updateSearchUiState(searchKey: String) {
        searchUiState.value = searchKey
        viewModelScope.launch {
            getAllSongs().collect { songList ->
                uiState.value = MidiRoomUiState.Success(songList)
            }
        }
    }

    private fun getAllSongs(): Flow<List<Song>> {
        if (searchUiState.value.isNotBlank()) {
            val withWildcardSuffix = "${searchUiState.value}%"
            return songsRepository.getFilteredSongsStream(withWildcardSuffix)
        }
        return songsRepository.getAllSongsStream()
    }
}


// MidiRoomScreen db request UI states
sealed class MidiRoomUiState {
    data class Success(val songs: List<Song>): MidiRoomUiState()
    data class Error(val exception: Throwable): MidiRoomUiState()
}
