package com.example.proyectospotify.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.entities.TrackResponse
import com.example.proyectospotify.service.JamendoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(app: Application): AndroidViewModel(app) {
    private val _results = MutableStateFlow<List<JamendoTrack>>(emptyList())
    val results: StateFlow<List<JamendoTrack>> = _results

    var isLoading = MutableStateFlow(false)
        private set

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _results.value = emptyList(); return@launch
            }
            try {
                isLoading.value = true
                val resp: TrackResponse = JamendoService.api.searchTracks(
                    query = query,
                    clientId = "3a52d888",
                    limit = 50
                )
                _results.value = resp.results
            } finally {
                isLoading.value = false
            }
        }
    }
}