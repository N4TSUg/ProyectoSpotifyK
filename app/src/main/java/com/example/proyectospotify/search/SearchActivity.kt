package com.example.proyectospotify.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.playlists.FirebaseSong
import com.example.proyectospotify.playlists.PlaylistViewModel

class SearchActivity : ComponentActivity() {
    private val vm: SearchViewModel by viewModels()
    private val playlistVm: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SearchScreen(vm, onAddToPlaylist = { track, playlistId ->
                    val song = FirebaseSong(
                        id = track.id,
                        name = track.name,
                        artist = track.artist_name,
                        image = track.image,
                        audio = track.audio ?: "no_audio_available",
                        duration = track.duration
                    )
                    playlistVm.addSong(playlistId, song)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: SearchViewModel,
    onAddToPlaylist: (JamendoTrack, String) -> Unit
) {
    val results by vm.results.collectAsState()
    var query by remember { mutableStateOf("") }
    var playlistId by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Buscar canciones") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    vm.search(query)
                },
                label = { Text("Escribe para buscar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = playlistId,
                onValueChange = { playlistId = it },
                label = { Text("ID de la playlist (Firebase)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(results) { track ->
                    ListItem(
                        headlineContent = { Text(track.name) },
                        supportingContent = { Text(track.artist_name) },
                        trailingContent = {
                            TextButton(onClick = {
                                if (playlistId.isNotEmpty()) {
                                    onAddToPlaylist(track, playlistId)
                                }
                            }) {
                                Text("Añadir")
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
