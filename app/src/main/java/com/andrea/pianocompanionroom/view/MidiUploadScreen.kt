package com.andrea.pianocompanionroom.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.viewmodel.MidiUploadViewModel

object MidiUploadDestination : NavigationDestination {
    override val route = "midi_upload"
    override val titleRes = R.string.midi_upload_screen
}

@Composable
fun MidiUploadScreen(
    navigateToMidiRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MidiUploadViewModel = hiltViewModel(),
    ) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
        it?.let {
            viewModel.saveSong(songUri = it)
        }
    }
    Button(onClick = { launcher.launch(arrayOf("audio/midi")) }) {
    }
}
