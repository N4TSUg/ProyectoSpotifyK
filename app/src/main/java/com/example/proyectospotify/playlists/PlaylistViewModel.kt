package com.example.proyectospotify.playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(app: Application): AndroidViewModel(app) {
    private val repo = PlaylistRepository(app)

    val playlists: StateFlow<List<PlaylistEntity>> =
        repo.playlists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun create(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) repo.createPlaylist(name)
    }

    fun addSong(playlistId: Long, song: SongEntity) = viewModelScope.launch {
        repo.addSongToPlaylist(playlistId, song)
    }

    fun removeSong(playlistId: Long, songId: String) = viewModelScope.launch {
        repo.removeSongFromPlaylist(playlistId, songId)
    }

    fun deletePlaylist(playlistId: Long) = viewModelScope.launch {
        repo.deletePlaylist(playlistId)
    }

    fun playlistWithSongs(id: Long): Flow<PlaylistWithSongs?> =
        repo.playlistWithSongs(id)
}