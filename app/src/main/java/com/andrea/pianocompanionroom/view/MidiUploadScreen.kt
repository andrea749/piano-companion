package com.andrea.pianocompanionroom.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.ui.theme.ThemeColors
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
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            viewModel.songUri.value = it
        }
    }

    Scaffold(modifier = modifier) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = { launcher.launch(arrayOf("audio/midi")) },
                        colors = ThemeColors.NavigationButtonColors,
                        border = ThemeColors.NavigationButtonBorderStroke,
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Text(
                            text = "select file",
                            color = ThemeColors.ButtonTextColor,
                            fontSize = 20.sp,)
                    }
                }
                Column {
                    TextField(
                        value = viewModel.songName.value,
                        onValueChange = { viewModel.songName.value = it },
                        label = { Text(text = "Song Name") },
                        maxLines = 1,
                    )
                    TextField(
                        value = viewModel.artistName.value,
                        onValueChange = { viewModel.artistName.value = it },
                        label = { Text(text = "Artist Name") },
                        maxLines = 1,
                    )
                }
                Button(onClick = {
                    viewModel.saveSong()
                    navigateToMidiRoom()
                },
                    colors = ThemeColors.NavigationButtonColors,
                    border = ThemeColors.NavigationButtonBorderStroke,
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = "Upload",
                        color = ThemeColors.ButtonTextColor,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

