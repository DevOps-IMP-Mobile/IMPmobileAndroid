package com.example.home

import com.example.domain.base.UiIntent
import com.example.domain.base.UiState
import com.example.domain.base.UiEffect
import com.example.domain.model.home.DashboardData

/**
 * Home 화면의 Intent (사용자 액션)
 */
sealed class HomeIntent : UiIntent {
    object LoadDashboard : HomeIntent()
    object RefreshDashboard : HomeIntent()
    object NavigateToTaskDetail : HomeIntent()
    object NavigateToStatusDetail : HomeIntent()
    object NavigateToTypeDetail : HomeIntent()
}

/**
 * Home 화면의 State (UI 상태)
 */
data class HomeState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardData = DashboardData(),
    val error: String? = null
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