package com.example.proyectospotify.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {

    private val repository = FirebasePlaylistRepository()

    private val _playlists = MutableStateFlow<List<FirebasePlaylist>>(emptyList())
    val playlists: StateFlow<List<FirebasePlaylist>> = _playlists

    init {

        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            loadPlaylists(uid)
        }
    }

    fun create(name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val playlist = FirebasePlaylist(
            id = "", // Firestore asignará el ID
            name = name,
            userId = userId,
            songs = emptyList()
        )

        viewModelScope.launch {
            try {
                repository.createPlaylist(playlist)
                loadPlaylists(userId)
            } catch (e: Exception) {
                // Log the exception to help debugging (check Logcat)
                android.util.Log.e("PlaylistViewModel", "Error creating playlist", e)
                // Optionally, you could expose the error via a StateFlow to show in the UI.
            }
        }
    }

    fun loadPlaylists(userId: String) {
        viewModelScope.launch {
            repository.getUserPlaylists(userId).collect { list ->
                _playlists.value = list
            }
        }
    }

    fun addSong(playlistId: String, song: FirebaseSong) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, song)
            FirebaseAuth.getInstance().currentUser?.uid?.let { loadPlaylists(it) }
        }
    }


    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            FirebaseAuth.getInstance().currentUser?.uid?.let { loadPlaylists(it) }
        }
    }


    fun removeSong(playlistId: String, songId: String) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
            FirebaseAuth.getInstance().currentUser?.uid?.let { loadPlaylists(it) }
        }
    }
}
