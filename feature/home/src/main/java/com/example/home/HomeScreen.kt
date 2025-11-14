package com.example.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    // 알림 권한 요청 Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permission", "알림 권한이 허용되었습니다")
        } else {
            Log.d("Permission", "알림 권한이 거부되었습니다")
        }
    }

    // 앱 실행 시 한 번만 권한 요청
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("Permission", "알림 권한이 이미 허용되어 있습니다")
                }
                else -> {
                    notificationPermissionLauncher.launch(permission)
                }
            }
        }
    }
    // Drawer 상태 동기화
    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    // Drawer가 닫힐 때 State 업데이트
    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed && state.isDrawerOpen) {
            viewModel.handleIntent(HomeIntent.CloseDrawer)
        }
    }

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
                // 🆕 추가
                is HomeEffect.ShowStatusUpdateSuccess -> {
                    // TODO: 성공 메시지 스낵바 표시
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProjectDrawerContent(
                projects = state.dashboardData.projects,
                selectedProject = state.dashboardData.selectedProject,
                onProjectSelected = { project ->
                    viewModel.handleIntent(HomeIntent.SelectProject(project))
                },
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 햄버거 메뉴 버튼
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "메뉴"
                                    )
                                }

                                Text(
                                    text = "홈",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.testAllAwsApis() // ⭐ 이 부분만 변경!
                                        android.widget.Toast.makeText(
                                            context,
                                            "AWS API 테스트 중... Logcat 확인!",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "AWS 테스트",
                                        tint = Color(0xFF4CAF50)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(HomeIntent.OpenUnifiedSheet)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star, // 통합 아이콘
                                        contentDescription = "통합 일정",
                                        tint = Color(0xFFFFD700) // 금색
                                    )
                                }
                                // 캘린더 아이콘 버튼 (최근 이슈 조회)
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(HomeIntent.OpenRecentIssuesSheet)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "최근 이슈"
                                    )
                                }

                                // 새로고침 버튼
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
                        }

// 통합 일정 BottomSheet
                        if (state.isUnifiedSheetOpen) {
                            UnifiedBottomSheet(
                                items = state.unifiedItems,
                                isLoading = state.isLoadingUnifiedItems,
                                selectedSources = state.selectedSources,
                                onSourceToggle = { source ->
                                    viewModel.handleIntent(HomeIntent.ToggleSource(source))
                                },
                                onDismiss = {
                                    viewModel.handleIntent(HomeIntent.CloseUnifiedSheet)
                                },
                                sheetState = rememberModalBottomSheetState()
                            )
                        }
// HomeScreen.kt의 210-220번 줄 근처
                        if (state.showStatusChangeDialog && state.selectedIssue != null) {
                            IssueStatusChangeDialog(
                                issue = state.selectedIssue!!,
                                onStatusSelected = { newStatus ->
                                    if (newStatus == com.example.domain.model.issue.IssueStatus.CLOSED) {
                                        viewModel.handleIntent(HomeIntent.ConfirmCompleteIssue(state.selectedIssue!!))
                                    } else {
                                        // ✅ enum name을 사용하여 상태 업데이트
                                        viewModel.handleIntent(
                                            HomeIntent.UpdateIssueStatus(
                                                state.selectedIssue!!,
                                                newStatus.name
                                            )
                                        )
                                    }
                                },
                                onDismiss = {
                                    viewModel.handleIntent(HomeIntent.CancelStatusChange)
                                }
                            )
                        }

                        // 🆕 완료 확인 다이얼로그
                        if (state.showCompleteConfirmDialog && state.selectedIssue != null) {
                            IssueCompleteConfirmDialog(
                                issue = state.selectedIssue!!,
                                onConfirm = {
                                    viewModel.handleIntent(HomeIntent.CompleteIssue(state.selectedIssue!!))
                                },
                                onDismiss = {
                                    viewModel.handleIntent(HomeIntent.CancelCompleteIssue)
                                }
                            )
                        }

                        // 🆕 로딩 오버레이
                        if (state.isUpdatingStatus) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        // 선택된 프로젝트 표시
                        if (state.dashboardData.selectedProject != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Column {
                                        Text(
                                            text = state.dashboardData.selectedProject!!.projectName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "프로젝트 번호: ${state.dashboardData.selectedProject!!.projectNo}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider()

                    HomeContent(
                        state = state,
                        onIntent = viewModel::handleIntent
                    )
                }

                // 최근 이슈 BottomSheet
                if (state.isRecentIssuesSheetOpen) {
                    RecentIssuesBottomSheet(
                        issues = state.recentIssues,
                        isLoading = state.isLoadingRecentIssues,
                        onIssueClick = { issue -> // ✅ 추가
                            viewModel.handleIntent(HomeIntent.SelectIssueForStatusChange(issue))
                        },
                        onDismiss = {
                            viewModel.handleIntent(HomeIntent.CloseRecentIssuesSheet)
                        },
                        sheetState = bottomSheetState
                    )
                }
            }
        }
    }
}

// 프로젝트 선택 Drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDrawerContent(
    projects: List<com.example.domain.model.home.Project>,
    selectedProject: com.example.domain.model.home.Project?,
    onProjectSelected: (com.example.domain.model.home.Project) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFF5F7FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
        ) {
            // ✨ 그라디언트 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFA27CEE), // 보라-파랑 시작
                                Color(0xFFCBAAEF)  // 연보라
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 📁 아이콘 배경
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountTree,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "프로젝트",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${projects.size}개 진행중",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // 프로젝트 목록
            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "프로젝트가 없습니다",
                            fontSize = 16.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projects) { project ->
                        ProjectDrawerItem(
                            project = project,
                            isSelected = selectedProject?.projectNo == project.projectNo,
                            onClick = { onProjectSelected(project) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 개선된 프로젝트 아이템 카드
 */
@Composable
private fun ProjectDrawerItem(
    project: com.example.domain.model.home.Project,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(0xFF667EEA).copy(alpha = 0.12f)
    } else {
        Color.White
    }

    val borderColor = if (isSelected) {
        Color(0xFF667EEA)
    } else {
        Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, borderColor)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🎨 프로젝트 아이콘 (색상별로 다르게)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        getProjectColor(project.projectNo).copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getProjectIcon(project.projectNo),
                    contentDescription = null,
                    tint = getProjectColor(project.projectNo),
                    modifier = Modifier.size(28.dp)
                )
            }

            // 프로젝트 정보
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = project.projectName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color(0xFF667EEA) else Color(0xFF2D3748),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 프로젝트 번호 태그
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected)
                            Color(0xFF667EEA).copy(alpha = 0.2f)
                        else
                            Color(0xFFE2E8F0)
                    ) {
                        Text(
                            text = "#${project.projectNo}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color(0xFF667EEA) else Color(0xFF718096),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // 상태 인디케이터
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF48BB78), CircleShape)
                        )
                        Text(
                            text = "진행중",
                            fontSize = 11.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }
            }

            // 선택 체크 아이콘
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF667EEA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "선택됨",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 프로젝트 번호에 따라 다른 색상 반환
 */
private fun getProjectColor(projectNo: String): Color {
    val colors = listOf(
        Color(0xFF667EEA), // 보라-파랑
        Color(0xFFED64A6), // 핑크
        Color(0xFF48BB78), // 초록
        Color(0xFFED8936), // 주황
        Color(0xFF4299E1), // 파랑
        Color(0xFF9F7AEA), // 보라
        Color(0xFFECC94B), // 노랑
        Color(0xFF38B2AC)  // 청록
    )
    return colors[projectNo.hashCode().mod(colors.size).let { if (it < 0) it + colors.size else it }]
}

/**
 * 프로젝트 번호에 따라 다른 아이콘 반환
 */
private fun getProjectIcon(projectNo: String): androidx.compose.ui.graphics.vector.ImageVector {
    val icons = listOf(
        Icons.Default.AccountTree,
        Icons.Default.FolderOpen,
        Icons.Default.Build,
        Icons.Default.Star,
        Icons.Default.Favorite,
        Icons.Default.Home,
        Icons.Default.Phone,
        Icons.Default.Email
    )
    return icons[projectNo.hashCode().mod(icons.size).let { if (it < 0) it + icons.size else it }]
}

// 최근 이슈 BottomSheet
// 최근 이슈 BottomSheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentIssuesBottomSheet(
    issues: List<com.example.domain.model.issue.Issue>,
    isLoading: Boolean,
    onIssueClick: (com.example.domain.model.issue.Issue) -> Unit, // ✅ 파라미터 추가
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(horizontal = 16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "최근 2주 이슈",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "총 ${issues.size}개 • 클릭하여 상태 변경",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // 내용
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (issues.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "최근 2주간 등록된 이슈가 없습니다",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(issues) { issue ->
                        IssueItemCard(
                            issue = issue,
                            onClick = { onIssueClick(issue) } // ✅ onClick 전달
                        )
                    }
                }
            }
        }
    }
}
// 이슈 아이템 카드
// 이슈 아이템 카드
// 이슈 아이템 카드 (개선된 날짜 중심 버전)
// 이슈 아이템 카드 (클릭 가능)
// 이슈 아이템 카드 (클릭 가능)
@Composable
fun IssueItemCard(
    issue: com.example.domain.model.issue.Issue,
    onClick: () -> Unit // ✅ 타입 명시
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // 클릭 가능
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(android.graphics.Color.parseColor(issue.status.colorHex))
                ) {
                    Text(
                        text = issue.status.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // 날짜 정보 (강조)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 등록일
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "등록일",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = issue.createdDate.take(10),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // 화살표
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )

                // 종료일 (마감일)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "종료일",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = issue.dueDate.take(10),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // 메타 정보 + 클릭 힌트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좌측: 우선순위 + 타입
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 우선순위
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(android.graphics.Color.parseColor(issue.priority.colorHex))
                        )
                        Text(
                            text = issue.priority.displayName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    // 타입
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = issue.type.displayName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // 우측: 클릭 힌트
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "상태 변경",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
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
            val strokeWidth = 30f // 25f -> 30f로 두껍게

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
            val strokeWidth = 30f // 25f -> 30f로 두껍게

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedBottomSheet(
    items: List<com.example.domain.model.unified.UnifiedItem>,
    isLoading: Boolean,
    selectedSources: List<com.example.domain.model.unified.ItemSource>,
    onSourceToggle: (com.example.domain.model.unified.ItemSource) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp)
                .padding(horizontal = 16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "통합 일정",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "총 ${items.size}개",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }

            // 소스 필터 칩
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.example.domain.model.unified.ItemSource.values().forEach { source ->
                    FilterChip(
                        selected = source in selectedSources,
                        onClick = { onSourceToggle(source) },
                        label = {
                            Text(
                                text = source.displayName,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(source.colorHex)),
                                        shape = CircleShape
                                    )
                            )
                        }
                    )
                }
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // 내용
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "선택한 기간에 일정이 없습니다",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items) { item ->
                        UnifiedItemCard(item = item)
                    }
                }
            }
        }
    }
}

// 통합 아이템 카드
@Composable
fun UnifiedItemCard(item: com.example.domain.model.unified.UnifiedItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 소스 배지 + 제목
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // 소스 배지
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(android.graphics.Color.parseColor(item.source.colorHex))
                    ) {
                        Text(
                            text = item.source.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // 제목
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 상태
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(android.graphics.Color.parseColor(item.status.colorHex))
                ) {
                    Text(
                        text = item.status.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // 날짜 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 시작일
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "시작",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = item.startDate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )

                // 종료일
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "종료",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = item.endDate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // 타입별 추가 정보
            when (item) {
                is com.example.domain.model.unified.UnifiedItem.AppIssue -> {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = item.issueType,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = item.assignee ?: "미지정",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                is com.example.domain.model.unified.UnifiedItem.GoogleEvent -> {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // 지역 변수로 할당
                    val meetLink = item.meetingLink
                    val eventLocation = item.location

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Google Meet 링크
                        if (meetLink != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF4285F4)
                                )
                                Text(
                                    text = "Google Meet",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4285F4),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 위치
                        if (eventLocation != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = eventLocation,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                is com.example.domain.model.unified.UnifiedItem.NotionTask -> {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = item.databaseName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
// 상태 변경 다이얼로그
@Composable
fun IssueStatusChangeDialog(
    issue: com.example.domain.model.issue.Issue,
    onStatusSelected: (com.example.domain.model.issue.IssueStatus) -> Unit, // ✅ String → IssueStatus
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "이슈 상태 변경",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = issue.title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "변경할 상태를 선택하세요",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                // 현재 상태
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "현재:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(android.graphics.Color.parseColor(issue.status.colorHex))
                        ) {
                            Text(
                                text = issue.status.displayName,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Divider()

                // 상태 버튼들 (enum 사용)
                com.example.domain.model.issue.IssueStatus.values().forEach { status ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onStatusSelected(status)
                                // onDismiss는 ViewModel에서 자동으로 처리됨
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, Color(android.graphics.Color.parseColor(status.colorHex))),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = status.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(status.colorHex)),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

// 완료 확인 다이얼로그
@Composable
fun IssueCompleteConfirmDialog(
    issue: com.example.domain.model.issue.Issue,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF4CAF50)
            )
        },
        title = {
            Text(
                text = "이슈를 완료하시겠습니까?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = issue.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(android.graphics.Color.parseColor(issue.priority.colorHex))
                            ) {
                                Text(
                                    text = issue.priority.displayName,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondary
                            ) {
                                Text(
                                    text = issue.type.displayName,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "완료된 이슈는 최근 이슈 목록에서 제거됩니다.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("완료")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}