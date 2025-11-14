package com.example.network.api

import com.example.network.dto.AwsNotificationResponseDto
import com.example.network.dto.AwsNotificationCreateRequestDto
import retrofit2.http.*

interface AwsNotificationApiService {

    @GET("notifications")
    suspend fun getAllNotifications(): List<AwsNotificationResponseDto>

    @GET("notifications/{id}")
    suspend fun getNotification(@Path("id") id: Long): AwsNotificationResponseDto

    @POST("notifications")
    suspend fun createNotification(@Body request: AwsNotificationCreateRequestDto): AwsNotificationResponseDto

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long)
}