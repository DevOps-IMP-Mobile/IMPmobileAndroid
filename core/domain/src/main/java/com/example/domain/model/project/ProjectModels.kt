package com.example.domain.model.project

/**
 * 프로젝트 정보
 */
data class Project(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val status: ProjectStatus,
    val memberCount: Int,
    val managerId: String,
    val managerName: String,
    val isStandard: Boolean = false
)

/**
 * 프로젝트 상태
 */
enum class ProjectStatus(val displayName: String, val colorHex: String) {
    RUNNING("실행중", "#4CAF50"),
    COMPLETED("완료", "#FF9800"),
    PAUSED("대기", "#9E9E9E")
}

/**
 * 프로젝트 팀 멤버
 */
data class ProjectMember(
    val id: String,
    val name: String,
    val userId: String,
    val position: String,  // 직책 (관리자, 사원)
    val role: String       // 권한 (프로젝트관리자, 팀원)
)