package com.example.data.repository

import com.example.domain.model.user.*
import com.example.domain.repository.AwsUserRepository
import com.example.network.api.AwsUserApiService
import com.example.data.mapper.toDomain
import com.example.data.mapper.toDto
import javax.inject.Inject
import javax.inject.Named

class AwsUserRepositoryImpl @Inject constructor(
    @Named("aws_user") private val apiService: AwsUserApiService
) : AwsUserRepository {

    override suspend fun getAllUsers(): Result<List<AwsUser>> {
        return try {
            val response = apiService.getAllUsers()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: Long): Result<AwsUser> {
        return try {
            val response = apiService.getUser(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(request: AwsUserCreateRequest): Result<AwsUser> {
        return try {
            val response = apiService.createUser(request.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(id: Long, request: AwsUserUpdateRequest): Result<AwsUser> {
        return try {
            val response = apiService.updateUser(id, request.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: Long): Result<Unit> {
        return try {
            apiService.deleteUser(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}