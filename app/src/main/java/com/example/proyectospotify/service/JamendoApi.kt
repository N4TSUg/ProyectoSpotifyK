package com.example.proyectospotify.service

import com.example.proyectospotify.entities.AlbumResponse
import com.example.proyectospotify.entities.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {
    @GET("albums")
    suspend fun getAlbums(
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int
    ): AlbumResponse

    @GET("tracks")
    suspend fun getTracks(
        @Query("album_id") albumId: String,
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int = 50
    ): TrackResponse

    @GET("tracks")
    suspend fun searchTracks(
        @Query("search") query: String,
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int = 50
    ): TrackResponse
}
