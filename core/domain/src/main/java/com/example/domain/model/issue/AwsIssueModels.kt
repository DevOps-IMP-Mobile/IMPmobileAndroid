package com.example.domain.model.issue

/**
 * AWS 서버용 이슈 정보
 */
data class AwsIssue(
    val id: Long,
    val title: String,
    val description: String,
    val type: AwsIssueType,
    val status: AwsIssueStatus,
    val priority: AwsIssuePriority,
    val importance: AwsIssueImportance,
    val assigneeId: String,
    val assigneeName: String,
    val reporterId: String,
    val reporterName: String,
    val createdDate: String,
    val dueDate: String,
    val updatedDate: String?,
    val repository: String,
    val projectNo: String
)

/**
 * AWS 이슈 생성/수정 요청
 */
data class AwsIssueRequest(
    val title: String,
    val description: String,
    val type: AwsIssueType,
    val status: AwsIssueStatus,
    val priority: AwsIssuePriority,
    val importance: AwsIssueImportance,
    val assigneeId: String,
    val assigneeName: String,
    val reporterId: String,
    val reporterName: String,
    val dueDate: String,
    val repository: String,
    val projectNo: String
)

/**
 * AWS 이슈 타입 (서버 문서 기준)
 */
enum class AwsIssueType(val typeName: String) {
    BUG("BUG"),
    FEATURE("FEATURE"),
    TASK("TASK"),
    IMPROVEMENT("IMPROVEMENT");

    companion object {
        fun fromString(value: String): AwsIssueType {
            return values().find { it.typeName == value } ?: BUG
        }
    }
}

/**
 * AWS 이슈 상태
 */
enum class AwsIssueStatus(val displayName: String, val colorHex: String) {
    REGISTERED("등록", "#2196F3"),
    IN_PROGRESS("진행중", "#FF9800"),
    RESOLVED("해결", "#4CAF50"),
    CLOSED("완료", "#9E9E9E");

    companion object {
        fun fromString(value: String): AwsIssueStatus {
            return values().find { it.name == value } ?: REGISTERED
        }
    }
}

/**
 * AWS 이슈 우선순위
 */
enum class AwsIssuePriority(val displayName: String, val colorHex: String) {
    CRITICAL("긴급", "#F44336"),
    HIGH("높음", "#FF9800"),
    NORMAL("보통", "#2196F3"),
    LOW("낮음", "#4CAF50");

    companion object {
        fun fromString(value: String): AwsIssuePriority {
            return values().find { it.name == value } ?: NORMAL
        }
    }
}

/**
 * AWS 이슈 중요도
 */
enum class AwsIssueImportance(val displayName: String, val colorHex: String) {
    CRITICAL("심각", "#F44336"),
    HIGH("높음", "#FF9800"),
    NORMAL("보통", "#4CAF50"),
    LOW("낮음", "#9E9E9E");

    companion object {
        fun fromString(value: String): AwsIssueImportance {
            return values().find { it.name == value } ?: NORMAL
        }
    }
}