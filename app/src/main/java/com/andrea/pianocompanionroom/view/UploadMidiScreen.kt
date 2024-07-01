package com.andrea.pianocompanionroom.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.ui.theme.PianoCompanionRoomTheme
import com.andrea.pianocompanionroom.viewmodel.AppViewModelProvider
import com.andrea.pianocompanionroom.viewmodel.UploadMidiViewModel
import kotlinx.coroutines.launch

object UploadMidiDestination : NavigationDestination {
    override val route = "upload_midi"
    override val titleRes = R.string.upload_screen
}

@Composable
fun UploadMidiScreen(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: UploadMidiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
//        Button(onClick = { viewModel.saveSong(context.assets.open("no name.mid")) }) {
        Button(onClick = { coroutineScope.launch {
            viewModel.saveSong(context.assets.open("no name.mid")) }
        }) {
            Text(text = "process and save midi")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PianoCompanionRoomTheme {
//        Greeting("Android")
    }
}
