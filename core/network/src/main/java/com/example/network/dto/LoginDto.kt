package com.example.network.dto

data class LoginRequestDto(
    val userId: String,
    val password: String
)

data class LoginResponseDto(
    val token: String?  // nullable로 변경
)