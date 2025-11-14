package com.example.data.repository

import com.example.domain.model.notification.*
import com.example.domain.repository.AwsNotificationRepository
import com.example.network.api.AwsNotificationApiService
import com.example.data.mapper.toDomain
import com.example.data.mapper.toDto
import javax.inject.Inject
import javax.inject.Named

class AwsNotificationRepositoryImpl @Inject constructor(
    @Named("aws_notification") private val apiService: AwsNotificationApiService
) : AwsNotificationRepository {

    override suspend fun getAllNotifications(): Result<List<AwsNotification>> {
        return try {
            val response = apiService.getAllNotifications()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotification(id: Long): Result<AwsNotification> {
        return try {
            val response = apiService.getNotification(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNotification(request: AwsNotificationCreateRequest): Result<AwsNotification> {
        return try {
            val response = apiService.createNotification(request.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNotification(id: Long): Result<Unit> {
        return try {
            apiService.deleteNotification(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}