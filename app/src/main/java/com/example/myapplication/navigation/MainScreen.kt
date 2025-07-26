package com.example.myapplication.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.home.HomeScreen
import com.example.project.ProjectScreen
import com.example.issue.IssueScreen
import com.example.profile.ProfileScreen

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
                ProjectScreen() // 파라미터 제거
            }

            composable(Screen.Issues.route) {
                IssueScreen() // 파라미터 제거
            }

            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = onLogout) // 파라미터 제거
            }
        }
    }
}