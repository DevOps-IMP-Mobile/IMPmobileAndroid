package com.example.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.issue.IssueStatus
import com.example.domain.usecase.issue.GetIssueListUseCase
import com.example.domain.usecase.issue.GetProjectsForIssueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val getIssueListUseCase: GetIssueListUseCase,
    private val getProjectsForIssueUseCase: GetProjectsForIssueUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(IssueState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<IssueEffect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()
    
    init {
        handleIntent(IssueIntent.LoadProjects)
        handleIntent(IssueIntent.LoadIssues)
    }
    
    fun handleIntent(intent: IssueIntent) {
        when (intent) {
            is IssueIntent.LoadIssues -> loadIssues()
            is IssueIntent.LoadProjects -> loadProjects()
            is IssueIntent.RefreshIssues -> refreshIssues()
            is IssueIntent.SelectProject -> selectProject(intent.projectId)
            is IssueIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is IssueIntent.FilterByStatus -> filterByStatus(intent.status)
            is IssueIntent.ChangeSortType -> changeSortType(intent.sortType)
            is IssueIntent.SelectIssue -> selectIssue(intent.issue)
            is IssueIntent.BackToIssueList -> backToIssueList()
            is IssueIntent.ApproveIssue -> approveIssue(intent.issueId)
            is IssueIntent.RejectIssue -> rejectIssue(intent.issueId)
        }
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            getProjectsForIssueUseCase()
                .catch { throwable ->
                    sendEffect(IssueEffect.ShowError(throwable.message ?: "프로젝트 로드 실패"))
                }
                .collect { projects ->
                    _state.update { it.copy(projects = projects) }
                    
                    // 첫 번째 프로젝트를 기본 선택
                    if (projects.isNotEmpty() && _state.value.selectedProjectId == null) {
                        _state.update { 
                            it.copy(
                                selectedProjectId = projects.first().first,
                                filter = it.filter.copy(projectId = projects.first().first)
                            ) 
                        }
                        loadIssues()
                    }
                }
        }
    }
    
    private fun loadIssues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val currentState = _state.value
            getIssueListUseCase(currentState.filter, currentState.sortType)
                .catch { throwable ->
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = throwable.message ?: "이슈 로드 실패"
                        ) 
                    }
                    sendEffect(IssueEffect.ShowError(throwable.message ?: "오류가 발생했습니다."))
                }
                .collect { issues ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            issues = issues,
                            error = null
                        )
                    }
                }
        }
    }
    
    private fun refreshIssues() {
        loadIssues()
        sendEffect(IssueEffect.ShowRefreshComplete)
    }
    
    private fun selectProject(projectId: String) {
        _state.update { 
            it.copy(
                selectedProjectId = projectId,
                filter = it.filter.copy(projectId = projectId)
            ) 
        }
        loadIssues()
    }
    
    private fun updateSearchQuery(query: String) {
        _state.update { 
            it.copy(
                searchQuery = query,
                filter = it.filter.copy(searchQuery = query)
            ) 
        }
        loadIssues()
    }
    
    private fun filterByStatus(status: IssueStatus?) {
        _state.update { 
            it.copy(filter = it.filter.copy(status = status)) 
        }
        loadIssues()
    }
    
    private fun changeSortType(sortType: IssueSortType) {
        _state.update { it.copy(sortType = sortType) }
        loadIssues()
    }
    
    private fun selectIssue(issue: com.example.domain.model.issue.Issue) {
        _state.update {
            it.copy(
                selectedIssue = issue,
                currentScreen = IssueScreenType.ISSUE_DETAIL
            )
        }
    }
    
    private fun backToIssueList() {
        _state.update {
            it.copy(
                currentScreen = IssueScreenType.ISSUE_LIST,
                selectedIssue = null
            )
        }
    }
    
    private fun approveIssue(issueId: String) {
        // TODO: 실제 승인 API 호출
        val issue = _state.value.selectedIssue
        if (issue != null) {
            sendEffect(IssueEffect.ShowApprovalSuccess(issue.title))
            // 목록으로 돌아가기
            backToIssueList()
            // 목록 새로고침
            loadIssues()
        }
    }
    
    private fun rejectIssue(issueId: String) {
        // TODO: 실제 반려 API 호출
        val issue = _state.value.selectedIssue
        if (issue != null) {
            sendEffect(IssueEffect.ShowRejectionSuccess(issue.title))
            // 목록으로 돌아가기
            backToIssueList()
            // 목록 새로고침
            loadIssues()
        }
    }
    
    private fun sendEffect(effect: IssueEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}