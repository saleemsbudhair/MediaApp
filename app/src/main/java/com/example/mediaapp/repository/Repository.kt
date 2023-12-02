package com.example.mediaapp.repository

import com.example.mediaapp.model.Album
import com.example.mediaapp.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    suspend fun fetchData(): Response<List<Album>> {
        return apiService.getData()
    }
}