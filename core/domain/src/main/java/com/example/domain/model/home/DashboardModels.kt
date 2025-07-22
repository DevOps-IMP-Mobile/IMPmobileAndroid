package com.example.domain.model.home

/**
 * 오늘의 할일 통계 데이터
 */
data class TodayTaskStats(
    val inProgress: Int = 0,    // 진행중
    val delayed: Int = 0,       // 지연
    val registered: Int = 0     // 등록
)

/**
 * 상태별 차트 데이터 
 */
data class StatusChartData(
    val label: String,
    val count: Int,
    val color: String
)

/**
 * 타입별 차트 데이터
 */
data class TypeChartData(
    val label: String,
    val count: Int,
    val color: String
)

/**
 * 대시보드 전체 데이터
 */
data class DashboardData(
    val todayTasks: TodayTaskStats = TodayTaskStats(),
    val statusChart: List<StatusChartData> = emptyList(),
    val typeChart: List<TypeChartData> = emptyList(),
    val currentDate: String = ""
)