package com.example.domain.model.issue

/**
 * 이슈 정보
 */
data class Issue(
    val id: String,
    val title: String,
    val description: String,
    val type: IssueType,
    val status: IssueStatus,
    val priority: IssuePriority,
    val importance: IssueImportance,
    val assigneeId: String,
    val assigneeName: String,
    val reporterId: String,
    val reporterName: String,
    val createdDate: String,
    val dueDate: String,
    val updatedDate: String? = null,
    val repository: String,
    val projectId: String
)

/**
 * 이슈 타입
 */
enum class IssueType(val displayName: String) {
    BUG("버그"),
    FEATURE("기능"),
    TASK("작업"),
    REVIEW("검토")
}

/**
 * 이슈 상태
 */
enum class IssueStatus(val displayName: String, val colorHex: String) {
    REGISTERED("등록", "#2196F3"),
    IN_PROGRESS("진행", "#FF9800"), 
    RESOLVED("해결", "#4CAF50"),
    CLOSED("완료", "#9E9E9E")
}

/**
 * 우선순위
 */
enum class IssuePriority(val displayName: String, val colorHex: String) {
    CRITICAL("긴급", "#F44336"),
    HIGH("높음", "#FF9800"),
    NORMAL("보통", "#2196F3"),
    LOW("낮음", "#4CAF50")
}

/**
 * 중요도
 */
enum class IssueImportance(val displayName: String, val colorHex: String) {
    CRITICAL("심각", "#F44336"),
    HIGH("높음", "#FF9800"),
    NORMAL("보통", "#4CAF50")
}

/**
 * 이슈 필터 정보
 */
data class IssueFilter(
    val projectId: String? = null,
    val status: IssueStatus? = null,
    val priority: IssuePriority? = null,
    val assigneeId: String? = null,
    val searchQuery: String = ""
)

/**
 * 이슈 정렬 타입
 */
enum class IssueSortType(val displayName: String) {
    PRIORITY("우선순위순"),
    DUE_DATE("마감일순"),
    IMPORTANCE("중요도순"),
    CREATED_DATE("등록일순")
}