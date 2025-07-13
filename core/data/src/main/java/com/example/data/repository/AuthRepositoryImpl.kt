package com.example.data.repository

import android.util.Log
import com.example.domain.repository.AuthRepository
import com.example.domain.model.User
import com.example.network.NetworkModule
import com.example.network.dto.LoginRequestDto

class AuthRepositoryImpl : AuthRepository {

    private val authApiService = NetworkModule.authApiService
    private var currentToken: String? = null
    private var currentUser: User? = null

    override suspend fun login(userId: String, password: String): Result<String> {
        return try {
            Log.d("AuthRepository", "로그인 시도 - userId: $userId")

            val request = LoginRequestDto(userId = userId, password = password)
            val response = authApiService.login(request)  // POST 방식 요청
            Log.d("AuthRepository", "응답 성공: $response")

            val token = response.token
            if (token.isNullOrEmpty()) {
                Log.e("AuthRepository", "토큰이 없습니다")
                return Result.failure(Exception("토큰이 없습니다"))
            }

            currentToken = token
            currentUser = User(userId = userId, token = token)

            Log.d("AuthRepository", "로그인 성공 - token: $token")
            Result.success(token)
        } catch (e: Exception) {
            Log.e("AuthRepository", "로그인 실패", e)
            Log.e("AuthRepository", "에러 메시지: ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun logout() {
        Log.d("AuthRepository", "로그아웃")
        currentToken = null
        currentUser = null
    }

    override suspend fun getToken(): String? {
        return currentToken
    }

    override suspend fun isLoggedIn(): Boolean {
        return currentToken != null
    }

    override suspend fun getCurrentUser(): User? {
        return currentUser
    }
}