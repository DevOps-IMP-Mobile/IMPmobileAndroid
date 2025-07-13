package com.example.network.api

import com.example.network.dto.LoginRequestDto
import com.example.network.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")  // POST로 변경
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto
}
