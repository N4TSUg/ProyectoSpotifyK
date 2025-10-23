package com.example.proyectospotify.playlists

import com.example.proyectospotify.entities.JamendoTrack

data class FirebasePlaylist(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val songs: List<FirebaseSong> = emptyList()
)
