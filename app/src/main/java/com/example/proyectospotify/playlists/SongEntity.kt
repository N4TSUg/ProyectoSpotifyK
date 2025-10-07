package com.example.proyectospotify.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val songId: String, // from Jamendo track id
    val title: String,
    val artist: String,
    val imageUrl: String? = null,
    val audioUrl: String? = null
)