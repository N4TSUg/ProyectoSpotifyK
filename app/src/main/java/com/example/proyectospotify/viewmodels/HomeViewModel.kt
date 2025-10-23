package com.example.proyectospotify.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectospotify.entities.AlbumResponse
import com.example.proyectospotify.entities.JamendoAlbum
import com.example.proyectospotify.service.JamendoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _albums = MutableStateFlow<List<JamendoAlbum>>(emptyList())
    val albums: StateFlow<List<JamendoAlbum>> = _albums

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            try {
                val response: AlbumResponse = JamendoService.api.getAlbums(
                    clientId = "3a52d888",
                    limit = 20
                )

                val albumsWithImages = response.results.take(8).mapIndexed { idx, album ->
                    val fixedImage = if (album.image.startsWith("//")) "https:${album.image}" else album.image
                    Log.i("HomeViewModel", "album[$idx]: ${album.name}, image: $fixedImage")
                    album.copy(image = fixedImage)
                }

                _albums.value = albumsWithImages

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching albums", e)
            }
        }
    }
}