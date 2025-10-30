package com.example.database.dao

import androidx.room.*
import com.example.database.entity.FcmTokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FcmTokenDao {

    /**
     * FCM 토큰 저장 또는 업데이트
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(token: FcmTokenEntity)

    /**
     * 사용자 ID로 FCM 토큰 조회
     */
    @Query("SELECT * FROM fcm_tokens WHERE user_id = :userId LIMIT 1")
    suspend fun getToken(userId: String): FcmTokenEntity?

    /**
     * FCM 토큰 Flow로 조회 (실시간 감지)
     */
    @Query("SELECT * FROM fcm_tokens WHERE user_id = :userId LIMIT 1")
    fun getTokenFlow(userId: String): Flow<FcmTokenEntity?>

    /**
     * 모든 FCM 토큰 조회
     */
    @Query("SELECT * FROM fcm_tokens")
    suspend fun getAllTokens(): List<FcmTokenEntity>

    /**
     * FCM 토큰 삭제 (로그아웃 시)
     */
    @Query("DELETE FROM fcm_tokens WHERE user_id = :userId")
    suspend fun deleteToken(userId: String)

    /**
     * 모든 FCM 토큰 삭제
     */
    @Query("DELETE FROM fcm_tokens")
    suspend fun deleteAllTokens()
}