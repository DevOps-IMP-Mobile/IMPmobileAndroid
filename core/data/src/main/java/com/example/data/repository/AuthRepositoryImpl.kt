package com.example.data.repository

import android.util.Log
import com.example.database.dao.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.repository.AuthRepository
import com.example.domain.model.User
import com.example.domain.context.UserContext
import com.example.network.api.AuthApiService  // NetworkModule 대신 직접 주입
import com.example.network.api.MeApiService
import com.example.network.dto.LoginRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val authApiService: AuthApiService,
    private val meApiService: MeApiService // Hilt로 주입받기
) : AuthRepository {

    override suspend fun login(userId: String, password: String): Result<String> {
        return try {
            Log.d("AuthRepository", "로그인 시도 - userId: $userId")

            val request = LoginRequestDto(userId = userId, password = password)
            val response = authApiService.login(request)
            Log.d("AuthRepository", "응답 성공: $response")

            val token = response.token
            if (token.isNullOrEmpty()) {
                Log.e("AuthRepository", "토큰이 없습니다")
                return Result.failure(Exception("토큰이 없습니다"))
            }

            // Room에 사용자 정보 저장/업데이트
            val existingUser = userDao.getUserById(userId)
            if (existingUser != null) {
                userDao.updateUserToken(userId, token, System.currentTimeMillis())
            } else {
                val newUser = User(
                    id = userId,
                    name = userId,
                    token = token,
                    lastLoginAt = System.currentTimeMillis()
                )
                userDao.insertUser(newUser.toEntity())
            }

            // 로그인 성공 후 /me API 호출 및 UserContext 저장
            try {
                val me = meApiService.getMe()
                UserContext.instance.userId = me.userId
                UserContext.instance.groupCode = me.groupCode
                UserContext.instance.spUid = me.spUid
                UserContext.instance.lastProjectNo = me.lastProjectNo
                UserContext.instance.userName = me.userName
                UserContext.instance.userUid = me.userUid
                Log.d("AuthRepository", "/me 정보 저장: $me")
            } catch (e: Exception) {
                Log.e("AuthRepository", "/me API 실패: ${e.message}")
            }

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
        // 현재 로그인된 사용자의 토큰 제거
        val currentUser = userDao.getLoggedInUser()
        currentUser?.let {
            userDao.clearUserToken(it.id)
        }
    }

    override suspend fun getToken(): String? {
        return userDao.getLoggedInUser()?.token
    }

    override suspend fun isLoggedIn(): Boolean {
        return userDao.getLoggedInUser()?.token != null
    }

    override suspend fun getCurrentUser(): User? {
        return userDao.getLoggedInUser()?.toDomain()
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        return userDao.getAllUsers().map { users ->
            users.firstOrNull { it.token != null }?.toDomain()
        }
    }

    override suspend fun saveUserToLocal(user: User) {
        userDao.insertUser(user.toEntity())
    }
}