package com.example.domain.usecase.issue

import com.example.domain.model.issue.*
import com.example.domain.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 이슈 목록을 가져오는 UseCase
 */
class GetIssueListUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(
        filter: IssueFilter = IssueFilter(),
        sortType: IssueSortType = IssueSortType.PRIORITY
    ): Flow<List<Issue>> = issueRepository.getIssueList(filter, sortType)
}

/**
 * 프로젝트 목록을 가져오는 UseCase (이슈 화면용)
 */
class GetProjectsForIssueUseCase @Inject constructor(
    // TODO: 실제 Repository 주입
) {
    operator fun invoke(): Flow<List<Pair<String, String>>> = kotlinx.coroutines.flow.flow {
        kotlinx.coroutines.delay(300)

        // Mock 프로젝트 데이터 (id, name)
        val mockProjects = listOf(
            "demo" to "데모 프로젝트",
            "demo2" to "데모 프로젝트2",
            "test124" to "test124"
        )

        emit(mockProjects)
    }
}