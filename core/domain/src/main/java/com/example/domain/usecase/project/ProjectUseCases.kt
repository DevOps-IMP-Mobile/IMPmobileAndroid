package com.example.domain.usecase.project

import com.example.domain.model.project.Project
import com.example.domain.model.project.ProjectMember
import com.example.domain.model.project.ProjectStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * 프로젝트 목록을 가져오는 UseCase
 */
class GetProjectListUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(): Flow<List<Project>> = flow {
        // 로딩 상태를 위한 지연
        delay(800)
        
        // Mock 데이터 생성
        val mockProjects = listOf(
            Project(
                id = "test124",
                name = "test124",
                description = "테스트 프로젝트",
                startDate = "2025-06-30",
                endDate = "2025-07-04",
                status = ProjectStatus.RUNNING,
                memberCount = 1,
                managerId = "test1234",
                managerName = "test124"
            ),
            Project(
                id = "demo2",
                name = "데모 프로젝트2",
                description = "데모용 프로젝트",
                startDate = "2025-06-03",
                endDate = "2025-06-03",
                status = ProjectStatus.RUNNING,
                memberCount = 1,
                managerId = "demo_user",
                managerName = "기본사용자"
            ),
            Project(
                id = "demo",
                name = "데모 프로젝트",
                description = "웹 어플리케이션 개발",
                startDate = "2025-05-01",
                endDate = "2025-12-31",
                status = ProjectStatus.RUNNING,
                memberCount = 3,
                managerId = "demo_user",
                managerName = "기본사용자"
            )
        )
        
        emit(mockProjects)
    }
}

/**
 * 특정 프로젝트의 팀 멤버를 가져오는 UseCase
 */
class GetProjectMembersUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(projectId: String): Flow<List<ProjectMember>> = flow {
        delay(500)
        
        // Mock 데이터 생성 (프로젝트별로 다른 데이터)
        val mockMembers = when (projectId) {
            "demo" -> listOf(
                ProjectMember(
                    id = "1",
                    name = "기본사용자",
                    userId = "demo_user",
                    position = "관리자",
                    role = "프로젝트관리자"
                ),
                ProjectMember(
                    id = "2", 
                    name = "데모사용자1",
                    userId = "demo_user_1",
                    position = "사원",
                    role = "팀원"
                ),
                ProjectMember(
                    id = "3",
                    name = "데모사용자2", 
                    userId = "demo_user_2",
                    position = "사원",
                    role = "팀원"
                )
            )
            else -> listOf(
                ProjectMember(
                    id = "1",
                    name = "기본사용자",
                    userId = "demo_user", 
                    position = "관리자",
                    role = "프로젝트관리자"
                )
            )
        }
        
        emit(mockMembers)
    }
}