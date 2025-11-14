package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class AwsNotificationResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("ownScheduleId") val ownScheduleId: Long,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class AwsNotificationCreateRequestDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("ownScheduleId") val ownScheduleId: Long
)