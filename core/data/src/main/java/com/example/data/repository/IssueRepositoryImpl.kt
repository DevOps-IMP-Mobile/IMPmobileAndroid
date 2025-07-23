package com.example.data.repository

import android.util.Log
import com.example.domain.model.issue.*
import com.example.domain.repository.IssueRepository
import com.example.network.api.IssueApiService
import com.example.domain.context.UserContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IssueRepositoryImpl @Inject constructor(
    private val issueApi: IssueApiService
) : IssueRepository {
    override suspend fun getIssueList(
        filter: IssueFilter,
        sortType: IssueSortType
    ): Flow<List<Issue>> = flow {
        try {
            Log.d("IssueAPI", "=== 이슈 목록 API 호출 시작 ===")
            val ctx = UserContext.instance
            val response = issueApi.getMyIssueList(
                projectNo = filter.projectId ?: ctx.lastProjectNo,
                spUid = ctx.spUid,
                loginId = ctx.userId,
                groupCode = ctx.groupCode
                // 필요시 추가 파라미터 전달
            )
            Log.d("IssueAPI", "API 성공 - 이슈 개수: ${response.list.size}")
            val issues = response.list.map { dto ->
                Issue(
                    id = dto.issueUid,
                    title = dto.issueName,
                    description = "", // API에 설명 필드가 없으므로 빈 값
                    type = IssueType.REVIEW, // 실제 매핑 필요시 추가
                    status = IssueStatus.REGISTERED, // 실제 매핑 필요시 추가
                    priority = IssuePriority.NORMAL, // 실제 매핑 필요시 추가
                    importance = IssueImportance.NORMAL, // 실제 매핑 필요시 추가
                    assigneeId = "", // 필요시 매핑
                    assigneeName = dto.chargerName,
                    reporterId = "",
                    reporterName = "",
                    createdDate = dto.crtrDtYYYYMMDD,
                    dueDate = dto.endDt,
                    repository = "",
                    projectId = dto.projectNo
                )
            }
            emit(issues)
        } catch (e: Exception) {
            Log.e("IssueAPI", "API 실패: ${e.message}")
            emit(emptyList())
        }
    }
} 