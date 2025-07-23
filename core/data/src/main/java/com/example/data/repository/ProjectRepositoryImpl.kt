package com.example.data.repository

import android.util.Log
import com.example.domain.model.project.Project
import com.example.domain.model.project.ProjectStatus
import com.example.domain.repository.ProjectRepository
import com.example.network.api.ProjectApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectApi: ProjectApiService
) : ProjectRepository {
    override suspend fun getProjectList(): Flow<List<Project>> = flow {
        try {
            Log.d("ProjectAPI", "=== 프로젝트 목록 API 호출 시작 ===")
            val response = projectApi.getProjectList()
            Log.d("ProjectAPI", "API 성공 - 프로젝트 개수: ${response.list.size}")
            val projects = response.list.map { dto ->
                Project(
                    id = dto.projectNo,
                    name = dto.projectName,
                    description = "", // API에 설명 필드가 없으므로 빈 값
                    startDate = dto.startDate ?: "",
                    endDate = dto.endDate ?: "",
                    status = ProjectStatus.RUNNING, // 상태 매핑 필요시 추가
                    memberCount = 0, // 추후 멤버 API 연동 시 수정
                    managerId = dto.managerId ?: "",
                    managerName = dto.managerName ?: "",
                    isStandard = dto.standardYn == "Y"
                )
            }
            emit(projects)
        } catch (e: Exception) {
            Log.e("ProjectAPI", "API 실패: ${e.message}")
            emit(emptyList())
        }
    }
} 