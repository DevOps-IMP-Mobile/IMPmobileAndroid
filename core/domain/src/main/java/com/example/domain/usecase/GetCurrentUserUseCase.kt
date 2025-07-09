package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? = authRepository.getCurrentUser()
}