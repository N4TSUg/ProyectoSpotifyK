package com.example.proyectospotify.playlists

import com.example.proyectospotify.entities.JamendoTrack

data class FirebaseSong(
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val image: String = "",
    val audio: String = "",
    val duration: Int = 0
)

fun FirebaseSong.toJamendoTrack(): JamendoTrack {
    return JamendoTrack(
        id = this.id,
        name = this.name,
        artist_name = this.artist,
        image = this.image,
        audio = this.audio,
        duration = this.duration
    )
}