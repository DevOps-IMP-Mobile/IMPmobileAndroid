package com.example.issue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@Composable
fun IssueScreen(
    viewModel: IssueViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Effect 처리
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is IssueEffect.ShowError -> {
                    // TODO: 에러 스낵바 표시
                }
                is IssueEffect.ShowRefreshComplete -> {
                    // TODO: 새로고침 완료 메시지
                }
                is IssueEffect.ShowApprovalSuccess -> {
                    // TODO: 승인 완료 스낵바
                }
                is IssueEffect.ShowRejectionSuccess -> {
                    // TODO: 반려 완료 스낵바
                }
            }
        }
    }
    
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
        
        Divider()
        
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
                placeholder = { Text("Search ID/Name/Number") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                },
                singleLine = true
            )
            
            // 필터 및 정렬
            FilterAndSortRow(
                currentSortType = state.sortType,
                issueCount = state.issues.size,
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
            items(state.issues) { issue ->
                IssueItem(
                    issue = issue,
                    onClick = { onIntent(IssueIntent.SelectIssue(issue)) }
                )
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
    onSortTypeChanged: (IssueSortType) -> Unit,
    onStatusFilter: (IssueStatus?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // All 필터 버튼
        FilterChip(
            onClick = { onStatusFilter(null) },
            label = { Text("All($issueCount)") },
            selected = false
        )
        
        // 정렬 드롭다운
        var sortExpanded by remember { mutableStateOf(false) }
        
        FilterChip(
            onClick = { sortExpanded = true },
            label = { Text(currentSortType.displayName) },
            selected = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
        
        DropdownMenu(
            expanded = sortExpanded,
            onDismissRequest = { sortExpanded = false }
        ) {
            IssueSortType.values().forEach { sortType ->
                DropdownMenuItem(
                    text = { Text(sortType.displayName) },
                    onClick = {
                        onSortTypeChanged(sortType)
                        sortExpanded = false
                    }
                )
            }
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 상단 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = issue.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Deadline: ${issue.dueDate}",
                        fontSize = 12.sp,
                        color = Color(0xFFFF6B35)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PriorityBadge(priority = issue.priority)
                    StatusBadge(status = issue.status)
                }
            }
            
            // 하단 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImportanceBadge(importance = issue.importance)
                
                AssigneeInfo(
                    assigneeName = issue.assigneeName,
                    assigneeId = issue.assigneeId
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: IssuePriority) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(android.graphics.Color.parseColor(priority.colorHex)).copy(alpha = 0.1f)
    ) {
        Text(
            text = priority.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = Color(android.graphics.Color.parseColor(priority.colorHex)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusBadge(status: IssueStatus) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(android.graphics.Color.parseColor(status.colorHex)).copy(alpha = 0.1f)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = Color(android.graphics.Color.parseColor(status.colorHex)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ImportanceBadge(importance: IssueImportance) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(android.graphics.Color.parseColor(importance.colorHex)).copy(alpha = 0.1f)
    ) {
        Text(
            text = importance.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = Color(android.graphics.Color.parseColor(importance.colorHex)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AssigneeInfo(assigneeName: String, assigneeId: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = assigneeName.first().toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = assigneeName,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun IssueDetailScreen(
    state: IssueState,
    onIntent: (IssueIntent) -> Unit
) {
    val issue = state.selectedIssue ?: return
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { onIntent(IssueIntent.BackToIssueList) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            
            Text(
                text = issue.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* TODO: 알림 */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "알림")
                }
                IconButton(onClick = { /* TODO: 수정 */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "수정")
                }
                IconButton(onClick = { onIntent(IssueIntent.BackToIssueList) }) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }
        }
        
        Divider()
        
        // 이슈 상세 정보
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                IssueDetailCard(issue = issue)
            }
            
            item {
                CommentSection()
            }
        }
        
        // 하단 액션 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onIntent(IssueIntent.ApproveIssue(issue.id)) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("승인")
            }
            
            Button(
                onClick = { onIntent(IssueIntent.RejectIssue(issue.id)) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4444)
                )
            ) {
                Text("반려")
            }
        }
    }
}

@Composable
private fun IssueDetailCard(issue: Issue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 기본 정보 그리드
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailInfoRow(
                    "이슈 상태",
                    issue.status.displayName,
                    badgeColor = Color(android.graphics.Color.parseColor(issue.status.colorHex))
                )
                DetailInfoRow("이슈타입", issue.type.displayName)
                DetailInfoRow(
                    "중요도", 
                    issue.importance.displayName,
                    badgeColor = Color(android.graphics.Color.parseColor(issue.importance.colorHex))
                )
                DetailInfoRow(
                    "우선순위",
                    issue.priority.displayName,
                    badgeColor = Color(android.graphics.Color.parseColor(issue.priority.colorHex))
                )
                DetailInfoRow("등록자", issue.reporterName)
                DetailInfoRow("담당자", issue.assigneeName)
                DetailInfoRow("등록일", issue.createdDate)
                DetailInfoRow("마감기한", issue.dueDate)
                DetailInfoRow("변경일", issue.updatedDate ?: "-")
                DetailInfoRow("레퍼지토리", issue.repository)
                
                // 내용
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "내용",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9F9F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Text(
                            text = issue.description,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    badgeColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        if (badgeColor != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = badgeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = value,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun CommentSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 댓글 탭
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TabButton("댓글", isSelected = true)
                TabButton("변경이력", isSelected = false)
                TabButton("자동화 수행", isSelected = false)
            }
            
            // 빈 댓글 상태
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💬",
                    fontSize = 24.sp
                )
                Text(
                    text = "작성된 댓글이 없습니다.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // 댓글 입력
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("새 댓글 입력") },
                minLines = 3
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* TODO: 댓글 등록 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("댓글 등록")
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    )
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun IssueItemPreview() {
    MaterialTheme {
        IssueItem(
            issue = Issue(
                id = "aaaaa",
                title = "aaaaa",
                description = "aaaaaaaaa",
                type = IssueType.REVIEW,
                status = IssueStatus.REGISTERED,
                priority = IssuePriority.CRITICAL,
                importance = IssueImportance.CRITICAL,
                assigneeId = "demo_user_1",
                assigneeName = "데모서용자1",
                reporterId = "admin",
                reporterName = "서비스관리자",
                createdDate = "2025-06-17",
                dueDate = "2025-06-17",
                repository = "module3",
                projectId = "demo"
            ),
            onClick = {}
        )
    }
}