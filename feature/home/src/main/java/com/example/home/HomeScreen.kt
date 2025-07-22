package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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

@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Effect 처리
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTaskList -> {
                    // TODO: 태스크 목록으로 이동
                }
                is HomeEffect.NavigateToStatusChart -> {
                    // TODO: 상태 차트로 이동
                }
                is HomeEffect.NavigateToTypeChart -> {
                    // TODO: 타입 차트로 이동
                }
                is HomeEffect.ShowError -> {
                    // TODO: 에러 스낵바 표시
                }
                is HomeEffect.ShowRefreshComplete -> {
                    // TODO: 새로고침 완료 메시지
                }
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 간단한 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "홈",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { viewModel.handleIntent(HomeIntent.RefreshDashboard) }
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
        
        HomeContent(
            state = state,
            onIntent = viewModel::handleIntent
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            TodayTaskSection(
                inProgress = state.dashboardData.todayTasks.inProgress,
                delayed = state.dashboardData.todayTasks.delayed,
                registered = state.dashboardData.todayTasks.registered,
                currentDate = state.dashboardData.currentDate,
                onTaskClick = { onIntent(HomeIntent.NavigateToTaskDetail) }
            )
        }
        
        item {
            StatusSection(
                onStatusClick = { onIntent(HomeIntent.NavigateToStatusDetail) },
                onTypeClick = { onIntent(HomeIntent.NavigateToTypeDetail) }
            )
        }
    }
}

@Composable
private fun TodayTaskSection(
    inProgress: Int,
    delayed: Int,
    registered: Int,
    currentDate: String,
    onTaskClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "오늘의 할일",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTaskClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TODAY $currentDate",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TaskCountItem(
                        count = inProgress.toString(), 
                        label = "진행", 
                        color = Color.Black
                    )
                    TaskCountItem(
                        count = delayed.toString(), 
                        label = "지연", 
                        color = Color.Red
                    )
                    TaskCountItem(
                        count = registered.toString(), 
                        label = "등록", 
                        color = Color.Blue
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCountItem(
    count: String,
    label: String, 
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatusSection(
    onStatusClick: () -> Unit,
    onTypeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "현황",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                title = "나의 상태 현황",
                modifier = Modifier.weight(1f),
                onClick = onStatusClick
            )
            StatusCard(
                title = "나의 타입 현황",
                modifier = Modifier.weight(1f),
                onClick = onTypeClick
            )
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "데이터가 없습니다",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// 프리뷰를 위한 더미 HomeState (MVI 의존성 없이)
@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TodayTaskSection(
                inProgress = 3,
                delayed = 1,
                registered = 2,
                currentDate = "2025-01-23",
                onTaskClick = {}
            )
            
            StatusSection(
                onStatusClick = {},
                onTypeClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayTaskSectionPreview() {
    MaterialTheme {
        TodayTaskSection(
            inProgress = 3,
            delayed = 1,
            registered = 2,
            currentDate = "2025-01-23",
            onTaskClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusSectionPreview() {
    MaterialTheme {
        StatusSection(
            onStatusClick = {},
            onTypeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 헤더 프리뷰
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "홈",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침"
                )
            }
            
            Divider()
            
            // 콘텐츠 프리뷰
            HomeContentPreview()
        }
    }
}