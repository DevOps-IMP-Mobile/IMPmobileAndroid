package com.example.domain.usecase.aws

import com.example.domain.model.user.*
import com.example.domain.repository.AwsUserRepository
import javax.inject.Inject

class GetAllAwsUsersUseCase @Inject constructor(
    private val repository: AwsUserRepository
) {
    suspend operator fun invoke(): Result<List<AwsUser>> {
        return repository.getAllUsers()
    }
}

class GetAwsUserUseCase @Inject constructor(
    private val repository: AwsUserRepository
) {
    suspend operator fun invoke(id: Long): Result<AwsUser> {
        return repository.getUser(id)
    }
}

class CreateAwsUserUseCase @Inject constructor(
    private val repository: AwsUserRepository
) {
    suspend operator fun invoke(request: AwsUserCreateRequest): Result<AwsUser> {
        return repository.createUser(request)
    }
}

class UpdateAwsUserUseCase @Inject constructor(
    private val repository: AwsUserRepository
) {
    suspend operator fun invoke(id: Long, request: AwsUserUpdateRequest): Result<AwsUser> {
        return repository.updateUser(id, request)
    }
}

class DeleteAwsUserUseCase @Inject constructor(
    private val repository: AwsUserRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteUser(id)
    }
}