package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.project.Project
import com.example.domain.model.project.ProjectMember
import com.example.domain.model.project.ProjectStatus

@Composable
fun ProjectScreen(
    viewModel: ProjectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Effect 처리
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProjectEffect.ShowError -> {
                    // TODO: 에러 스낵바 표시
                }
                is ProjectEffect.ShowRefreshComplete -> {
                    // TODO: 새로고침 완료 메시지
                }
            }
        }
    }
    
    when (state.currentScreen) {
        ProjectScreenType.PROJECT_LIST -> {
            ProjectListScreen(
                state = state,
                onIntent = viewModel::handleIntent
            )
        }
        ProjectScreenType.PROJECT_DETAIL -> {
            ProjectDetailScreen(
                state = state,
                onIntent = viewModel::handleIntent
            )
        }
    }
}

@Composable
private fun ProjectListScreen(
    state: ProjectState,
    onIntent: (ProjectIntent) -> Unit
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
                text = "프로젝트 ${state.projects.size}개",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { onIntent(ProjectIntent.RefreshProjects) }
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
        
        // 프로젝트 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.projects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { onIntent(ProjectIntent.SelectProject(project)) }
                )
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 프로젝트 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(project.status.colorHex)),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.name.first().toString().uppercase(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 프로젝트명
            Text(
                text = project.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // 설명
            Text(
                text = project.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2
            )
            
            // 하단 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 멤버 수
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("👥", fontSize = 12.sp)
                    Text(
                        "${project.memberCount}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // 상태
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(android.graphics.Color.parseColor(project.status.colorHex)).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = project.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = Color(android.graphics.Color.parseColor(project.status.colorHex)),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectDetailScreen(
    state: ProjectState,
    onIntent: (ProjectIntent) -> Unit
) {
    val project = state.selectedProject ?: return
    
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
                onClick = { onIntent(ProjectIntent.BackToProjectList) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            
            Text(
                text = project.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Divider()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 프로젝트 정보
            item {
                ProjectInfoCard(project = project)
            }
            
            // 팀 멤버
            item {
                TeamMembersSection(
                    members = state.projectMembers,
                    isLoading = state.isLoadingMembers
                )
            }
        }
    }
}

@Composable
private fun ProjectInfoCard(project: Project) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "프로젝트 정보",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow("프로젝트 No.", project.id)
                InfoRow("프로젝트명", project.name)
                InfoRow("시작일", project.startDate)
                InfoRow("종료일", project.endDate)
                InfoRow("표준 여부", if (project.isStandard) "예" else "아니오")
                InfoRow("상태", project.status.displayName)
                InfoRow("관리자 ID", project.managerId)
                InfoRow("관리자명", project.managerName)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TeamMembersSection(
    members: List<ProjectMember>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "팀 멤버",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    members.forEach { member ->
                        MemberItem(member = member)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberItem(member: ProjectMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 아바타
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.name.first().toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 멤버 정보
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = member.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${member.position} • ${member.role}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun ProjectCardPreview() {
    MaterialTheme {
        ProjectCard(
            project = Project(
                id = "demo",
                name = "데모 프로젝트",
                description = "웹 어플리케이션 개발",
                startDate = "2025-05-01",
                endDate = "2025-12-31",
                status = ProjectStatus.RUNNING,
                memberCount = 3,
                managerId = "demo_user",
                managerName = "기본사용자"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
    MaterialTheme {
        MemberItem(
            member = ProjectMember(
                id = "1",
                name = "기본사용자",
                userId = "demo_user",
                position = "관리자",
                role = "프로젝트관리자"
            )
        )
    }
}