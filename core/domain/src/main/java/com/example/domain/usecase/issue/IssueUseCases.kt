package com.example.domain.usecase.issue

import com.example.domain.model.issue.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * 이슈 목록을 가져오는 UseCase
 */
class GetIssueListUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(
        filter: IssueFilter = IssueFilter(),
        sortType: IssueSortType = IssueSortType.PRIORITY
    ): Flow<List<Issue>> = flow {
        // 로딩 상태를 위한 지연
        delay(800)
        
        // Mock 데이터 생성 (프로토타입 데이터 기반)
        val mockIssues = listOf(
            Issue(
                id = "aaaaa",
                title = "aaaaa",
                description = "aaaaaaaaa",
                type = IssueType.REVIEW,
                status = IssueStatus.REGISTERED,
                priority = IssuePriority.CRITICAL,
                importance = IssueImportance.CRITICAL,
                assigneeId = "demo_user_1",
                assigneeName = "데모서용자1",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-06-17",
                dueDate = "2025-06-17",
                updatedDate = null,
                repository = "module3",
                projectId = "demo"
            ),
            Issue(
                id = "issue2",
                title = "이슈2",
                description = "이슈2 설명",
                type = IssueType.TASK,
                status = IssueStatus.REGISTERED,
                priority = IssuePriority.CRITICAL,
                importance = IssueImportance.HIGH,
                assigneeId = "demo_user",
                assigneeName = "기본사용자",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-05-27",
                dueDate = "2025-05-27",
                repository = "module1",
                projectId = "demo"
            ),
            Issue(
                id = "issue1",
                title = "이슈1",
                description = "이슈1 설명",
                type = IssueType.BUG,
                status = IssueStatus.IN_PROGRESS,
                priority = IssuePriority.CRITICAL,
                importance = IssueImportance.CRITICAL,
                assigneeId = "demo_user",
                assigneeName = "기본사용자",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-05-27",
                dueDate = "2025-05-27",
                repository = "module2",
                projectId = "demo"
            ),
            Issue(
                id = "mass_action",
                title = "질량 조치",
                description = "질량 조치 관련 이슈",
                type = IssueType.TASK,
                status = IssueStatus.IN_PROGRESS,
                priority = IssuePriority.HIGH,
                importance = IssueImportance.NORMAL,
                assigneeId = "demo_user",
                assigneeName = "기본사용자",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-06-19",
                dueDate = "2025-06-19",
                repository = "module1",
                projectId = "demo"
            ),
            Issue(
                id = "bug_fix",
                title = "버그",
                description = "버그 수정 필요",
                type = IssueType.BUG,
                status = IssueStatus.REGISTERED,
                priority = IssuePriority.NORMAL,
                importance = IssueImportance.NORMAL,
                assigneeId = "demo_user",
                assigneeName = "기본사용자",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-05-31",
                dueDate = "2025-05-31",
                repository = "module3",
                projectId = "demo"
            )
        )
        
        // 필터링 적용
        var filteredIssues = mockIssues
        
        filter.projectId?.let { projectId ->
            filteredIssues = filteredIssues.filter { it.projectId == projectId }
        }
        
        filter.status?.let { status ->
            filteredIssues = filteredIssues.filter { it.status == status }
        }
        
        if (filter.searchQuery.isNotEmpty()) {
            filteredIssues = filteredIssues.filter { 
                it.title.contains(filter.searchQuery, ignoreCase = true) ||
                it.description.contains(filter.searchQuery, ignoreCase = true)
            }
        }
        
        // 정렬 적용
        val sortedIssues = when (sortType) {
            IssueSortType.PRIORITY -> filteredIssues.sortedBy { it.priority.ordinal }
            IssueSortType.DUE_DATE -> filteredIssues.sortedBy { it.dueDate }
            IssueSortType.IMPORTANCE -> filteredIssues.sortedBy { it.importance.ordinal }
            IssueSortType.CREATED_DATE -> filteredIssues.sortedByDescending { it.createdDate }
        }
        
        emit(sortedIssues)
    }
}

/**
 * 프로젝트 목록을 가져오는 UseCase (이슈 화면용)
 */
class GetProjectsForIssueUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(): Flow<List<Pair<String, String>>> = flow {
        delay(300)
        
        // Mock 프로젝트 데이터 (id, name)
        val mockProjects = listOf(
            "demo" to "데모 프로젝트",
            "demo2" to "데모 프로젝트2",
            "test124" to "test124"
        )
        
        emit(mockProjects)
    }
}