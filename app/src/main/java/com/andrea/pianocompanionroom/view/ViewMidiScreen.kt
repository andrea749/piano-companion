package com.andrea.pianocompanionroom.view

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.ui.theme.ThemeColors.ButtonTextColor
import com.andrea.pianocompanionroom.ui.theme.ThemeColors.ControlButtonColor
import com.andrea.pianocompanionroom.ui.theme.ThemeColors.HeaderTextColor
import com.andrea.pianocompanionroom.ui.theme.ThemeColors.NavigationButtonBorderStroke
import com.andrea.pianocompanionroom.ui.theme.ThemeColors.NavigationButtonColors
import com.andrea.pianocompanionroom.viewmodel.ViewMidiViewModel
import kotlinx.coroutines.delay

// TODO handle screen rotations (maybe make required).

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
                color = HeaderTextColor,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize * 1.5F,
            )
            if (!uiState.value.hasStarted) {
                CountdownAnimation(onFinish = {
                    viewModel.updateHasStarted()
                    viewModel.scanAndConnectToTarget()
                })
            } else {
                StreamControls(
                    uiState.value.isPlaying,
                    modifier = Modifier.fillMaxWidth(0.5F),
                    onPause = { viewModel.pause() },
                    onPlay = { viewModel.play() },)
                Button(
                    onClick = navigateToMidiRoom,
                    modifier = Modifier
                        .fillMaxWidth(0.5F),
                    colors = NavigationButtonColors,
                    border = NavigationButtonBorderStroke,
                ) {
                    Text(
                        text = "Finished",
                        color = ButtonTextColor,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun StreamControls(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onPause: () -> Unit = {},
    onPlay: () -> Unit = {},
    onRewind: () -> Unit = {},
    onFastForward: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRewind() },
            painter = painterResource(id = R.drawable.replay_10),
            contentDescription = "Rewind",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(ControlButtonColor)
        )
        if (isPlaying) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPause() },
                painter = painterResource(id = R.drawable.pause_24dp),
                contentDescription = "Pause",
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.tint(ControlButtonColor)
            )
        } else {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlay() },
                painter = painterResource(id = R.drawable.baseline_play_circle_24),
                contentDescription = "Play",
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.tint(ControlButtonColor)
            )
        }
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFastForward() },
            painter = painterResource(id = R.drawable.forward_10),
            contentDescription = "Fast forward",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(ControlButtonColor)
        )
    }
}

@Composable
fun CountdownAnimation(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
) {
    var count by remember { mutableIntStateOf(3) }
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(1000),
        label = "CountAnimation"
    )
    LaunchedEffect(key1 = count) {
        if (count > 0) {
            delay(1000)
            count--
            if (count == 0) {
                onFinish()
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = animatedCount.toString(),
            color = Color.White,
            fontSize = 50.sp,
        )
    }
}
