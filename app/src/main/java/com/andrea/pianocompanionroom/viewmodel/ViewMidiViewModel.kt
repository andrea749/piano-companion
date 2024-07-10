package com.andrea.pianocompanionroom.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.data.Song
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.view.ViewMidiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


// TODO include count off
@HiltViewModel
class ViewMidiViewModel @Inject constructor(
    songsRepository: SongsRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ViewMidiDestination.itemIdArg])

    val uiState: StateFlow<ViewMidiUiState> = songsRepository.getSongStream(itemId)
        .filterNotNull()
        .map {
            ViewMidiUiState(
                selectedSong = it,
                isPlaying = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ViewMidiUiState()
        )
}

// UI state for ViewMidiScreen
data class ViewMidiUiState(
    val selectedSong: Song? = null,
    val isPlaying: Boolean = false,
)
