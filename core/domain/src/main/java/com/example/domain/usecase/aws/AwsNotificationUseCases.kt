package com.example.domain.usecase.aws

import com.example.domain.model.notification.*
import com.example.domain.repository.AwsNotificationRepository
import javax.inject.Inject

class GetAllAwsNotificationsUseCase @Inject constructor(
    private val repository: AwsNotificationRepository
) {
    suspend operator fun invoke(): Result<List<AwsNotification>> {
        return repository.getAllNotifications()
    }
}

class GetAwsNotificationUseCase @Inject constructor(
    private val repository: AwsNotificationRepository
) {
    suspend operator fun invoke(id: Long): Result<AwsNotification> {
        return repository.getNotification(id)
    }
}

class CreateAwsNotificationUseCase @Inject constructor(
    private val repository: AwsNotificationRepository
) {
    suspend operator fun invoke(request: AwsNotificationCreateRequest): Result<AwsNotification> {
        return repository.createNotification(request)
    }
}

class DeleteAwsNotificationUseCase @Inject constructor(
    private val repository: AwsNotificationRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteNotification(id)
    }
}