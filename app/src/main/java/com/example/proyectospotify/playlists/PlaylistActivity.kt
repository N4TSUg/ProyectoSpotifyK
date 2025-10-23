package com.example.proyectospotify.playlists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectospotify.screens.CreatePlaylistDialog
import com.example.proyectospotify.ui.theme.ProyectoSpotifyTheme
import com.google.firebase.auth.FirebaseAuth

class PlaylistActivity : ComponentActivity() {

    private val playlistVm: PlaylistViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoSpotifyTheme {
                val playlists by playlistVm.playlists.collectAsState()
                var showDialog by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Tus Playlists", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showDialog = true },
                            containerColor = Color(0xFFE53935)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Crear Playlist", tint = Color.White)
                        }
                    },
                    containerColor = Color.Black
                ) { padding ->
                    if (playlists.isEmpty()) {
                        Box(
                            Modifier.fillMaxSize().padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aún no tienes playlists.\nToca '+' para crear la primera.",
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.padding(padding)) {
                            items(playlists.size) { index ->
                                val playlist = playlists[index]
                                ListItem(
                                    headlineContent = { Text(playlist.name, color = Color.White) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }

                if (showDialog) {
                    CreatePlaylistDialog(
                        onDismiss = { showDialog = false },
                        onCreate = { name ->
                            playlistVm.create(name)
                            showDialog = false
                        }
                    )
                }
            }
        }

        FirebaseAuth.getInstance().currentUser?.let { user ->
            playlistVm.loadPlaylists(user.uid)
        }
    }
}
