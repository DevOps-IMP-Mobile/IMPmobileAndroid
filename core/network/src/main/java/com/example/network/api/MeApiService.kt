package com.example.network.api

import com.example.network.dto.MeResponse
import retrofit2.http.GET

interface MeApiService {
    @GET("me")
    suspend fun getMe(): MeResponse
} 