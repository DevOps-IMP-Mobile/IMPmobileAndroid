package com.example.issue

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.issue.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreen(
    viewModel: IssueViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect 처리
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is IssueEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is IssueEffect.ShowRefreshComplete -> {
                    // TODO: 새로고침 완료 메시지
                }
                is IssueEffect.ShowCreateSuccess -> {
                    // TODO: 등록 완료 스낵바
                }
                is IssueEffect.ShowUpdateSuccess -> {
                    // TODO: 수정 완료 스낵바
                }
                is IssueEffect.ShowDeleteSuccess -> {
                    // TODO: 삭제 완료 스낵바
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->  // innerPadding을 _로 변경하여 사용하지 않음을 표시
        when (state.currentScreen) {
            IssueScreenType.ISSUE_LIST -> {
                IssueListScreen(
                    state = state,
                    onIntent = viewModel::handleIntent
                )
            }
            IssueScreenType.ISSUE_DETAIL -> {
                IssueDetailScreen(
                    state = state,
                    onIntent = viewModel::handleIntent
                )
            }
            IssueScreenType.ISSUE_CREATE -> {
                IssueCreateScreen(
                    state = state,
                    onIntent = viewModel::handleIntent
                )
            }
            IssueScreenType.ISSUE_EDIT -> {
                IssueEditScreen(
                    state = state,
                    onIntent = viewModel::handleIntent
                )
            }
        }
    }
}

@Composable
private fun IssueListScreen(
    state: IssueState,
    onIntent: (IssueIntent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이슈",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 이슈 등록 버튼
                Button(
                    onClick = { onIntent(IssueIntent.NavigateToCreate) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "이슈 등록",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("이슈 등록")
                }
                
                // 새로고침 버튼
                IconButton(
                    onClick = { onIntent(IssueIntent.RefreshIssues) }
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "새로고침"
                        )
                    }
                }
            }
        }
        
        HorizontalDivider()
        
        // 프로젝트 선택 및 필터
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 프로젝트 선택
            ProjectSelector(
                projects = state.projects,
                selectedProjectId = state.selectedProjectId,
                onProjectSelected = { projectId ->
                    onIntent(IssueIntent.SelectProject(projectId))
                }
            )
            
            // 검색창
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onIntent(IssueIntent.UpdateSearchQuery(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("이슈 ID, 이름, 번호로 검색") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onIntent(IssueIntent.UpdateSearchQuery("")) }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "검색어 지우기")
                        }
                    }
                },
                singleLine = true
            )
            
            // 필터 및 정렬
            FilterAndSortRow(
                currentSortType = state.sortType,
                issueCount = state.issues.size,
                searchQuery = state.searchQuery,
                onSortTypeChanged = { onIntent(IssueIntent.ChangeSortType(it)) },
                onStatusFilter = { onIntent(IssueIntent.FilterByStatus(it)) }
            )
        }
        
        // 이슈 목록
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.issues.isEmpty() && state.searchQuery.isNotEmpty()) {
                // 검색 결과가 없을 때
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "검색 결과가 없습니다",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "'${state.searchQuery}'에 대한 검색 결과가 없습니다",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else if (state.issues.isEmpty()) {
                // 이슈가 없을 때
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "등록된 이슈가 없습니다",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                // 이슈 목록 표시
                items(state.issues) { issue ->
                    IssueItem(
                        issue = issue,
                        onClick = { onIntent(IssueIntent.SelectIssue(issue)) }
                    )
                }
            }
            
            // 하단 여백
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectSelector(
    projects: List<Pair<String, String>>,
    selectedProjectId: String?,
    onProjectSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProject = projects.find { it.first == selectedProjectId }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedProject?.second ?: "프로젝트 선택",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            projects.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onProjectSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterAndSortRow(
    currentSortType: IssueSortType,
    issueCount: Int,
    searchQuery: String,
    onSortTypeChanged: (IssueSortType) -> Unit,
    onStatusFilter: (IssueStatus?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이슈 개수
        Text(
            text = "총 ${issueCount}개",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        // 정렬 옵션
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = currentSortType == IssueSortType.PRIORITY,
                onClick = { onSortTypeChanged(IssueSortType.PRIORITY) },
                label = { Text("우선순위") }
            )
            FilterChip(
                selected = currentSortType == IssueSortType.CREATED_DATE,
                onClick = { onSortTypeChanged(IssueSortType.CREATED_DATE) },
                label = { Text("등록일") }
            )
        }
    }
}

@Composable
private fun IssueItem(
    issue: Issue,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 제목과 상태
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = issue.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                StatusChip(status = issue.status)
            }
            
            // 타입, 우선순위, 중요도
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeChip(type = issue.type)
                PriorityChip(priority = issue.priority)
                ImportanceChip(importance = issue.importance)
            }
            
            // 담당자와 날짜
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "담당: ${issue.assigneeName ?: "미지정"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = issue.createdDate,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: IssueStatus) {
    val (backgroundColor, textColor) = when (status) {
        IssueStatus.REGISTERED -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        IssueStatus.IN_PROGRESS -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
        IssueStatus.RESOLVED -> Color(0xFFE8F5E8) to Color(0xFF388E3C)
        IssueStatus.CLOSED -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = when (status) {
                IssueStatus.REGISTERED -> "등록"
                IssueStatus.IN_PROGRESS -> "진행"
                IssueStatus.RESOLVED -> "해결"
                IssueStatus.CLOSED -> "완료"
            },
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TypeChip(type: IssueType) {
    val (backgroundColor, textColor) = when (type) {
        IssueType.BUG -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        IssueType.FEATURE -> Color(0xFFE8F5E8) to Color(0xFF388E3C)
        IssueType.TASK -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        IssueType.REVIEW -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = when (type) {
                IssueType.BUG -> "버그"
                IssueType.FEATURE -> "기능"
                IssueType.TASK -> "작업"
                IssueType.REVIEW -> "검토"
            },
            fontSize = 11.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PriorityChip(priority: IssuePriority) {
    val (backgroundColor, textColor) = when (priority) {
        IssuePriority.CRITICAL -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        IssuePriority.HIGH -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
        IssuePriority.NORMAL -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        IssuePriority.LOW -> Color(0xFFE8F5E8) to Color(0xFF388E3C)
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = when (priority) {
                IssuePriority.CRITICAL -> "긴급"
                IssuePriority.HIGH -> "높음"
                IssuePriority.NORMAL -> "보통"
                IssuePriority.LOW -> "낮음"
            },
            fontSize = 11.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ImportanceChip(importance: IssueImportance) {
    val (backgroundColor, textColor) = when (importance) {
        IssueImportance.CRITICAL -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        IssueImportance.HIGH -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
        IssueImportance.NORMAL -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        IssueImportance.LOW -> Color(0xFFE8F5E8) to Color(0xFF388E3C)
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = when (importance) {
                IssueImportance.CRITICAL -> "심각"
                IssueImportance.HIGH -> "높음"
                IssueImportance.NORMAL -> "보통"
                IssueImportance.LOW -> "낮음"
            },
            fontSize = 11.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun IssueDetailScreen(
    state: IssueState,
    onIntent: (IssueIntent) -> Unit
) {
    val issue = state.selectedIssue
    
    if (issue == null) {
        onIntent(IssueIntent.BackToIssueList)
        return
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onIntent(IssueIntent.BackToIssueList) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            
            Text(
                text = "이슈 상세",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onIntent(IssueIntent.NavigateToEdit) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "수정",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("수정")
                }
                
                Button(
                    onClick = { onIntent(IssueIntent.DeleteIssue) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    enabled = !state.isDeleting
                ) {
                    if (state.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("삭제")
                }
            }
        }
        
        HorizontalDivider()
        
        // 이슈 상세 내용
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // 제목
                Text(
                    text = issue.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                // 상태, 타입, 우선순위, 중요도
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = issue.status)
                    TypeChip(type = issue.type)
                    PriorityChip(priority = issue.priority)
                    ImportanceChip(importance = issue.importance)
                }
            }
            
            item {
                // 설명
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "설명",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = issue.description.ifEmpty { "설명이 없습니다." },
                            fontSize = 14.sp,
                            color = if (issue.description.isEmpty()) Color.Gray else Color.Black
                        )
                    }
                }
            }
            
            item {
                // 담당자 정보
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "담당자",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = issue.assigneeName ?: "미지정",
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            item {
                // 날짜 정보
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "날짜 정보",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "등록일",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = issue.createdDate,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "완료 예정일",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = issue.dueDate ?: "미정",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IssueCreateScreen(
    state: IssueState,
    onIntent: (IssueIntent) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTypeId by remember { mutableStateOf("") }
    var selectedPriorityCd by remember { mutableStateOf("") }
    var selectedImportanceCd by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onIntent(IssueIntent.BackFromCreate) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            
            Text(
                text = "이슈 등록",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = {
                    onIntent(IssueIntent.CreateIssue(
                        title = title,
                        description = description,
                        typeId = selectedTypeId,
                        priorityCd = selectedPriorityCd,
                        importanceCd = selectedImportanceCd,
                        startDate = startDate,
                        endDate = endDate
                    ))
                },
                enabled = title.isNotEmpty() && selectedTypeId.isNotEmpty() && 
                         selectedPriorityCd.isNotEmpty() && selectedImportanceCd.isNotEmpty() &&
                         !state.isCreating
            ) {
                if (state.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("등록")
                }
            }
        }
        
        HorizontalDivider()
        
        // 폼
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("설명") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
            
            item {
                // 이슈 타입 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedType = options.issueTypes.find { it.id == selectedTypeId }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedType?.name ?: "이슈 타입 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("이슈 타입 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedTypeId.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.issueTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedTypeId = type.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("이슈 타입 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 우선순위 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedPriority = options.priorities.find { it.id == selectedPriorityCd }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedPriority?.name ?: "우선순위 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("우선순위 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedPriorityCd.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.priorities.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority.name) },
                                    onClick = {
                                        selectedPriorityCd = priority.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("우선순위 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 중요도 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedImportance = options.importances.find { it.id == selectedImportanceCd }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedImportance?.name ?: "중요도 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("중요도 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedImportanceCd.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.importances.forEach { importance ->
                                DropdownMenuItem(
                                    text = { Text(importance.name) },
                                    onClick = {
                                        selectedImportanceCd = importance.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("중요도 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 시작일
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("시작일 (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-01-01") }
                )
            }
            
            item {
                // 완료 예정일
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("완료 예정일 (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-12-31") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IssueEditScreen(
    state: IssueState,
    onIntent: (IssueIntent) -> Unit
) {
    var title by remember { mutableStateOf(state.editForm.title) }
    var description by remember { mutableStateOf(state.editForm.description) }
    var selectedTypeId by remember { mutableStateOf(state.editForm.typeId) }
    var selectedPriorityCd by remember { mutableStateOf(state.editForm.priorityCd) }
    var selectedImportanceCd by remember { mutableStateOf(state.editForm.importanceCd) }
    var startDate by remember { mutableStateOf(state.editForm.startDate) }
    var endDate by remember { mutableStateOf(state.editForm.endDate) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onIntent(IssueIntent.BackFromEdit) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            
            Text(
                text = "이슈 수정",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = {
                    onIntent(IssueIntent.UpdateIssue(
                        title = title,
                        description = description,
                        typeId = selectedTypeId,
                        priorityCd = selectedPriorityCd,
                        importanceCd = selectedImportanceCd,
                        startDate = startDate,
                        endDate = endDate
                    ))
                },
                enabled = title.isNotEmpty() && selectedTypeId.isNotEmpty() && 
                         selectedPriorityCd.isNotEmpty() && selectedImportanceCd.isNotEmpty() &&
                         !state.isEditing
            ) {
                if (state.isEditing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("수정")
                }
            }
        }
        
        HorizontalDivider()
        
        // 폼 (등록과 동일하지만 기존 값으로 초기화)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = title.isBlank(),
                    supportingText = if (title.isBlank()) {
                        { Text("제목을 입력해주세요", color = Color.Red) }
                    } else null
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("설명") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
            
            item {
                // 이슈 타입 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedType = options.issueTypes.find { it.id == selectedTypeId }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedType?.name ?: "이슈 타입 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("이슈 타입 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedTypeId.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.issueTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedTypeId = type.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("이슈 타입 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 우선순위 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedPriority = options.priorities.find { it.id == selectedPriorityCd }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedPriority?.name ?: "우선순위 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("우선순위 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedPriorityCd.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.priorities.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority.name) },
                                    onClick = {
                                        selectedPriorityCd = priority.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("우선순위 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 중요도 선택
                val options = state.issueOptions
                if (options != null) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedImportance = options.importances.find { it.id == selectedImportanceCd }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedImportance?.name ?: "중요도 선택 *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("중요도 *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (selectedImportanceCd.isEmpty()) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.importances.forEach { importance ->
                                DropdownMenuItem(
                                    text = { Text(importance.name) },
                                    onClick = {
                                        selectedImportanceCd = importance.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // 옵션이 로드되지 않은 경우
                    OutlinedTextField(
                        value = "이슈 옵션 로딩 중...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("중요도 *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        trailingIcon = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    )
                }
            }
            
            item {
                // 시작일
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("시작일 (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-01-01") }
                )
            }
            
            item {
                // 완료 예정일
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("완료 예정일 (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-12-31") }
                )
            }
        }
    }
}