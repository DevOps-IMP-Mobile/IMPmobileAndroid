    package com.example.profile

    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ExitToApp
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.hilt.navigation.compose.hiltViewModel

    @Composable
    fun ProfileScreen(
        onLogout: () -> Unit,
        viewModel: ProfileViewModel = hiltViewModel()
    ) {
        val uiState by viewModel.uiState
        var showLogoutDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "프로필",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val currentState = uiState) {
                    is ProfileUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ProfileUiState.Error -> {
                        Text("에러: ${currentState.message}")
                    }
                    is ProfileUiState.Success -> {
                        ProfileContent(
                            userInfo = currentState.userProfile,
                            onLogoutClick = { showLogoutDialog = true }
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("로그아웃") },
                text = { Text("로그아웃 하시겠습니까?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout() // <-- 여기서 로그인 화면으로 이동
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }

    @Composable
    fun ProfileContent(userInfo: UserProfile, onLogoutClick: () -> Unit) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(128.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo.userName?.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = userInfo.userName ?: "사용자",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when (userInfo.groupCode) {
                "SYSMGR" -> "시스템 관리자"
                "ADMIN" -> "관리자"
                "USER" -> "사용자"
                "DEV" -> "개발자"
                else -> userInfo.groupCode ?: "사용자"
            },
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileTextField("사용자 ID", userInfo.userId)
        ProfileTextField("UID", userInfo.userUid)
        ProfileTextField("그룹", userInfo.groupCode)
        ProfileTextField("SP UID", userInfo.spUid)
        ProfileTextField("마지막 프로젝트", userInfo.lastProjectNo)
        ProfileTextField("마지막 접속", userInfo.lastResponseDate)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "로그아웃")
            Spacer(modifier = Modifier.width(8.dp))
            Text("로그아웃")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    @Composable
    fun ProfileTextField(label: String, value: String?) {
        OutlinedTextField(
            value = value ?: "정보 없음",
            onValueChange = {},
            label = { Text(label) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
    }
