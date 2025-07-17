package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProjectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "프로젝트",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 임시 프로젝트 목록
        val projects = listOf(
            "프로젝트 A",
            "프로젝트 B",
            "프로젝트 C"
        )

        LazyColumn {
            items(projects) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { /* 프로젝트 클릭 */ }
                ) {
                    Text(
                        text = project,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}