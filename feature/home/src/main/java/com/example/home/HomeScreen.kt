package com.example.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
        // 헤더
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            
            // 프로젝트 선택 드롭다운
            ProjectDropdown(
                projects = state.dashboardData.projects,
                selectedProject = state.dashboardData.selectedProject,
                onProjectSelected = { project ->
                    viewModel.handleIntent(HomeIntent.SelectProject(project))
                }
            )
        }
        
        Divider()
        
        HomeContent(
            state = state,
            onIntent = viewModel::handleIntent
        )
    }
}

@Composable
private fun ProjectSelector(
    projects: List<com.example.domain.model.home.Project>,
    selectedProject: com.example.domain.model.home.Project?,
    onProjectSelected: (com.example.domain.model.home.Project) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (projects.isNotEmpty()) {
                    expanded = true 
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = selectedProject?.projectName ?: "프로젝트를 선택하세요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedProject != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (selectedProject != null) {
                    Text(
                        text = "프로젝트 번호: ${selectedProject.projectNo}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "프로젝트 선택",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
    
    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { 
                Text(
                    text = "프로젝트 선택",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(projects) { project ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onProjectSelected(project)
                                    expanded = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedProject?.projectNo == project.projectNo) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = project.projectName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedProject?.projectNo == project.projectNo) 
                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "프로젝트 번호: ${project.projectNo}",
                                    fontSize = 12.sp,
                                    color = if (selectedProject?.projectNo == project.projectNo) 
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { expanded = false }) {
                    Text("취소")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            LargeStatusChartSection(
                state = state,
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
                        label = "잔여", 
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LargeStatusChartSection(
    state: HomeState,
    onStatusClick: () -> Unit,
    onTypeClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "현황",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // 슬라이드 차트 컨테이너
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 좌우 화살표 버튼
                IconButton(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "이전 차트",
                        tint = if (pagerState.currentPage > 0) Color(0xFF6366F1) else Color.Gray
                    )
                }
                
                IconButton(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "다음 차트",
                        tint = if (pagerState.currentPage < 1) Color(0xFF6366F1) else Color.Gray
                    )
                }
                
                // 슬라이드 차트
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            // 나의 상태 현황
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Text(
                                    text = "나의 상태 현황",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // 큰 도넛 차트 (위로 올림)
                                Box(
                                    modifier = Modifier.offset(y = (-20).dp) // 위로 20dp 올림
                                ) {
                                    LargeStatusChart(
                                        statusChartData = state.dashboardData.statusChart
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // 범례 (위로 올림)
                                Box(
                                    modifier = Modifier.offset(y = (-15).dp) // 위로 15dp 올림
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                                    ) {
                                        state.dashboardData.statusChart.take(3).forEach { statusData ->
                                            LegendItem(
                                                color = Color(android.graphics.Color.parseColor(statusData.color)),
                                                label = statusData.label
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        1 -> {
                            // 나의 타입 현황
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Text(
                                    text = "나의 타입 현황",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // 큰 도넛 차트 (위로 올림)
                                Box(
                                    modifier = Modifier.offset(y = (-20).dp) // 위로 20dp 올림
                                ) {
                                    LargeTypeChart(
                                        typeChartData = state.dashboardData.typeChart
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // 범례 (위로 올림)
                                Box(
                                    modifier = Modifier.offset(y = (-15).dp) // 위로 15dp 올림
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                                    ) {
                                        state.dashboardData.typeChart.take(3).forEach { typeData ->
                                            LegendItem(
                                                color = Color(android.graphics.Color.parseColor(typeData.color)),
                                                label = typeData.label
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                // 페이지 인디케이터 (하단 점)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) { iteration ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = if (pagerState.currentPage == iteration) 
                                        Color(0xFF6366F1) 
                                    else 
                                        Color(0xFFE5E7EB),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
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
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (title == "나의 상태 현황") {
                    StatusChart()
                } else {
                    TypeChart()
                }
            }
        }
    }
}

@Composable
private fun StatusChart() {
    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // SVG 스타일의 도넛 차트 구현
        Canvas(
            modifier = Modifier.size(60.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 25f
            val strokeWidth = 8f
            
            // 배경 원
            drawCircle(
                color = Color(0xFFE5E7EB),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // 등록 상태 (빨간색) - 40%
            val registeredAngle = 144f // 360 * 0.4
            drawArc(
                color = Color(0xFFEF4444),
                startAngle = -90f,
                sweepAngle = registeredAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
            
            // 확인 상태 (초록색) - 20%
            val confirmedAngle = 72f // 360 * 0.2
            drawArc(
                color = Color(0xFF10B981),
                startAngle = -90f + registeredAngle,
                sweepAngle = confirmedAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
private fun LargeStatusChart(
    statusChartData: List<com.example.domain.model.home.StatusChartData>
) {
    Box(
        modifier = Modifier.size(300.dp), // 250dp -> 300dp로 크게
        contentAlignment = Alignment.Center
    ) {
        // 큰 SVG 스타일의 도넛 차트 구현
        Canvas(
            modifier = Modifier.size(300.dp) // 250dp -> 300dp로 크게
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 120f // 100f -> 120f로 크게
            val strokeWidth = 30f // 25f -> 30f로 두껋게
            
            // 배경 원
            drawCircle(
                color = Color(0xFFE5E7EB),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            if (statusChartData.isNotEmpty()) {
                val totalCount = statusChartData.sumOf { it.count }
                var currentAngle = -90f
                
                statusChartData.forEach { statusData ->
                    val percentage = if (totalCount > 0) statusData.count.toFloat() / totalCount else 0f
                    val sweepAngle = percentage * 360f
                    
                    if (sweepAngle > 0) {
                        drawArc(
                            color = Color(android.graphics.Color.parseColor(statusData.color)),
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                        currentAngle += sweepAngle
                    }
                }
            } else {
                // 데이터가 없을 때 기본 차트
                drawArc(
                    color = Color(0xFFEF4444),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

@Composable
private fun LargeTypeChart(
    typeChartData: List<com.example.domain.model.home.TypeChartData>
) {
    Box(
        modifier = Modifier.size(300.dp), // 250dp -> 300dp로 크게
        contentAlignment = Alignment.Center
    ) {
        // 큰 SVG 스타일의 도넛 차트 구현
        Canvas(
            modifier = Modifier.size(300.dp) // 250dp -> 300dp로 크게
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 120f // 100f -> 120f로 크게
            val strokeWidth = 30f // 25f -> 30f로 두껋게
            
            // 배경 원
            drawCircle(
                color = Color(0xFFE5E7EB),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            if (typeChartData.isNotEmpty()) {
                val totalCount = typeChartData.sumOf { it.count }
                var currentAngle = -90f
                
                typeChartData.forEach { typeData ->
                    val percentage = if (totalCount > 0) typeData.count.toFloat() / totalCount else 0f
                    val sweepAngle = percentage * 360f
                    
                    if (sweepAngle > 0) {
                        drawArc(
                            color = Color(android.graphics.Color.parseColor(typeData.color)),
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                        currentAngle += sweepAngle
                    }
                }
            } else {
                // 데이터가 없을 때 기본 차트
                drawArc(
                    color = Color(0xFFEF4444),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

@Composable
private fun TypeChart() {
    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // SVG 스타일의 도넛 차트 구현
        Canvas(
            modifier = Modifier.size(60.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 25f
            val strokeWidth = 8f
            
            // 배경 원
            drawCircle(
                color = Color(0xFFE5E7EB),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // 버그 타입 (빨간색) - 100%
            drawArc(
                color = Color(0xFFEF4444),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
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
            
            LargeStatusChartSection(
                state = HomeState(),
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
fun LargeStatusChartSectionPreview() {
    MaterialTheme {
        LargeStatusChartSection(
            state = HomeState(),
            onStatusClick = {},
            onTypeClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDropdown(
    projects: List<com.example.domain.model.home.Project>,
    selectedProject: com.example.domain.model.home.Project?,
    onProjectSelected: (com.example.domain.model.home.Project) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedProject?.projectName ?: "프로젝트 선택",
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("프로젝트") }
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            projects.forEach { project ->
                DropdownMenuItem(
                    text = { Text(project.projectName) },
                    onClick = {
                        onProjectSelected(project)
                        expanded = false
                    }
                )
            }
        }
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