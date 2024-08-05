package com.andrea.pianocompanionroom.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.data.model.Song
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.ui.theme.ThemeColors
import com.andrea.pianocompanionroom.viewmodel.MidiRoomViewModel

object MidiRoomDestination : NavigationDestination {
    override val route = "midi_room"
    override val titleRes = R.string.midi_room_screen
}


@Composable
fun MidiRoomScreen(
    navigateToUploadScreen: () -> Unit,
    navigateToViewScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MidiRoomViewModel = hiltViewModel(),
    ) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = 20.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Button(
                    shape = RoundedCornerShape(corner = CornerSize(5.dp)),
                    modifier = Modifier.wrapContentSize(),
                    onClick = navigateToUploadScreen,
                    colors = ThemeColors.NavigationButtonColors,
                    border = ThemeColors.NavigationButtonBorderStroke,
                ) {
                    Text(
                        text = "upload midi",
                        color = ThemeColors.ButtonTextColor,
                        fontSize = 20.sp,
                        )
                }
            }
            AllSongs(
                viewModel.uiState.collectAsState().value.songs,
                navigateToViewScreen,
                viewModel.searchUiState.value,
                { viewModel.updateSearchUiState(it) }
            )
        }
    }
}

@Composable
fun AllSongs(
    songData: List<Song>,
    navigateToViewScreen: (Int) -> Unit,
    searchUiText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            ) {
            Text(
                text = "Pick a song",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }
        Row(horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(start = 20.dp)
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .height(54.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(corner = CornerSize(5.dp))
                    ),
                shape = RoundedCornerShape(corner = CornerSize(5.dp)),
                value = searchUiText,
                onValueChange = { onValueChange(it) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    disabledContainerColor = Color.DarkGray,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    )
            )
        }
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(songData) { song ->
                SongCard(song, navigateToViewScreen)
            }
        }
    }
}

// TODO add swipe to edit/delete maybe?
@Composable
fun SongCard(
    song: Song,
    navigateToViewScreen: (Int) -> Unit,
    modifier: Modifier = Modifier) {
    Card(
        onClick = { navigateToViewScreen(song.id) },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 10.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                AsyncImage(
                    model = song.albumUrl.ifBlank {
                        ImageRequest.Builder(LocalContext.current)
                            .data(LocalContext.current.filesDir.path + "/" + song.name + ".jpg")
                            .crossfade(true)
                            .build()
                    },
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "album cover",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 8.dp),
                )
            }
            SongDetails(song = song)
        }
    }
}

@Composable
fun SongDetails(song: Song, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = song.name.ifEmpty { "Untitled" },
            fontWeight = FontWeight.SemiBold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = song.artist.ifEmpty { "Untitled" },
                fontWeight = FontWeight.Thin,
            )
            Text(
                text = song.duration,
                fontWeight = FontWeight.Thin,
            )
        }
    }
}

