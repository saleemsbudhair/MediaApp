package com.example.mediaapp.network

import com.example.mediaapp.model.Album
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/albums")
    suspend fun getData(): Response<List<Album>>
}
