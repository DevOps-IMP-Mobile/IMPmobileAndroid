package com.example.data.mapper

import com.example.domain.model.notification.*
import com.example.network.dto.AwsNotificationResponseDto
import com.example.network.dto.AwsNotificationCreateRequestDto

fun AwsNotificationResponseDto.toDomain(): AwsNotification {
    return AwsNotification(
        id = this.id,
        userId = this.userId,
        userName = this.userName,
        ownScheduleId = this.ownScheduleId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun AwsNotificationCreateRequest.toDto(): AwsNotificationCreateRequestDto {
    return AwsNotificationCreateRequestDto(
        userId = this.userId,
        ownScheduleId = this.ownScheduleId
    )
}