package com.example.proyectospotify.entities

data class JamendoTrack(
    val id: String,
    val name: String,
    val duration: Int,
    val audio: String?,
    val artist_name: String,
    val image: String
)