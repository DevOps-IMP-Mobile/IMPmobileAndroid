package com.example.domain.manager

interface TokenManager {
    suspend fun getToken(): String?
    suspend fun clearToken()
} 