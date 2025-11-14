package com.example.network.api

import com.example.network.dto.AwsUserResponseDto
import com.example.network.dto.AwsUserCreateRequestDto
import com.example.network.dto.AwsUserUpdateRequestDto
import retrofit2.http.*

interface AwsUserApiService {

    @GET("users")
    suspend fun getAllUsers(): List<AwsUserResponseDto>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): AwsUserResponseDto

    @POST("users")
    suspend fun createUser(@Body request: AwsUserCreateRequestDto): AwsUserResponseDto

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: AwsUserUpdateRequestDto
    ): AwsUserResponseDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)
}