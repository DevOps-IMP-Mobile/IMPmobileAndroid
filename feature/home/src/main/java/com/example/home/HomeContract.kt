package com.example.home

import com.example.domain.base.UiIntent
import com.example.domain.base.UiState
import com.example.domain.base.UiEffect
import com.example.domain.model.home.DashboardData
import com.example.domain.model.issue.Issue

/**
 * Home 화면의 Intent (사용자 액션)
 */
sealed class HomeIntent : UiIntent {
    object LoadDashboard : HomeIntent()
    object RefreshDashboard : HomeIntent()
    object NavigateToTaskDetail : HomeIntent()
    object NavigateToStatusDetail : HomeIntent()
    object NavigateToTypeDetail : HomeIntent()
    data class SelectProject(val project: com.example.domain.model.home.Project) : HomeIntent()

    // 새로 추가: Drawer 관련
    object OpenDrawer : HomeIntent()
    object CloseDrawer : HomeIntent()

    // 새로 추가: 최근 이슈 BottomSheet 관련
    object OpenRecentIssuesSheet : HomeIntent()
    object CloseRecentIssuesSheet : HomeIntent()
    object LoadRecentIssues : HomeIntent()
}

/**
 * Home 화면의 State (UI 상태)
 */
data class HomeState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardData = DashboardData(),
    val error: String? = null,

    // 새로 추가: Drawer 상태
    val isDrawerOpen: Boolean = false,

    // 새로 추가: BottomSheet 상태
    val isRecentIssuesSheetOpen: Boolean = false,
    val recentIssues: List<Issue> = emptyList(),
    val isLoadingRecentIssues: Boolean = false
) : UiState

/**
 * Home 화면의 Effect (일회성 이벤트)
 */
sealed class HomeEffect : UiEffect {
    object NavigateToTaskList : HomeEffect()
    object NavigateToStatusChart : HomeEffect()
    object NavigateToTypeChart : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
    object ShowRefreshComplete : HomeEffect()
}