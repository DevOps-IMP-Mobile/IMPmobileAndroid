package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String, password: String): Result<String> {
        return try {
            authRepository.login(userId, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}