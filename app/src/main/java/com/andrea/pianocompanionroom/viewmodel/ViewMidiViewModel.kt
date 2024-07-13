package com.andrea.pianocompanionroom.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ble.BLE
import com.andrea.pianocompanionroom.data.Song
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.data.parseMidiFile
import com.andrea.pianocompanionroom.view.ViewMidiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


// TODO include count off
/* include all ble code including connecting and streaming to the device.
* This flow is entered by selecting a song from MidiRoomScreen.
* Then, the phone connects to the arduino and sends the song.
* currently, the arduino plays the whole song, but later either the device will send chunks
* and stop sending if the user hits pause, or it'll send the whole song and then send some
* "pause" signal when the user wants. */
@HiltViewModel
class ViewMidiViewModel @Inject constructor(
    songsRepository: SongsRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
): ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ViewMidiDestination.itemIdArg])
    private val ble = BLE(
        "Arduino",
        getString(context, R.string.led_service_uuid),
        getString(context, R.string.led_char_uuid),
    )
    private var currentByte = 0

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

    fun scanAndConnectToTarget(context: Context) {
        val payload = uiState.value.selectedSong?.let { parseMidiFile(it) } ?: error("Song not found")
        ble.scanAndConnectToTarget(context,
            onCharacteristicWrite = { currentByte++
                ble.writeCharacteristic(payload[currentByte])
            },
            firstByteArray = payload[0],
        )
    }
}

// UI state for ViewMidiScreen
data class ViewMidiUiState(
    val selectedSong: Song? = null,
    val isPlaying: Boolean = false,
)
