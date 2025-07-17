// feature/issue/src/main/java/com/example/issue/IssueScreen.kt
package com.example.issue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IssueScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "이슈",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 임시 이슈 목록
        val issues = listOf(
            "버그 수정 필요",
            "새 기능 추가",
            "성능 개선"
        )

        LazyColumn {
            items(issues) { issue ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { /* 이슈 클릭 */ }
                ) {
                    Text(
                        text = issue,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}