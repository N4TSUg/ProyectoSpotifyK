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
import com.example.proyectospotify.playlists.PlaylistViewModel
import com.example.proyectospotify.playlists.SongEntity

class SearchActivity: ComponentActivity() {
    private val vm: SearchViewModel by viewModels()
    private val playlistVm: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SearchScreen(vm, onAddToPlaylist = { track, playlistId ->
                    playlistVm.addSong(
                        playlistId,
                        SongEntity(
                            songId = track.id,
                            title = track.name,
                            artist = track.artist_name,
                            imageUrl = track.image,
                            audioUrl = track.audio
                        )
                    )
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm: SearchViewModel, onAddToPlaylist: (com.example.proyectospotify.entities.JamendoTrack, Long) -> Unit) {
    val results by vm.results.collectAsState()
    var query by remember { mutableStateOf("") }
    var playlistIdStr by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Buscar canciones") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; vm.search(query) },
                label = { Text("Escribe para buscar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = playlistIdStr,
                onValueChange = { playlistIdStr = it },
                label = { Text("Playlist ID para agregar") },
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
                                playlistIdStr.toLongOrNull()?.let { pid -> onAddToPlaylist(track, pid) }
                            }) { Text("Añadir") }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}