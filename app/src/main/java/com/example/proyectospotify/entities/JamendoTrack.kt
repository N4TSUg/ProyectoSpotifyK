package com.example.proyectospotify.entities


data class JamendoTrack(
    val id: String,
    val name: String,
    val duration: Int,   // ✅ duración en segundos
    val audio: String?   // URL del audio
)
