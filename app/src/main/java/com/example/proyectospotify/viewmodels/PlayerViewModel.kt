package com.example.proyectospotify.viewmodels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectospotify.entities.JamendoTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

enum class RepeatMode {
    NONE, REPEAT_ONE, REPEAT_ALL
}

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var progressJob: Job? = null
    private var currentPlaylist: List<JamendoTrack> = emptyList()
    private var originalPlaylist: List<JamendoTrack> = emptyList()
    private var currentTrackIndex: Int = -1

    private val _currentTrack = MutableStateFlow<JamendoTrack?>(null)
    private val _isPlaying = MutableStateFlow(false)
    private val _currentPosition = MutableStateFlow(0)
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    private val _isShuffle = MutableStateFlow(false)
    private val _currentAlbumId = MutableStateFlow<String?>(null)
    private val _currentAlbumName = MutableStateFlow<String?>(null)
    private val _currentAlbumImage = MutableStateFlow<String?>(null)

    val currentTrack = _currentTrack.asStateFlow()
    val isPlaying = _isPlaying.asStateFlow()
    val currentPosition = _currentPosition.asStateFlow()
    val repeatMode = _repeatMode.asStateFlow()
    val isShuffle = _isShuffle.asStateFlow()
    val currentAlbumId = _currentAlbumId.asStateFlow()
    val currentAlbumName = _currentAlbumName.asStateFlow()
    val currentAlbumImage = _currentAlbumImage.asStateFlow()

    init {
        mediaPlayer.setOnCompletionListener {
            handleTrackCompletion()
        }
    }

    fun playTrack(track: JamendoTrack, playlist: List<JamendoTrack>, albumId: String, albumImage: String, albumName: String) {
        if (track.id == _currentTrack.value?.id && mediaPlayer.isPlaying) {
            return
        }

        originalPlaylist = playlist
        _currentAlbumId.value = albumId
        _currentAlbumImage.value = albumImage
        _currentAlbumName.value = albumName

        currentPlaylist = if (_isShuffle.value) playlist.shuffled() else playlist
        currentTrackIndex = currentPlaylist.indexOfFirst { it.id == track.id }

        internalPlay(track)
    }

    private fun internalPlay(track: JamendoTrack) {
        _currentTrack.value = track
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(track.audio)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { player ->
                player.start()
                _isPlaying.value = true
                startProgressTracking()
            }
        } catch (e: IOException) {
            _isPlaying.value = false
            _currentTrack.value = null
        }
    }

    fun seekTo(position: Int) {
        if (mediaPlayer.isPlaying || _currentTrack.value != null) {
            mediaPlayer.seekTo(position)
        }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = mediaPlayer.currentPosition
                delay(500)
            }
        }
    }

    fun togglePlayPause() {
        if (mediaPlayer.isPlaying) {
            pause()
        } else {
            if (_currentTrack.value != null) {
                resume()
            }
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _isPlaying.value = false
            progressJob?.cancel()
        }
    }

    private fun resume() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _isPlaying.value = true
            startProgressTracking()
        }
    }

    private fun handleTrackCompletion() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_ONE -> {
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            }
            RepeatMode.REPEAT_ALL -> {
                playNext()
            }
            RepeatMode.NONE -> {
                if (currentTrackIndex < currentPlaylist.lastIndex) {
                    playNext()
                } else {
                    _isPlaying.value = false
                    progressJob?.cancel()
                    _currentPosition.value = 0
                    mediaPlayer.seekTo(0)
                }
            }
        }
    }

    // Estas funciones ya existen y funcionan perfectamente para nuestro propósito
    fun playNext() {
        if (currentPlaylist.isEmpty()) return
        currentTrackIndex = (currentTrackIndex + 1) % currentPlaylist.size
        internalPlay(currentPlaylist[currentTrackIndex])
    }

    fun playPrevious() {
        if (currentPlaylist.isEmpty()) return
        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else currentPlaylist.lastIndex
        internalPlay(currentPlaylist[currentTrackIndex])
    }

    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.NONE
        }
    }

    fun toggleShuffleMode() {
        _isShuffle.value = !_isShuffle.value
        val track = _currentTrack.value
        if (track != null) {
            currentPlaylist = if (_isShuffle.value) originalPlaylist.shuffled() else originalPlaylist
            currentTrackIndex = currentPlaylist.indexOfFirst { it.id == track.id }
        }
    }

    fun resetPlayer() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        _currentTrack.value = null
        _isPlaying.value = false
        _currentPosition.value = 0
        _currentAlbumId.value = null
        _currentAlbumImage.value = null
        _currentAlbumName.value = null
        progressJob?.cancel()
        currentPlaylist = emptyList()
        originalPlaylist = emptyList()
        currentTrackIndex = -1
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        progressJob?.cancel()
    }

    companion object {
        @Volatile
        private var INSTANCE: PlayerViewModel? = null

        fun getInstance(application: Application): PlayerViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlayerViewModel(application).also { INSTANCE = it }
            }
        }
    }

}
