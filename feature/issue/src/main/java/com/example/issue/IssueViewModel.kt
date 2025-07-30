package com.example.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.issue.IssueStatus
import com.example.domain.usecase.issue.GetIssueListUseCase
import com.example.domain.usecase.issue.GetProjectsForIssueUseCase
import com.example.domain.usecase.issue.CreateIssueUseCase
import com.example.domain.usecase.issue.UpdateIssueUseCase
import com.example.domain.usecase.issue.DeleteIssueUseCase
import com.example.domain.usecase.issue.GetIssueOptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val getIssueListUseCase: GetIssueListUseCase,
    private val getProjectsForIssueUseCase: GetProjectsForIssueUseCase,
    private val createIssueUseCase: CreateIssueUseCase,
    private val updateIssueUseCase: UpdateIssueUseCase,
    private val deleteIssueUseCase: DeleteIssueUseCase,
    private val getIssueOptionsUseCase: GetIssueOptionsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(IssueState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<IssueEffect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()
    
    // 검색 디바운싱을 위한 Flow
    private val _searchQuery = MutableStateFlow("")
    
    init {
        handleIntent(IssueIntent.LoadProjects)
        handleIntent(IssueIntent.LoadIssues)
        
        // 검색 쿼리 디바운싱 처리
        viewModelScope.launch {
            _searchQuery
                .debounce(500) // 500ms 디바운싱
                .distinctUntilChanged()
                .collect { query ->
                    // API 호출만 수행 (filter는 이미 업데이트됨)
                    loadIssues()
                }
        }
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
            
            // CRUD 관련
            is IssueIntent.NavigateToCreate -> navigateToCreate()
            is IssueIntent.NavigateToEdit -> navigateToEdit()
            is IssueIntent.BackFromCreate -> backFromCreate()
            is IssueIntent.BackFromEdit -> backFromEdit()
            is IssueIntent.CreateIssue -> createIssue(intent)
            is IssueIntent.UpdateIssue -> updateIssue(intent)
            is IssueIntent.DeleteIssue -> deleteIssue()
            is IssueIntent.LoadIssueOptions -> loadIssueOptions()
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
                            issues = issues,
                            isLoading = false
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
        _searchQuery.value = query
    }
    
    private fun filterByStatus(status: IssueStatus?) {
        _state.update { 
            it.copy(
                filter = it.filter.copy(status = status)
            ) 
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
                selectedIssue = null,
                currentScreen = IssueScreenType.ISSUE_LIST
            ) 
        }
    }
    
    // CRUD 관련 메서드들
    private fun navigateToCreate() {
        val currentState = _state.value
        _state.update { 
            it.copy(
                currentScreen = IssueScreenType.ISSUE_CREATE,
                createForm = IssueFormData()
            ) 
        }
        loadIssueOptions()
    }
    
    private fun navigateToEdit() {
        val currentState = _state.value
        val selectedIssue = currentState.selectedIssue
        if (selectedIssue != null) {
            // title이 비어있을 경우 기본값 설정
            val title = if (selectedIssue.title.isBlank()) {
                "제목 없음" // 기본값
            } else {
                selectedIssue.title
            }
            
            android.util.Log.d("IssueViewModel", "이슈 수정 화면 진입:")
            android.util.Log.d("IssueViewModel", "- 선택된 이슈 ID: ${selectedIssue.id}")
            android.util.Log.d("IssueViewModel", "- 원본 제목: '${selectedIssue.title}'")
            android.util.Log.d("IssueViewModel", "- 사용할 제목: '$title'")
            
            // 이슈 옵션을 먼저 로드한 후 실제 ID로 매핑
            viewModelScope.launch {
                val projectId = currentState.selectedProjectId ?: return@launch
                getIssueOptionsUseCase(projectId).onSuccess { options ->
                    // 이슈 타입 ID 매핑
                    val typeId = options.issueTypes.find { it.name == selectedIssue.type.displayName }?.id 
                        ?: when (selectedIssue.type) {
                            com.example.domain.model.issue.IssueType.BUG -> "20230626135333726323"
                            com.example.domain.model.issue.IssueType.FEATURE -> "20230626135406976598"
                            com.example.domain.model.issue.IssueType.TASK -> "20230626135504438951"
                            com.example.domain.model.issue.IssueType.REVIEW -> "20230626135504438951"
                        }
                    
                    // 우선순위 코드 매핑
                    val priorityCd = options.priorities.find { it.name == selectedIssue.priority.displayName }?.id
                        ?: when (selectedIssue.priority) {
                            com.example.domain.model.issue.IssuePriority.CRITICAL -> "001"
                            com.example.domain.model.issue.IssuePriority.HIGH -> "002"
                            com.example.domain.model.issue.IssuePriority.NORMAL -> "003"
                            com.example.domain.model.issue.IssuePriority.LOW -> "004"
                        }
                    
                    // 중요도 코드 매핑
                    val importanceCd = options.importances.find { it.name == selectedIssue.importance.displayName }?.id
                        ?: when (selectedIssue.importance) {
                            com.example.domain.model.issue.IssueImportance.CRITICAL -> "001"
                            com.example.domain.model.issue.IssueImportance.HIGH -> "002"
                            com.example.domain.model.issue.IssueImportance.NORMAL -> "003"
                            com.example.domain.model.issue.IssueImportance.LOW -> "004"
                        }
                    
                    android.util.Log.d("IssueViewModel", "매핑된 값들:")
                    android.util.Log.d("IssueViewModel", "- typeId: $typeId (원본: ${selectedIssue.type.displayName})")
                    android.util.Log.d("IssueViewModel", "- priorityCd: $priorityCd (원본: ${selectedIssue.priority.displayName})")
                    android.util.Log.d("IssueViewModel", "- importanceCd: $importanceCd (원본: ${selectedIssue.importance.displayName})")
                    
                    _state.update { 
                        it.copy(
                            currentScreen = IssueScreenType.ISSUE_EDIT,
                            issueOptions = options, // 이슈 옵션을 state에 저장
                            editForm = IssueFormData(
                                title = title,
                                description = selectedIssue.description,
                                typeId = typeId,
                                priorityCd = priorityCd,
                                importanceCd = importanceCd,
                                startDate = selectedIssue.createdDate,
                                endDate = selectedIssue.dueDate
                            )
                        ) 
                    }
                }.onFailure { exception ->
                    android.util.Log.e("IssueViewModel", "이슈 옵션 로드 실패: ${exception.message}")
                    // 옵션 로드 실패 시 기본값 사용
                    _state.update { 
                        it.copy(
                            currentScreen = IssueScreenType.ISSUE_EDIT,
                            editForm = IssueFormData(
                                title = title,
                                description = selectedIssue.description,
                                typeId = "20230626135333726323", // 기본값
                                priorityCd = "003", // 기본값
                                importanceCd = "003", // 기본값
                                startDate = selectedIssue.createdDate,
                                endDate = selectedIssue.dueDate
                            )
                        ) 
                    }
                }
            }
        }
    }
    
    private fun backFromCreate() {
        _state.update { 
            it.copy(
                currentScreen = IssueScreenType.ISSUE_LIST,
                createForm = IssueFormData()
            ) 
        }
    }
    
    private fun backFromEdit() {
        _state.update { 
            it.copy(
                currentScreen = IssueScreenType.ISSUE_DETAIL,
                editForm = IssueFormData()
            ) 
        }
    }
    
    private fun createIssue(intent: IssueIntent.CreateIssue) {
        viewModelScope.launch {
            _state.update { it.copy(isCreating = true) }
            val currentState = _state.value
            val projectId = currentState.selectedProjectId ?: return@launch
            // projectNo는 별도 입력 없이, 선택된 프로젝트(selectedProjectId)만 사용
            createIssueUseCase(
                title = intent.title,
                description = intent.description,
                typeId = intent.typeId,
                priorityCd = intent.priorityCd,
                importanceCd = intent.importanceCd,
                startDate = intent.startDate,
                endDate = intent.endDate,
                projectNo = projectId // <- 여기만 사용
            ).onSuccess {
                _state.update { it.copy(isCreating = false) }
                sendEffect(IssueEffect.ShowCreateSuccess)
                backFromCreate()
                loadIssues() // 목록 새로고침
            }.onFailure { exception ->
                _state.update { it.copy(isCreating = false) }
                sendEffect(IssueEffect.ShowError(exception.message ?: "이슈 등록 실패"))
            }
        }
    }
    
    private fun updateIssue(intent: IssueIntent.UpdateIssue) {
        viewModelScope.launch {
            _state.update { it.copy(isEditing = true) }
            
            val currentState = _state.value
            val selectedIssue = currentState.selectedIssue ?: return@launch
            val projectId = currentState.selectedProjectId ?: return@launch
            
            // 디버깅을 위한 로그 추가
            android.util.Log.d("IssueViewModel", "이슈 수정 요청:")
            android.util.Log.d("IssueViewModel", "- 제목: ${intent.title}")
            android.util.Log.d("IssueViewModel", "- 설명: ${intent.description}")
            android.util.Log.d("IssueViewModel", "- 타입 ID: ${intent.typeId}")
            android.util.Log.d("IssueViewModel", "- 우선순위 ID: ${intent.priorityCd}")
            android.util.Log.d("IssueViewModel", "- 중요도 ID: ${intent.importanceCd}")
            android.util.Log.d("IssueViewModel", "- 시작일: ${intent.startDate}")
            android.util.Log.d("IssueViewModel", "- 완료일: ${intent.endDate}")
            
            updateIssueUseCase(
                issueId = selectedIssue.id,
                title = intent.title,
                description = intent.description,
                typeId = intent.typeId,
                priorityCd = intent.priorityCd,
                importanceCd = intent.importanceCd,
                startDate = intent.startDate,
                endDate = intent.endDate,
                projectNo = projectId
            ).onSuccess {
                _state.update { it.copy(isEditing = false) }
                sendEffect(IssueEffect.ShowUpdateSuccess)
                backFromEdit()
                
                // 목록 새로고침 후 상세 화면의 selectedIssue도 업데이트
                loadIssues()
                
                // 수정된 이슈 정보로 selectedIssue 업데이트
                val updatedIssue = selectedIssue.copy(
                    title = intent.title,
                    description = intent.description,
                    type = mapStringToIssueType(intent.typeId),
                    priority = mapStringToIssuePriority(intent.priorityCd),
                    importance = mapStringToIssueImportance(intent.importanceCd),
                    dueDate = intent.endDate
                )
                
                _state.update { it.copy(selectedIssue = updatedIssue) }
            }.onFailure { exception ->
                _state.update { it.copy(isEditing = false) }
                sendEffect(IssueEffect.ShowError(exception.message ?: "이슈 수정 실패"))
            }
        }
    }
    
    // ID를 IssueType으로 변환하는 헬퍼 함수
    private fun mapStringToIssueType(typeId: String): com.example.domain.model.issue.IssueType {
        val currentState = _state.value
        val options = currentState.issueOptions
        
        android.util.Log.d("IssueViewModel", "타입 매핑 - 입력 ID: $typeId")
        
        return if (options != null) {
            // ID로 타입 찾기
            val type = options.issueTypes.find { it.id == typeId }
            android.util.Log.d("IssueViewModel", "타입 매핑 - 찾은 타입: ${type?.name}")
            
            val result = when (type?.name) {
                "버그" -> com.example.domain.model.issue.IssueType.BUG
                "기능", "요구사항" -> com.example.domain.model.issue.IssueType.FEATURE
                "작업", "테스트 케이스" -> com.example.domain.model.issue.IssueType.TASK
                "검토" -> com.example.domain.model.issue.IssueType.REVIEW
                else -> com.example.domain.model.issue.IssueType.BUG
            }
            android.util.Log.d("IssueViewModel", "타입 매핑 - 결과: $result")
            result
        } else {
            android.util.Log.d("IssueViewModel", "타입 매핑 - 옵션 없음, 기본값 사용")
            com.example.domain.model.issue.IssueType.BUG
        }
    }
    
    // ID를 IssuePriority로 변환하는 헬퍼 함수
    private fun mapStringToIssuePriority(priorityId: String): com.example.domain.model.issue.IssuePriority {
        val currentState = _state.value
        val options = currentState.issueOptions
        
        return if (options != null) {
            // ID로 우선순위 찾기
            val priority = options.priorities.find { it.id == priorityId }
            when (priority?.name) {
                "긴급" -> com.example.domain.model.issue.IssuePriority.CRITICAL
                "높음" -> com.example.domain.model.issue.IssuePriority.HIGH
                "보통" -> com.example.domain.model.issue.IssuePriority.NORMAL
                "낮음" -> com.example.domain.model.issue.IssuePriority.LOW
                else -> com.example.domain.model.issue.IssuePriority.NORMAL
            }
        } else {
            // 옵션이 없으면 기본값
            com.example.domain.model.issue.IssuePriority.NORMAL
        }
    }
    
    // ID를 IssueImportance로 변환하는 헬퍼 함수
    private fun mapStringToIssueImportance(importanceId: String): com.example.domain.model.issue.IssueImportance {
        val currentState = _state.value
        val options = currentState.issueOptions
        
        return if (options != null) {
            // ID로 중요도 찾기
            val importance = options.importances.find { it.id == importanceId }
            when (importance?.name) {
                "긴급" -> com.example.domain.model.issue.IssueImportance.CRITICAL
                "높음" -> com.example.domain.model.issue.IssueImportance.HIGH
                "보통" -> com.example.domain.model.issue.IssueImportance.NORMAL
                "낮음" -> com.example.domain.model.issue.IssueImportance.LOW
                else -> com.example.domain.model.issue.IssueImportance.NORMAL
            }
        } else {
            // 옵션이 없으면 기본값
            com.example.domain.model.issue.IssueImportance.NORMAL
        }
    }
    
    private fun deleteIssue() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            
            val currentState = _state.value
            val selectedIssue = currentState.selectedIssue ?: return@launch
            val projectId = currentState.selectedProjectId ?: return@launch
            
            deleteIssueUseCase(
                issueId = selectedIssue.id,
                projectNo = projectId
            ).onSuccess {
                _state.update { it.copy(isDeleting = false) }
                sendEffect(IssueEffect.ShowDeleteSuccess)
                backToIssueList()
                loadIssues() // 목록 새로고침
            }.onFailure { exception ->
                _state.update { it.copy(isDeleting = false) }
                sendEffect(IssueEffect.ShowError(exception.message ?: "이슈 삭제 실패"))
            }
        }
    }
    
    private fun loadIssueOptions() {
        viewModelScope.launch {
            val currentState = _state.value
            val projectId = currentState.selectedProjectId ?: return@launch
            
            getIssueOptionsUseCase(projectId)
                .onSuccess { options ->
                    _state.update { it.copy(issueOptions = options) }
                }
                .onFailure { exception ->
                    sendEffect(IssueEffect.ShowError(exception.message ?: "옵션 로드 실패"))
                }
        }
    }
    
    private fun sendEffect(effect: IssueEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}