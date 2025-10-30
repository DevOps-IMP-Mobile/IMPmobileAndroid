package com.example.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.home.GetDashboardDataUseCase
import com.example.domain.usecase.issue.GetIssueListUseCase
import com.example.domain.usecase.unified.GetUnifiedItemsUseCase
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.unified.ItemSource
import com.example.data.local.CompletedIssuesManager  // ✅ 추가
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
    private val getIssueListUseCase: GetIssueListUseCase,
    private val getUnifiedItemsUseCase: GetUnifiedItemsUseCase,
    private val completedIssuesManager: CompletedIssuesManager  // ✅ 추가
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
            is HomeIntent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
            }
            is HomeIntent.CloseDrawer -> {
                _state.update { it.copy(isDrawerOpen = false) }
            }
            is HomeIntent.OpenRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = true) }
                handleIntent(HomeIntent.LoadRecentIssues)
            }
            is HomeIntent.CloseRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = false) }
            }
            is HomeIntent.LoadRecentIssues -> loadRecentIssues()
            is HomeIntent.OpenUnifiedSheet -> {
                _state.update { it.copy(isUnifiedSheetOpen = true) }
                handleIntent(HomeIntent.LoadUnifiedItems)
            }
            is HomeIntent.CloseUnifiedSheet -> {
                _state.update { it.copy(isUnifiedSheetOpen = false) }
            }
            is HomeIntent.LoadUnifiedItems -> loadUnifiedItems()
            is HomeIntent.ToggleSource -> toggleSource(intent.source)

            // 상태 변경 관련
            is HomeIntent.SelectIssueForStatusChange -> {
                _state.update {
                    it.copy(
                        selectedIssue = intent.issue,
                        showStatusChangeDialog = true
                    )
                }
            }
            is HomeIntent.CancelStatusChange -> {
                _state.update {
                    it.copy(
                        selectedIssue = null,
                        showStatusChangeDialog = false
                    )
                }
            }
            is HomeIntent.UpdateIssueStatus -> {
                updateIssueStatus(intent.issue, intent.newStatusCode)
            }
            is HomeIntent.ConfirmCompleteIssue -> {
                _state.update {
                    it.copy(
                        selectedIssue = intent.issue,
                        showStatusChangeDialog = false,
                        showCompleteConfirmDialog = true
                    )
                }
            }
            is HomeIntent.CancelCompleteIssue -> {
                _state.update {
                    it.copy(
                        selectedIssue = null,
                        showCompleteConfirmDialog = false
                    )
                }
            }
            is HomeIntent.CompleteIssue -> {
                completeIssue(intent.issue)
            }
        }
    }

    private fun selectProject(project: com.example.domain.model.home.Project) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    dashboardData = it.dashboardData.copy(selectedProject = project),
                    isDrawerOpen = false
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
            _state.update { it.copy(isLoading = true, error = null) }

            getDashboardDataUseCase(selectedProject)
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                    sendEffect(HomeEffect.ShowError(throwable.message ?: "오류가 발생했습니다."))
                }
                .collect { dashboardData ->
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

    private fun loadRecentIssues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRecentIssues = true) }

            val selectedProject = _state.value.dashboardData.selectedProject
            if (selectedProject == null) {
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
                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = emptyList()
                            )
                        }
                    }
                    .collect { issues ->
                        val twoWeeksAgo = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -14)
                        }.time

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val filteredIssues = issues.filter { issue ->
                            try {
                                val issueDate = dateFormat.parse(issue.createdDate.take(10))
                                val isRecent = issueDate != null && issueDate.after(twoWeeksAgo)
                                val isNotCompleted = !completedIssuesManager.isCompleted(issue.id)  // ✅ 완료한 이슈 필터링
                                isRecent && isNotCompleted
                            } catch (e: Exception) {
                                false
                            }
                        }

                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = filteredIssues
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingRecentIssues = false,
                        recentIssues = emptyList()
                    )
                }
            }
        }
    }

    private fun loadUnifiedItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingUnifiedItems = true) }

            try {
                val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val startDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -14)
                }.time.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }

                getUnifiedItemsUseCase(
                    startDate = startDate,
                    endDate = endDate,
                    sources = _state.value.selectedSources
                )
                    .catch { throwable ->
                        _state.update {
                            it.copy(
                                isLoadingUnifiedItems = false,
                                unifiedItems = emptyList()
                            )
                        }
                    }
                    .collect { items ->
                        _state.update {
                            it.copy(
                                isLoadingUnifiedItems = false,
                                unifiedItems = items
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingUnifiedItems = false,
                        unifiedItems = emptyList()
                    )
                }
            }
        }
    }

    private fun toggleSource(source: ItemSource) {
        val currentSources = _state.value.selectedSources.toMutableList()
        if (source in currentSources) {
            currentSources.remove(source)
        } else {
            currentSources.add(source)
        }
        _state.update { it.copy(selectedSources = currentSources) }
        loadUnifiedItems()
    }

    // ✅ 상태 변경 (UI만)
    private fun updateIssueStatus(issue: com.example.domain.model.issue.Issue, newStatusCode: String) {
        viewModelScope.launch {
            val newStatus = mapStatusCode(newStatusCode)

            // 업데이트된 이슈 객체 생성
            val updatedIssue = issue.copy(status = newStatus)

            // UI에서만 상태 변경
            val updatedIssues = _state.value.recentIssues.map { currentIssue ->
                if (currentIssue.id == issue.id) {
                    updatedIssue
                } else {
                    currentIssue
                }
            }

            // recentIssues 업데이트 및 다이얼로그 닫기
            _state.update {
                it.copy(
                    recentIssues = updatedIssues,
                    showStatusChangeDialog = false,  // ✅ 다이얼로그 닫기
                    selectedIssue = null  // ✅ 선택 해제
                )
            }

            sendEffect(HomeEffect.ShowStatusUpdateSuccess("상태가 변경되었습니다"))
        }
    }

    // ✅ 완료 처리 (로컬 저장 + UI 제거)
    private fun completeIssue(issue: com.example.domain.model.issue.Issue) {
        viewModelScope.launch {
            // 다이얼로그 닫기
            _state.update {
                it.copy(
                    showCompleteConfirmDialog = false,
                    selectedIssue = null
                )
            }

            // 완료한 이슈 ID를 로컬에 저장
            completedIssuesManager.addCompletedIssue(issue.id)

            // UI에서 제거
            val updatedIssues = _state.value.recentIssues.filter { it.id != issue.id }
            _state.update {
                it.copy(recentIssues = updatedIssues)
            }

            sendEffect(HomeEffect.ShowStatusUpdateSuccess("이슈가 완료되었습니다"))
        }
    }

    private fun mapStatusCode(code: String): com.example.domain.model.issue.IssueStatus {
        return when (code) {
            "REGISTERED" -> com.example.domain.model.issue.IssueStatus.REGISTERED
            "IN_PROGRESS" -> com.example.domain.model.issue.IssueStatus.IN_PROGRESS
            "RESOLVED" -> com.example.domain.model.issue.IssueStatus.RESOLVED
            "CLOSED" -> com.example.domain.model.issue.IssueStatus.CLOSED
            else -> com.example.domain.model.issue.IssueStatus.REGISTERED
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