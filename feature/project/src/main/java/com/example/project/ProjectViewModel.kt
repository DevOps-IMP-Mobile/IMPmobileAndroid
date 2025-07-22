package com.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.project.GetProjectListUseCase
import com.example.domain.usecase.project.GetProjectMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val getProjectListUseCase: GetProjectListUseCase,
    private val getProjectMembersUseCase: GetProjectMembersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProjectState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<ProjectEffect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()
    
    init {
        handleIntent(ProjectIntent.LoadProjects)
    }
    
    fun handleIntent(intent: ProjectIntent) {
        when (intent) {
            is ProjectIntent.LoadProjects -> loadProjects()
            is ProjectIntent.RefreshProjects -> refreshProjects()
            is ProjectIntent.SelectProject -> selectProject(intent.project)
            is ProjectIntent.BackToProjectList -> backToProjectList()
        }
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getProjectListUseCase()
                .catch { throwable ->
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                        ) 
                    }
                    sendEffect(ProjectEffect.ShowError(throwable.message ?: "오류가 발생했습니다."))
                }
                .collect { projects ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            projects = projects,
                            error = null
                        )
                    }
                }
        }
    }
    
    private fun refreshProjects() {
        loadProjects()
        sendEffect(ProjectEffect.ShowRefreshComplete)
    }
    
    private fun selectProject(project: com.example.domain.model.project.Project) {
        _state.update {
            it.copy(
                selectedProject = project,
                currentScreen = ProjectScreenType.PROJECT_DETAIL,
                isLoadingMembers = true
            )
        }
        
        // 프로젝트 멤버 로드
        loadProjectMembers(project.id)
    }
    
    private fun loadProjectMembers(projectId: String) {
        viewModelScope.launch {
            getProjectMembersUseCase(projectId)
                .catch { throwable ->
                    _state.update { 
                        it.copy(
                            isLoadingMembers = false,
                            error = throwable.message
                        ) 
                    }
                }
                .collect { members ->
                    _state.update {
                        it.copy(
                            isLoadingMembers = false,
                            projectMembers = members,
                            error = null
                        )
                    }
                }
        }
    }
    
    private fun backToProjectList() {
        _state.update {
            it.copy(
                currentScreen = ProjectScreenType.PROJECT_LIST,
                selectedProject = null,
                projectMembers = emptyList()
            )
        }
    }
    
    private fun sendEffect(effect: ProjectEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}