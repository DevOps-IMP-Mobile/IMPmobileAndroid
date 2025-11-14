package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class AwsUserResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("interlockEmail") val interlockEmail: String?
)

data class AwsUserCreateRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("interlockEmail") val interlockEmail: String?
)

data class AwsUserUpdateRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("interlockEmail") val interlockEmail: String?
)