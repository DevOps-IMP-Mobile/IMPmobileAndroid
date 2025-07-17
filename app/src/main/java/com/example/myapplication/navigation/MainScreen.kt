package com.example.myapplication.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.home.HomeScreen

// 임시 화면들 (실제 모듈이 완성되기 전까지 사용)
@Composable
fun TempProjectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "프로젝트 화면",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun TempIssueScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "이슈 화면",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun TempProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "프로필 화면",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그아웃")
        }
    }
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onLogout = onLogout)
            }

            composable(Screen.Projects.route) {
                TempProjectScreen()
            }

            composable(Screen.Issues.route) {
                TempIssueScreen()
            }

            composable(Screen.Profile.route) {
                TempProfileScreen(onLogout = onLogout)
            }
        }
    }
}