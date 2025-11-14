package com.example.domain.model.notification

/**
 * AWS 서버용 알림 정보
 */
data class AwsNotification(
    val id: Long,
    val userId: Long,
    val userName: String,
    val ownScheduleId: Long,
    val createdAt: String,
    val updatedAt: String
)

/**
 * AWS 알림 생성 요청
 */
data class AwsNotificationCreateRequest(
    val userId: Long,
    val ownScheduleId: Long
)