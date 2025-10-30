package com.example.home

import com.example.domain.base.UiIntent
import com.example.domain.base.UiState
import com.example.domain.base.UiEffect
import com.example.domain.model.home.DashboardData
import com.example.domain.model.issue.Issue
import com.example.domain.model.unified.UnifiedItem
import com.example.domain.model.unified.ItemSource

sealed class HomeIntent : UiIntent {
    object LoadDashboard : HomeIntent()
    object RefreshDashboard : HomeIntent()
    object NavigateToTaskDetail : HomeIntent()
    object NavigateToStatusDetail : HomeIntent()
    object NavigateToTypeDetail : HomeIntent()
    data class SelectProject(val project: com.example.domain.model.home.Project) : HomeIntent()

    object OpenDrawer : HomeIntent()
    object CloseDrawer : HomeIntent()

    object OpenRecentIssuesSheet : HomeIntent()
    object CloseRecentIssuesSheet : HomeIntent()
    object LoadRecentIssues : HomeIntent()

    object OpenUnifiedSheet : HomeIntent()
    object CloseUnifiedSheet : HomeIntent()
    object LoadUnifiedItems : HomeIntent()
    data class ToggleSource(val source: ItemSource) : HomeIntent()

    // 🆕 이슈 상태 관리
    data class SelectIssueForStatusChange(val issue: Issue) : HomeIntent()
    object CancelStatusChange : HomeIntent()
    data class UpdateIssueStatus(val issue: Issue, val newStatusCode: String) : HomeIntent()
    data class ConfirmCompleteIssue(val issue: Issue) : HomeIntent()
    object CancelCompleteIssue : HomeIntent()
    data class CompleteIssue(val issue: Issue) : HomeIntent()
}

data class HomeState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardData = DashboardData(),
    val error: String? = null,

    val isDrawerOpen: Boolean = false,

    val isRecentIssuesSheetOpen: Boolean = false,
    val recentIssues: List<Issue> = emptyList(),
    val isLoadingRecentIssues: Boolean = false,

    val isUnifiedSheetOpen: Boolean = false,
    val unifiedItems: List<UnifiedItem> = emptyList(),
    val isLoadingUnifiedItems: Boolean = false,
    val selectedSources: List<ItemSource> = ItemSource.values().toList(),

    // 🆕 상태 변경 관련
    val selectedIssue: Issue? = null,
    val showStatusChangeDialog: Boolean = false,
    val showCompleteConfirmDialog: Boolean = false,
    val isUpdatingStatus: Boolean = false
) : UiState

sealed class HomeEffect : UiEffect {
    object NavigateToTaskList : HomeEffect()
    object NavigateToStatusChart : HomeEffect()
    object NavigateToTypeChart : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
    object ShowRefreshComplete : HomeEffect()
    data class ShowStatusUpdateSuccess(val message: String) : HomeEffect()
}