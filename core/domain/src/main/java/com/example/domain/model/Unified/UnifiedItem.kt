// core/domain/src/main/java/com/example/domain/model/unified/UnifiedItem.kt
package com.example.domain.model.unified

import java.time.LocalDateTime

/**
 * 이슈, 구글 캘린더, 노션을 통합하는 공통 인터페이스
 */
sealed class UnifiedItem {
    abstract val id: String
    abstract val title: String
    abstract val description: String
    abstract val startDate: String
    abstract val endDate: String
    abstract val source: ItemSource
    abstract val priority: UnifiedPriority
    abstract val status: UnifiedStatus
    abstract val tags: List<String>

    // 앱 이슈
    data class AppIssue(
        override val id: String,
        override val title: String,
        override val description: String,
        override val startDate: String,
        override val endDate: String,
        override val priority: UnifiedPriority,
        override val status: UnifiedStatus,
        override val tags: List<String>,
        val issueType: String,
        val assignee: String?
    ) : UnifiedItem() {
        override val source = ItemSource.APP_ISSUE
    }

    // 구글 캘린더 일정
    data class GoogleEvent(
        override val id: String,
        override val title: String,
        override val description: String,
        override val startDate: String,
        override val endDate: String,
        override val priority: UnifiedPriority,
        override val status: UnifiedStatus,
        override val tags: List<String>,
        val location: String?,
        val attendees: List<String>,
        val meetingLink: String?
    ) : UnifiedItem() {
        override val source = ItemSource.GOOGLE_CALENDAR
    }

    // 노션 페이지/작업
    data class NotionTask(
        override val id: String,
        override val title: String,
        override val description: String,
        override val startDate: String,
        override val endDate: String,
        override val priority: UnifiedPriority,
        override val status: UnifiedStatus,
        override val tags: List<String>,
        val notionUrl: String,
        val databaseName: String
    ) : UnifiedItem() {
        override val source = ItemSource.NOTION
    }
}

enum class ItemSource(val displayName: String, val colorHex: String) {
    APP_ISSUE("앱 이슈", "#6366F1"),
    GOOGLE_CALENDAR("구글 캘린더", "#4285F4"),
    NOTION("노션", "#000000")
}

enum class UnifiedPriority(val displayName: String, val colorHex: String) {
    CRITICAL("긴급", "#F44336"),
    HIGH("높음", "#FF9800"),
    NORMAL("보통", "#2196F3"),
    LOW("낮음", "#4CAF50")
}

enum class UnifiedStatus(val displayName: String, val colorHex: String) {
    TODO("할 일", "#FF9800"),
    IN_PROGRESS("진행중", "#2196F3"),
    DONE("완료", "#4CAF50"),
    CANCELLED("취소", "#9E9E9E")
}