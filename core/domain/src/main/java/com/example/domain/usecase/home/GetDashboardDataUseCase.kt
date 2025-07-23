package com.example.domain.usecase.home

import com.example.domain.model.home.DashboardData
import com.example.domain.model.home.TodayTaskStats
import com.example.domain.repository.DashboardRepository
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
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(): Flow<DashboardData> {
        return dashboardRepository.getDashboardData()
    }
}