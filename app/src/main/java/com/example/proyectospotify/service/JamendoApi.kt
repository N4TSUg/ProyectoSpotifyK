package com.example.proyectospotify.service


import com.example.proyectospotify.entities.AlbumResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {
    @GET("albums")
    suspend fun getAlbums(
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int
    ): AlbumResponse
}
