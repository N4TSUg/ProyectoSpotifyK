package com.example.proyectospotify.playlists

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.ui.theme.ProyectoSpotifyTheme
import com.example.proyectospotify.viewmodels.PlayerViewModel
import com.google.firebase.auth.FirebaseAuth

class PlaylistDetailActivity : ComponentActivity() {

    private val playlistVm: PlaylistViewModel by viewModels()
    private val playerVm: PlayerViewModel by lazy {
        PlayerViewModel.getInstance(application)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.getStringExtra("playlist_id")
        if (playlistId == null) {
            finish()
            return
        }

        setContent {
            ProyectoSpotifyTheme {
                val playlists by playlistVm.playlists.collectAsState()
                val playlist = playlists.find { it.id == playlistId }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(playlist?.name ?: "Playlist", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                        )
                    },
                    containerColor = Color.Black
                ) { padding ->
                    if (playlist == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Playlist no encontrada", color = Color.Gray)
                        }
                    } else {
                        PlaylistSongsList(
                            playlist = playlist,
                            onDeleteSong = { song ->
                                playlistVm.removeSong(playlist.id, song.id)
                            },
                            onPlaySong = { song ->
                                try {
                                    playerVm.playTrack(
                                        track = song.toJamendoTrack(),
                                        playlist = playlist.songs.map { it.toJamendoTrack() },
                                        albumId = playlist.id,
                                        albumImage = song.image,
                                        albumName = playlist.name
                                    )
                                } catch (e: Exception) {
                                    Log.e("PlaylistDetail", "Error al reproducir canción", e)
                                }
                            }
                        )
                    }
                }
            }
        }
        FirebaseAuth.getInstance().currentUser?.uid?.let { playlistVm.loadPlaylists(it) }
    }
}

@Composable
fun PlaylistSongsList(
    playlist: FirebasePlaylist,
    onDeleteSong: (FirebaseSong) -> Unit,
    onPlaySong: (FirebaseSong) -> Unit
) {
    val safeSongs = playlist.songs ?: emptyList()

    if (safeSongs.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Esta playlist está vacía.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            items(safeSongs.size) { index ->
                val song = safeSongs[index]
                SongItem(song = song, onDelete = onDeleteSong, onPlay = onPlaySong)
                Divider(color = Color.Gray.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun SongItem(song: FirebaseSong, onDelete: (FirebaseSong) -> Unit, onPlay: (FirebaseSong) -> Unit) {
    val accentColor = Color(0xFFE53935)
    val context = LocalContext.current

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay(song) },
        headlineContent = {
            Text(song.name, color = Color.White, fontWeight = FontWeight.Bold)
        },
        supportingContent = {
            Text(song.artist, color = Color.Gray)
        },
        leadingContent = {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(song.image)
                    .crossfade(true)
                    .build(),
                contentDescription = song.name,
                modifier = Modifier.size(56.dp)
            )
        },
        trailingContent = {
            IconButton(onClick = { onDelete(song) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = accentColor
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
