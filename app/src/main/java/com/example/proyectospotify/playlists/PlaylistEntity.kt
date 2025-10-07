package com.example.proyectospotify.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    val createdAt: Long = System.currentTimeMillis()
)