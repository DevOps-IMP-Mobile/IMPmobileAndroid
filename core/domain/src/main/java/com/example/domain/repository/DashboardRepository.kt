// core/domain/src/main/java/com/example/domain/repository/DashboardRepository.kt
package com.example.domain.repository

import com.example.domain.model.home.DashboardData
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    suspend fun getDashboardData(selectedProject: com.example.domain.model.home.Project?): Flow<DashboardData>
}