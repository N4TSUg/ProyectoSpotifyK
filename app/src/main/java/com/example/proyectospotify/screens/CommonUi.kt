package com.example.proyectospotify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectospotify.viewmodels.PlayerViewModel
import com.example.proyectospotify.viewmodels.RepeatMode

@Composable
fun PlayerControls(playerViewModel: PlayerViewModel, accentColor: Color) {
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()
    val isShuffle by playerViewModel.isShuffle.collectAsState()

    currentTrack?.let { track ->
        val trackDuration = (track.duration * 1000).toLong()
        var sliderPosition by remember(currentTrack) { mutableFloatStateOf(0f) }
        var isUserDragging by remember { mutableStateOf(false) }

        // Este es el LaunchedEffect original que sí te funcionaba
        LaunchedEffect(currentPosition) {
            if (!isUserDragging) {
                sliderPosition = if (trackDuration > 0) currentPosition.toFloat() / trackDuration else 0f
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(track.name, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            Text(track.artist_name, color = Color.LightGray, maxLines = 1)

            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    isUserDragging = true
                    sliderPosition = newValue
                },
                onValueChangeFinished = {
                    val newPosition = (sliderPosition * trackDuration).toInt()
                    playerViewModel.seekTo(newPosition)
                    isUserDragging = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val displayTime = if (isUserDragging) (sliderPosition * trackDuration).toLong() else currentPosition.toLong()
                Text(text = formatDuration(displayTime), fontSize = 12.sp, color = Color.Gray)
                Text(text = formatDuration(trackDuration), fontSize = 12.sp, color = Color.Gray)
            }

            // --- El resto de los botones de control no cambia ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { playerViewModel.toggleShuffleMode() }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Aleatorio",
                        tint = if (isShuffle) accentColor else Color.White
                    )
                }
                IconButton(onClick = { playerViewModel.playPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, "Anterior", tint = Color.White)
                }
                IconButton(onClick = { playerViewModel.togglePlayPause() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
                        contentDescription = "Play/Pausa",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                }
                IconButton(onClick = { playerViewModel.playNext() }) {
                    Icon(Icons.Default.SkipNext, "Siguiente", tint = Color.White)
                }
                IconButton(onClick = { playerViewModel.toggleRepeatMode() }) {
                    Icon(
                        imageVector = if (repeatMode == RepeatMode.REPEAT_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repetir",
                        tint = if (repeatMode != RepeatMode.NONE) accentColor else Color.White
                    )
                }
            }
        }
    }
}

fun formatDuration(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
