package com.andrea.pianocompanionroom.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.data.Song
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.viewmodel.MidiRoomViewModel
import kotlinx.coroutines.launch

object MidiRoomDestination : NavigationDestination {
    override val route = "midi_room"
    override val titleRes = R.string.midi_room_screen
}


@Composable
fun MidiRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: MidiRoomViewModel = hiltViewModel(),
    ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        Column(
            modifier = modifier.padding(it),
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Button(
                    shape = RoundedCornerShape(corner = CornerSize(5.dp)),
                    modifier = Modifier.wrapContentSize(),
                    // TODO - move to viewmodel
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveSong(context.assets.open("no name.mid"))
                        }
                    }
                ) {
                    // TODO - lead to other screen with a save form
                    Text(text = "process and save midi")
                }
            }
            AllSongs(viewModel.uiState.collectAsState().value.songs)
        }
    }

}

@Composable
fun AllSongs(
    songData: List<Song>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            ) {
            Text(
                text = "Pick a song",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(songData) { song ->
                SongCard(song)
            }
        }
    }
}

@Composable
fun SongCard(song: Song, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
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
            text = song.id.toString(),
            fontWeight = FontWeight.SemiBold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = song.artist,
                fontWeight = FontWeight.Thin,
            )
            Text(
                text = song.duration,
                fontWeight = FontWeight.Thin,
            )
        }
    }
}
