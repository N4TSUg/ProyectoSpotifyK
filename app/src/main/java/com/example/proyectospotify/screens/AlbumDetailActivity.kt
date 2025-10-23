package com.example.proyectospotify.screens

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.service.JamendoService
import com.example.proyectospotify.viewmodels.PlayerViewModel
import kotlinx.coroutines.launch

// <<-- ¡¡LA SOLUCIÓN ESTÁ AQUÍ!! AÑADIMOS LAS IMPORTACIONES QUE FALTABAN -->>
import com.example.proyectospotify.screens.PlayerControls
import com.example.proyectospotify.screens.formatDuration

class AlbumDetailActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by lazy {
        PlayerViewModel.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val albumName = intent.getStringExtra("album_name") ?: "Sin título"
        val albumImage = intent.getStringExtra("album_image") ?: ""
        val albumId = intent.getStringExtra("album_id") ?: ""

        setContent {
            AlbumDetailScreen(
                albumId = albumId,
                albumName = albumName,
                albumImage = albumImage,
                playerViewModel = playerViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: String,
    albumName: String,
    albumImage: String,
    playerViewModel: PlayerViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf<List<JamendoTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val currentTrack by playerViewModel.currentTrack.collectAsState()

    val accentColor = Color(0xFFE53935)
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.3f),
            Color.Black
        )
    )

    LaunchedEffect(albumId) {
        if (albumId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val response = JamendoService.api.getTracks(
                        albumId = albumId,
                        clientId = "3a52d888",
                        limit = 50
                    )
                    tracks = response.results
                } catch (e: Exception) {
                    Log.e("AlbumDetail", "Error cargando pistas", e)
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumName, maxLines = 1, color = Color.White) },
                navigationIcon = {
                    val activity = (LocalContext.current as? Activity)
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(albumImage).crossfade(true).build(),
                contentDescription = albumName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
                        val isCurrentlyPlaying = currentTrack?.id == track.id
                        TrackItem(
                            track = track,
                            trackNumber = index + 1,
                            isCurrentlyPlaying = isCurrentlyPlaying,
                            accentColor = accentColor,
                            onTrackClick = {
                                playerViewModel.playTrack(track, tracks, albumId, albumImage, albumName)
                            }
                        )
                        Divider(color = Color.White.copy(alpha = 0.1f))
                    }
                }
            }

            // Esta llamada ahora es válida gracias al 'import' que añadimos.
            PlayerControls(
                playerViewModel = playerViewModel,
                accentColor = accentColor
            )
        }
    }
}

@Composable
fun TrackItem(
    track: JamendoTrack,
    trackNumber: Int,
    isCurrentlyPlaying: Boolean,
    accentColor: Color,
    onTrackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$trackNumber",
            color = if (isCurrentlyPlaying) accentColor else Color.Gray,
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                color = if (isCurrentlyPlaying) accentColor else Color.White,
                fontWeight = if (isCurrentlyPlaying) FontWeight.Bold else FontWeight.Normal
            )
            Text(text = track.artist_name, color = Color.LightGray)
        }
        if (isCurrentlyPlaying) {
            Icon(Icons.Filled.GraphicEq, contentDescription = "Reproduciendo", tint = accentColor)
        }
    }
}
