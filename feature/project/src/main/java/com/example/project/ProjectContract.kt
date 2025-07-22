package com.example.project

import com.example.domain.base.UiIntent
import com.example.domain.base.UiState
import com.example.domain.base.UiEffect
import com.example.domain.model.project.Project
import com.example.domain.model.project.ProjectMember

/**
 * Project 화면의 Intent (사용자 액션)
 */
sealed class ProjectIntent : UiIntent {
    object LoadProjects : ProjectIntent()
    object RefreshProjects : ProjectIntent()
    data class SelectProject(val project: Project) : ProjectIntent()
    object BackToProjectList : ProjectIntent()
}

/**
 * Project 화면의 State (UI 상태)
 */
data class ProjectState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val selectedProject: Project? = null,
    val projectMembers: List<ProjectMember> = emptyList(),
    val isLoadingMembers: Boolean = false,
    val error: String? = null,
    val currentScreen: ProjectScreenType = ProjectScreenType.PROJECT_LIST
) : UiState

/**
 * Project 화면의 Effect (일회성 이벤트)
 */
sealed class ProjectEffect : UiEffect {
    data class ShowError(val message: String) : ProjectEffect()
    object ShowRefreshComplete : ProjectEffect()
}

/**
 * Project 화면 타입
 */
enum class ProjectScreenType {
    PROJECT_LIST,     // 프로젝트 목록
    PROJECT_DETAIL    // 프로젝트 상세
}