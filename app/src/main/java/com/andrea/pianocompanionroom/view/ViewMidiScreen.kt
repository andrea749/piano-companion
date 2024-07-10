package com.andrea.pianocompanionroom.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.viewmodel.ViewMidiViewModel

// TODO add rewind placeholders and handle screen rotations (maybe make required).

object ViewMidiDestination : NavigationDestination {
    override val route = "view_midi"
    override val titleRes = R.string.view_midi_screen
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun ViewMidiScreen(
    navigateToMidiRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewMidiViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(modifier = modifier
        .fillMaxSize(),
        containerColor = Color.Black) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = uiState.value.selectedSong?.name ?: "No Title",
                color = Color.Blue,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize * 1.5F,
            )
            StreamControls(modifier = Modifier.fillMaxWidth(0.5F))
            Button(
                onClick = navigateToMidiRoom,
                modifier = Modifier
                    .fillMaxWidth(0.5F),
                colors = ButtonColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent),
                border = BorderStroke(2.dp, Color.Red),
            ) {
                Text(
                    text = "Finished",
                    color = Color.White,
                    fontSize = 20.sp,
                    )
            }
        }
    }
}

@Composable
fun StreamControls(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /*TODO*/ },
            painter = painterResource(id = R.drawable.baseline_play_circle_24),
            contentDescription = "Play",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color.White)
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /*TODO*/ },
            painter = painterResource(id = R.drawable.pause_24dp),
            contentDescription = "Pause",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}
