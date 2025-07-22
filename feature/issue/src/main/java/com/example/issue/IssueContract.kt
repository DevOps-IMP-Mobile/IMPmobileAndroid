package com.example.issue

import com.example.domain.base.UiIntent
import com.example.domain.base.UiState
import com.example.domain.base.UiEffect
import com.example.domain.model.issue.Issue
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.issue.IssueStatus

/**
 * Issue 화면의 Intent (사용자 액션)
 */
sealed class IssueIntent : UiIntent {
    object LoadIssues : IssueIntent()
    object LoadProjects : IssueIntent()
    object RefreshIssues : IssueIntent()
    data class SelectProject(val projectId: String) : IssueIntent()
    data class UpdateSearchQuery(val query: String) : IssueIntent()
    data class FilterByStatus(val status: IssueStatus?) : IssueIntent()
    data class ChangeSortType(val sortType: IssueSortType) : IssueIntent()
    data class SelectIssue(val issue: Issue) : IssueIntent()
    object BackToIssueList : IssueIntent()
    data class ApproveIssue(val issueId: String) : IssueIntent()
    data class RejectIssue(val issueId: String) : IssueIntent()
}

/**
 * Issue 화면의 State (UI 상태)
 */
data class IssueState(
    val isLoading: Boolean = false,
    val issues: List<Issue> = emptyList(),
    val projects: List<Pair<String, String>> = emptyList(), // (id, name)
    val selectedProjectId: String? = null,
    val selectedIssue: Issue? = null,
    val filter: IssueFilter = IssueFilter(),
    val sortType: IssueSortType = IssueSortType.PRIORITY,
    val searchQuery: String = "",
    val currentScreen: IssueScreenType = IssueScreenType.ISSUE_LIST,
    val error: String? = null
) : UiState

/**
 * Issue 화면의 Effect (일회성 이벤트)
 */
sealed class IssueEffect : UiEffect {
    data class ShowError(val message: String) : IssueEffect()
    object ShowRefreshComplete : IssueEffect()
    data class ShowApprovalSuccess(val issueTitle: String) : IssueEffect()
    data class ShowRejectionSuccess(val issueTitle: String) : IssueEffect()
}

/**
 * Issue 화면 타입
 */
enum class IssueScreenType {
    ISSUE_LIST,    // 이슈 목록
    ISSUE_DETAIL   // 이슈 상세
}