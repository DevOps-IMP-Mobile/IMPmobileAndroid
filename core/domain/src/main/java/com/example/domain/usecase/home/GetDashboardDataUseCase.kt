package com.example.domain.usecase.home

import com.example.domain.model.home.DashboardData
import com.example.domain.model.home.TodayTaskStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 대시보드 데이터를 가져오는 UseCase
 */
class GetDashboardDataUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(): Flow<DashboardData> = flow {
        // 로딩 상태를 위한 지연
        delay(500)
        
        // Mock 데이터 생성
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val mockData = DashboardData(
            todayTasks = TodayTaskStats(
                inProgress = 3,
                delayed = 1,
                registered = 2
            ),
            statusChart = emptyList(), // TODO: 실제 차트 데이터
            typeChart = emptyList(),   // TODO: 실제 차트 데이터
            currentDate = currentDate
        )
        
        emit(mockData)
    }
}