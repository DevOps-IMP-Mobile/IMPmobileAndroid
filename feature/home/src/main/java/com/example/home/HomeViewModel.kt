package com.example.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.home.GetDashboardDataUseCase
import com.example.domain.usecase.issue.GetIssueListUseCase
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getIssueListUseCase: GetIssueListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadDashboard)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDashboard -> loadDashboard()
            is HomeIntent.RefreshDashboard -> refreshDashboard()
            is HomeIntent.SelectProject -> selectProject(intent.project)
            is HomeIntent.NavigateToTaskDetail -> {
                sendEffect(HomeEffect.NavigateToTaskList)
            }
            is HomeIntent.NavigateToStatusDetail -> {
                sendEffect(HomeEffect.NavigateToStatusChart)
            }
            is HomeIntent.NavigateToTypeDetail -> {
                sendEffect(HomeEffect.NavigateToTypeChart)
            }
            // 새로 추가: Drawer 처리
            is HomeIntent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
            }
            is HomeIntent.CloseDrawer -> {
                _state.update { it.copy(isDrawerOpen = false) }
            }
            // 새로 추가: BottomSheet 처리
            is HomeIntent.OpenRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = true) }
                handleIntent(HomeIntent.LoadRecentIssues)
            }
            is HomeIntent.CloseRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = false) }
            }
            is HomeIntent.LoadRecentIssues -> loadRecentIssues()
        }
    }

    private fun selectProject(project: com.example.domain.model.home.Project) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "프로젝트 선택: ${project.projectName} (${project.projectNo})")
            _state.update {
                it.copy(
                    dashboardData = it.dashboardData.copy(selectedProject = project),
                    isDrawerOpen = false // 프로젝트 선택 후 Drawer 닫기
                )
            }
            loadDashboardWithSelectedProject(project)
        }
    }

    private fun loadDashboard() {
        loadDashboardWithSelectedProject(_state.value.dashboardData.selectedProject)
    }

    private fun loadDashboardWithSelectedProject(selectedProject: com.example.domain.model.home.Project?) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "대시보드 로드 - 선택된 프로젝트: ${selectedProject?.projectName} (${selectedProject?.projectNo})")
            _state.update { it.copy(isLoading = true, error = null) }

            getDashboardDataUseCase(selectedProject)
                .catch { throwable ->
                    Log.e("HomeViewModel", "UseCase 에러: ${throwable.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                    sendEffect(HomeEffect.ShowError(throwable.message ?: "오류가 발생했습니다."))
                }
                .collect { dashboardData ->
                    Log.d("HomeViewModel", "데이터 수신 - 프로젝트: ${dashboardData.selectedProject?.projectName}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dashboardData = dashboardData.copy(selectedProject = selectedProject),
                            error = null
                        )
                    }
                }
        }
    }

    // 새로 추가: 최근 2주 이슈 로드
    private fun loadRecentIssues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRecentIssues = true) }

            val selectedProject = _state.value.dashboardData.selectedProject
            if (selectedProject == null) {
                Log.w("HomeViewModel", "프로젝트가 선택되지 않음")
                _state.update {
                    it.copy(
                        isLoadingRecentIssues = false,
                        recentIssues = emptyList()
                    )
                }
                return@launch
            }

            try {
                val filter = IssueFilter(projectId = selectedProject.projectNo)
                getIssueListUseCase(filter, IssueSortType.CREATED_DATE)
                    .catch { throwable ->
                        Log.e("HomeViewModel", "최근 이슈 로드 실패: ${throwable.message}")
                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = emptyList()
                            )
                        }
                    }
                    .collect { issues ->
                        // 최근 2주 필터링
                        val twoWeeksAgo = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -14)
                        }.time

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val filteredIssues = issues.filter { issue ->
                            try {
                                val issueDate = dateFormat.parse(issue.createdDate.take(10))
                                issueDate != null && issueDate.after(twoWeeksAgo)
                            } catch (e: Exception) {
                                false
                            }
                        }

                        Log.d("HomeViewModel", "최근 2주 이슈: ${filteredIssues.size}개")
                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = filteredIssues
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "최근 이슈 로드 예외: ${e.message}")
                _state.update {
                    it.copy(
                        isLoadingRecentIssues = false,
                        recentIssues = emptyList()
                    )
                }
            }
        }
    }

    private fun refreshDashboard() {
        loadDashboard()
        sendEffect(HomeEffect.ShowRefreshComplete)
    }

    private fun sendEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}