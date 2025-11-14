package com.example.domain.repository

import com.example.domain.model.notification.AwsNotification
import com.example.domain.model.notification.AwsNotificationCreateRequest

interface AwsNotificationRepository {

    /**
     * 모든 알림 조회
     */
    suspend fun getAllNotifications(): Result<List<AwsNotification>>

    /**
     * 알림 조회
     */
    suspend fun getNotification(id: Long): Result<AwsNotification>

    /**
     * 알림 생성
     */
    suspend fun createNotification(request: AwsNotificationCreateRequest): Result<AwsNotification>

    /**
     * 알림 삭제
     */
    suspend fun deleteNotification(id: Long): Result<Unit>
}