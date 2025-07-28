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
            Log.d("IssueAPI", "검색 쿼리: ${filter.searchQuery}, 상태 필터: ${filter.status}")
            
            val ctx = UserContext.instance
            
            val response = if (filter.searchQuery.isNotEmpty()) {
                // 검색이 있을 때는 getIssueList API 사용
                Log.d("IssueAPI", "검색 API 호출 - 검색어: ${filter.searchQuery}")
                issueApi.getIssueList(
                    srchIssueName = filter.searchQuery,
                    groupCode = ctx.groupCode,
                    loginId = ctx.userId,
                    projectNo = filter.projectId ?: ctx.lastProjectNo,
                    spUid = ctx.spUid
                )
            } else {
                // 검색이 없을 때는 getMyIssueList API 사용
                Log.d("IssueAPI", "나의 이슈 목록 API 호출")
                issueApi.getMyIssueList(
                    projectNo = filter.projectId ?: ctx.lastProjectNo,
                    spUid = ctx.spUid,
                    loginId = ctx.userId,
                    groupCode = ctx.groupCode
                )
            }
            
            Log.d("IssueAPI", "API 성공 - 이슈 개수: ${response.list.size}")
            
            val issues = response.list.map { dto ->
                Issue(
                    id = dto.issueUid,
                    title = dto.issueName,
                    description = "", // API에 설명 필드가 없으므로 빈 값
                    type = mapIssueType(dto.issueTypeName),
                    status = mapIssueStatus(dto.issueStateName),
                    priority = mapIssuePriority(dto.priority),
                    importance = mapIssueImportance(dto.importance),
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
    
    private fun mapIssueType(typeName: String): IssueType {
        return when (typeName) {
            "버그" -> IssueType.BUG
            "기능" -> IssueType.FEATURE
            "작업" -> IssueType.TASK
            "검토" -> IssueType.REVIEW
            else -> IssueType.REVIEW
        }
    }
    
    private fun mapIssueStatus(statusName: String): IssueStatus {
        return when (statusName) {
            "등록" -> IssueStatus.REGISTERED
            "진행" -> IssueStatus.IN_PROGRESS
            "해결" -> IssueStatus.RESOLVED
            "완료" -> IssueStatus.CLOSED
            else -> IssueStatus.REGISTERED
        }
    }
    
    private fun mapIssuePriority(priority: String): IssuePriority {
        return when (priority) {
            "긴급" -> IssuePriority.CRITICAL
            "높음" -> IssuePriority.HIGH
            "보통" -> IssuePriority.NORMAL
            "낮음" -> IssuePriority.LOW
            else -> IssuePriority.NORMAL
        }
    }
    
    private fun mapIssueImportance(importance: String): IssueImportance {
        return when (importance) {
            "심각" -> IssueImportance.CRITICAL
            "높음" -> IssueImportance.HIGH
            "보통" -> IssueImportance.NORMAL
            else -> IssueImportance.NORMAL
        }
    }
} 