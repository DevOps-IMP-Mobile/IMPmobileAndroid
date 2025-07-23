// core/data/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
package com.example.data.repository

import android.util.Log
import com.example.domain.model.home.DashboardData
import com.example.domain.model.home.StatusChartData
import com.example.domain.model.home.TodayTaskStats
import com.example.domain.model.home.TypeChartData
import com.example.domain.repository.DashboardRepository
import com.example.network.api.DashboardApiService
import com.example.network.dto.DashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardApi: DashboardApiService
) : DashboardRepository {

    override suspend fun getDashboardData(): Flow<DashboardData> = flow {
        try {
            Log.d("DashboardAPI", "=== API 호출 시작 ===")
            val response = dashboardApi.getMyIssueList()
            Log.d("DashboardAPI", "API 성공 - 이슈 개수: ${response.listCnt}")
            val dashboardData = mapToDashboardData(response)
            Log.d("DashboardAPI", "변환된 데이터 - 진행:${dashboardData.todayTasks.inProgress}, 지연:${dashboardData.todayTasks.delayed}")
            emit(dashboardData)
        } catch (e: Exception) {
            Log.e("DashboardAPI", "API 실패 - Mock 데이터 사용: ${e.message}")
            emit(getMockDashboardData())
        }
    }

    private fun mapToDashboardData(response: DashboardResponse): DashboardData {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 오늘의 할일 계산
        val todayTasks = TodayTaskStats(
            inProgress = response.list.count { !isCompleteStatus(it.issueStateName) },
            delayed = response.list.count { it.isn == "지연" },
            registered = response.list.count { it.crtrDtYYYYMMDD.contains(currentDate) }
        )

        // 상태별 차트 데이터
        val statusGrouped = response.list.groupBy { it.issueStateName }
        val statusChart = statusGrouped.map { entry ->
            val status = entry.key
            val issues = entry.value
            StatusChartData(
                label = status,
                count = issues.count(),
                color = issues.firstOrNull()?.issueStateColor ?: "#666666"
            )
        }

        // 타입별 차트 데이터
        val typeGrouped = response.list.groupBy { it.issueTypeName }
        val typeChart = mutableListOf<TypeChartData>()
        var index = 0
        for (entry in typeGrouped) {
            val type = entry.key
            val issues = entry.value
            typeChart.add(
                TypeChartData(
                    label = type,
                    count = issues.count(),
                    color = getTypeColor(index)
                )
            )
            index++
        }

        return DashboardData(
            todayTasks = todayTasks,
            statusChart = statusChart,
            typeChart = typeChart,
            currentDate = currentDate
        )
    }

    private fun isCompleteStatus(status: String): Boolean {
        return status in listOf("완료", "해결", "종료", "닫힘")
    }

    private fun getTypeColor(index: Int): String {
        val colors = listOf("#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD")
        return colors[index % colors.count()]
    }

    private fun getMockDashboardData(): DashboardData {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return DashboardData(
            todayTasks = TodayTaskStats(
                inProgress = 3,
                delayed = 1,
                registered = 2
            ),
            statusChart = listOf(
                StatusChartData("등록", 2, "#FF6B6B"),
                StatusChartData("진행", 3, "#4ECDC4"),
                StatusChartData("완료", 1, "#45B7D1")
            ),
            typeChart = listOf(
                TypeChartData("버그", 3, "#FF6B6B"),
                TypeChartData("기능", 2, "#4ECDC4"),
                TypeChartData("개선", 1, "#45B7D1")
            ),
            currentDate = currentDate
        )
    }
}