// core/data/src/main/java/com/example/data/repository/MockUnifiedRepositoryImpl.kt
package com.example.data.repository

import com.example.domain.model.unified.*
import com.example.domain.repository.SyncStatus
import com.example.domain.repository.UnifiedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockUnifiedRepositoryImpl @Inject constructor() : UnifiedRepository {

    override suspend fun getAllItems(
        startDate: String,
        endDate: String,
        sources: List<ItemSource>
    ): Flow<List<UnifiedItem>> = flow {
        delay(500) // 네트워크 지연 시뮬레이션

        val items = mutableListOf<UnifiedItem>()

        // Mock 앱 이슈
        if (ItemSource.APP_ISSUE in sources) {
            items.add(
                UnifiedItem.AppIssue(
                    id = "issue-1",
                    title = "로그인 버그 수정",
                    description = "로그인 시 토큰이 저장되지 않는 문제",
                    startDate = "2025-10-20",
                    endDate = "2025-10-27",
                    priority = UnifiedPriority.CRITICAL,
                    status = UnifiedStatus.IN_PROGRESS,
                    tags = listOf("버그", "긴급"),
                    issueType = "버그",
                    assignee = "홍길동"
                )
            )
        }

        // Mock 구글 캘린더 일정
        if (ItemSource.GOOGLE_CALENDAR in sources) {
            items.add(
                UnifiedItem.GoogleEvent(
                    id = "google-1",
                    title = "팀 회의",
                    description = "주간 스프린트 회의",
                    startDate = "2025-10-26",
                    endDate = "2025-10-26",
                    priority = UnifiedPriority.HIGH,
                    status = UnifiedStatus.TODO,
                    tags = listOf("회의", "팀"),
                    location = "회의실 A",
                    attendees = listOf("김철수", "이영희", "박민수"),
                    meetingLink = "https://meet.google.com/abc-defg-hij"
                )
            )
        }

        // Mock 노션 작업
        if (ItemSource.NOTION in sources) {
            items.add(
                UnifiedItem.NotionTask(
                    id = "notion-1",
                    title = "API 문서 작성",
                    description = "REST API 명세서 작성 완료하기",
                    startDate = "2025-10-25",
                    endDate = "2025-10-28",
                    priority = UnifiedPriority.NORMAL,
                    status = UnifiedStatus.TODO,
                    tags = listOf("문서", "개발"),
                    notionUrl = "https://notion.so/api-docs",
                    databaseName = "프로젝트 작업"
                )
            )
        }

        // 날짜순 정렬
        emit(items.sortedBy { it.startDate })
    }

    override suspend fun getItemsBySource(
        source: ItemSource,
        startDate: String,
        endDate: String
    ): Flow<List<UnifiedItem>> = getAllItems(startDate, endDate, listOf(source))

    override suspend fun getSyncStatus(source: ItemSource): SyncStatus {
        return SyncStatus(
            source = source,
            lastSyncTime = "2025-10-26 14:30",
            isSyncing = false,
            error = null
        )
    }

    override suspend fun syncSource(source: ItemSource): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}