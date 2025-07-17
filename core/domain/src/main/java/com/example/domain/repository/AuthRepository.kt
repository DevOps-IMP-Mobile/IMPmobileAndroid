package com.example.domain.repository

import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(userId: String, password: String): Result<String>
    suspend fun logout()
    suspend fun getToken(): String?
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User?
    fun getCurrentUserFlow(): Flow<User?>
    suspend fun saveUserToLocal(user: User)
}
