package com.example.data.repository

import android.util.Log
import com.example.database.dao.FcmTokenDao
import com.example.database.entity.FcmTokenEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepository @Inject constructor(
    private val fcmTokenDao: FcmTokenDao
) {
    companion object {
        private const val TAG = "FcmRepository"
    }

    /**
     * FCM 토큰 저장
     */
    suspend fun saveFcmToken(
        userId: String,
        fcmToken: String,
        spUid: String? = null
    ): Result<Boolean> {
        return try {
            Log.d(TAG, "FCM 토큰 저장: userId=$userId")

            val entity = FcmTokenEntity(
                userId = userId,
                fcmToken = fcmToken,
                spUid = spUid,
                deviceType = "ANDROID",
                updatedAt = System.currentTimeMillis()
            )

            fcmTokenDao.insertOrUpdate(entity)
            Log.d(TAG, "✅ FCM 토큰 저장 성공")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ FCM 토큰 저장 실패", e)
            Result.failure(e)
        }
    }

    /**
     * FCM 토큰 조회
     */
    suspend fun getFcmToken(userId: String): String? {
        return try {
            val entity = fcmTokenDao.getToken(userId)
            entity?.fcmToken
        } catch (e: Exception) {
            Log.e(TAG, "FCM 토큰 조회 실패", e)
            null
        }
    }

    /**
     * FCM 토큰 Flow로 조회
     */
    fun getFcmTokenFlow(userId: String): Flow<FcmTokenEntity?> {
        return fcmTokenDao.getTokenFlow(userId)
    }

    /**
     * FCM 토큰 삭제 (로그아웃 시)
     */
    suspend fun deleteFcmToken(userId: String): Result<Boolean> {
        return try {
            fcmTokenDao.deleteToken(userId)
            Log.d(TAG, "FCM 토큰 삭제 완료: userId=$userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "FCM 토큰 삭제 실패", e)
            Result.failure(e)
        }
    }

    /**
     * 모든 FCM 토큰 조회 (디버깅용)
     */
    suspend fun getAllTokens(): List<FcmTokenEntity> {
        return try {
            fcmTokenDao.getAllTokens()
        } catch (e: Exception) {
            Log.e(TAG, "모든 토큰 조회 실패", e)
            emptyList()
        }
    }
}