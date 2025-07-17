// Screen.kt 아이콘 수정
package com.example.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // 인증 관련
    object Login : Screen("login", "로그인")

    // 메인 화면들 (바텀 네비게이션)
    object Home : Screen("home", "홈", Icons.Default.Home)
    object Projects : Screen("projects", "프로젝트", Icons.Default.Build)
    object Issues : Screen("issues", "이슈", Icons.Default.List)
    object Profile : Screen("profile", "프로필", Icons.Default.Person)

    // 세부 화면들
    object ProjectDetail : Screen("project_detail/{projectId}", "프로젝트 상세") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }

    object IssueDetail : Screen("issue_detail/{issueId}", "이슈 상세") {
        fun createRoute(issueId: String) = "issue_detail/$issueId"
    }
}

// 바텀 네비게이션 아이템들
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Projects,
    Screen.Issues,
    Screen.Profile
)