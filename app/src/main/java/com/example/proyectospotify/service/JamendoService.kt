package com.example.proyectospotify.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JamendoService {
    private const val BASE_URL = "https://api.jamendo.com/"

    val api: JamendoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JamendoApi::class.java)
    }
}