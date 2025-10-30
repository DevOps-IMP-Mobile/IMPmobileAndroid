// core/domain/src/main/java/com/example/domain/repository/UnifiedRepository.kt
package com.example.domain.repository

import com.example.domain.model.unified.UnifiedItem
import com.example.domain.model.unified.ItemSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UnifiedRepository {
    // 모든 소스의 아이템을 통합해서 가져오기
    suspend fun getAllItems(
        startDate: String,
        endDate: String,
        sources: List<ItemSource> = ItemSource.values().toList()
    ): Flow<List<UnifiedItem>>

    // 특정 소스만 가져오기
    suspend fun getItemsBySource(
        source: ItemSource,
        startDate: String,
        endDate: String
    ): Flow<List<UnifiedItem>>

    // 동기화 상태 확인
    suspend fun getSyncStatus(source: ItemSource): SyncStatus

    // 수동 동기화 트리거
    suspend fun syncSource(source: ItemSource): Result<Boolean>
}

data class SyncStatus(
    val source: ItemSource,
    val lastSyncTime: String?,
    val isSyncing: Boolean,
    val error: String?
)