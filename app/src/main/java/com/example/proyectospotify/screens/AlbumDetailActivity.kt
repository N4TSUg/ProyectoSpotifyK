package com.example.proyectospotify.screens
import androidx.compose.foundation.lazy.itemsIndexed

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.entities.TrackResponse
import com.example.proyectospotify.service.JamendoService
import kotlinx.coroutines.launch

class AlbumDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recibir datos del Intent
        val albumName = intent.getStringExtra("album_name") ?: "Sin título"
        val albumImage = intent.getStringExtra("album_image") ?: ""
        val albumId = intent.getStringExtra("album_id") ?: ""

        setContent {
            MaterialTheme {
                AlbumDetailScreen(albumId, albumName, albumImage)
            }
        }
    }
}

@Composable
fun AlbumDetailScreen(albumId: String, albumName: String, albumImage: String) {
    val coroutineScope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf<List<JamendoTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentTrackIndex by remember { mutableStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }

    // Actualizar posición cada segundo
    LaunchedEffect(isPlaying, mediaPlayer) {
        while (isPlaying && mediaPlayer != null) {
            currentPosition = mediaPlayer!!.currentPosition / 1000
            kotlinx.coroutines.delay(1000)
        }
    }

    // Cargar pistas
    LaunchedEffect(albumId) {
        if (albumId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val response = JamendoService.api.getTracks(
                        albumId = albumId,
                        clientId = "3a52d888",
                        limit = 30
                    )
                    tracks = response.results
                } catch (e: Exception) {
                    Log.e("AlbumDetail", "Error cargando pistas", e)
                    errorMessage = "No se pudieron cargar las pistas"
                } finally {
                    isLoading = false
                }
            }
        } else {
            errorMessage = "ID del álbum inválido"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Imagen y nombre del álbum
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(albumImage)
                .crossfade(true)
                .build(),
            contentDescription = albumName,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = albumName,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de pistas
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else if (errorMessage != null) {
            Text(errorMessage ?: "Error desconocido", color = Color.Red)
        } else if (tracks.isEmpty()) {
            Text("No hay pistas disponibles", color = Color.White)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(tracks) { index, track ->
                    TrackItemGlobal(track = track) {
                        currentTrackIndex = index
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(track.audio)
                            prepare()
                            start()
                            isPlaying = true
                        }
                        currentPosition = 0
                    }
                }
            }
        }

        // Mini reproductor
        if (currentTrackIndex != -1) {
            val track = tracks[currentTrackIndex]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(track.name, color = Color.White, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Retroceder
                    Text("⏮", color = Color.White, fontSize = 28.sp, modifier = Modifier
                        .clickable {
                            if (currentTrackIndex > 0) {
                                currentTrackIndex -= 1
                                mediaPlayer?.release()
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(tracks[currentTrackIndex].audio)
                                    prepare()
                                    start()
                                    isPlaying = true
                                }
                                currentPosition = 0
                            }
                        }
                        .padding(horizontal = 16.dp)
                    )

                    // Play/Pausa
                    Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 28.sp,
                        modifier = Modifier.clickable {
                            mediaPlayer?.let { mp ->
                                if (isPlaying) {
                                    mp.pause()
                                    isPlaying = false
                                } else {
                                    mp.start()
                                    isPlaying = true
                                }
                            }
                        }
                    )

                    // Avanzar
                    Text("⏭", color = Color.White, fontSize = 28.sp, modifier = Modifier
                        .clickable {
                            if (currentTrackIndex < tracks.lastIndex) {
                                currentTrackIndex += 1
                                mediaPlayer?.release()
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(tracks[currentTrackIndex].audio)
                                    prepare()
                                    start()
                                    isPlaying = true
                                }
                                currentPosition = 0
                            }
                        }
                        .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((currentPosition.toFloat() / track.duration.coerceAtLeast(1)))
                            .height(4.dp)
                            .background(Color.Green)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tiempo actual / total
                Text(
                    text = "$currentPosition / ${track.duration} seg",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TrackItemGlobal(track: JamendoTrack, onTrackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTrackClick() }
    ) {
        Text(track.name, fontSize = 18.sp, color = Color.White)
        Text("Duración: ${track.duration} seg", fontSize = 14.sp, color = Color.Gray)
    }
}
