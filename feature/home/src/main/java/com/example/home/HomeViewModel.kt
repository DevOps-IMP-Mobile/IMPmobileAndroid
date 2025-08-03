package com.example.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.home.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
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
        }
    }
    
    private fun selectProject(project: com.example.domain.model.home.Project) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "프로젝트 선택: ${project.projectName} (${project.projectNo})")
            _state.update { 
                it.copy(
                    dashboardData = it.dashboardData.copy(selectedProject = project)
                ) 
            }
            // 프로젝트 변경 시 대시보드 데이터 새로 로드 (선택된 프로젝트 유지)
            loadDashboardWithSelectedProject(project)
        }
    }
    
    private fun loadDashboard() {
        loadDashboardWithSelectedProject(_state.value.dashboardData.selectedProject ?: null)
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
                    Log.d("HomeViewModel", "데이터 수신 - 프로젝트 개수: ${dashboardData.projects.size}")
                    Log.d("HomeViewModel", "데이터 수신 - 오늘의 할일: 진행=${dashboardData.todayTasks.inProgress}, 지연=${dashboardData.todayTasks.delayed}, 등록=${dashboardData.todayTasks.registered}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dashboardData = dashboardData.copy(selectedProject = selectedProject),
                            error = null
                        )
                    }
                    Log.d("HomeViewModel", "상태 업데이트 완료")
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