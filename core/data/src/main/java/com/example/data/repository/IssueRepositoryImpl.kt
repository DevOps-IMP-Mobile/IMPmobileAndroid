package com.example.data.repository

import android.util.Log
import com.example.domain.model.issue.*
import com.example.domain.repository.IssueRepository
import com.example.network.api.IssueApiService
import com.example.network.api.CodeApiService
import com.example.domain.context.UserContext
import com.example.network.dto.CreateIssueRequest
import com.example.network.dto.UpdateIssueRequest
import com.example.network.dto.DeleteIssueRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IssueRepositoryImpl @Inject constructor(
    private val issueApi: IssueApiService,
    private val codeApi: CodeApiService
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
                    title = dto.issueName.ifEmpty { "제목 없음" },  // 빈 문자열일 경우 기본값 설정
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
    
    override suspend fun createIssue(
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String
    ): Result<Boolean> {
        return try {
            val ctx = UserContext.instance
            Log.d("IssueAPI", "=== 이슈 등록 API 호출 시작 ===")
            Log.d("IssueAPI", "API URL: POST http://211.55.77.168:8402/its/devops/issuemgr/createIssue")
            
            // 날짜 변환 (YYYYMMDD 또는 YYYY-MM-DD 입력 모두 YYYY-MM-DD로 변환)
            fun toDashDate(input: String): String = when {
                input.matches(Regex("\\d{8}")) -> input.substring(0,4) + "-" + input.substring(4,6) + "-" + input.substring(6,8)
                input.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> input
                else -> input // fallback
            }
            val startDash = toDashDate(startDate)
            val endDash = toDashDate(endDate)
            val issueDtIso = startDash + "T15:30:00.000Z" // Postman 예시와 동일하게 15:30:00.000Z 고정

            Log.d("IssueAPI", "이슈 등록 파라미터: title=$title, description=$description, typeId=$typeId, priorityCd=$priorityCd, importanceCd=$importanceCd, startDate=$startDash, endDate=$endDash, issueDt=$issueDtIso, projectNo=$projectNo, loginId=${ctx.userId}, spUid=${ctx.spUid}")

            val response = issueApi.createIssue(
                issueName = title,
                issueDesc = description,
                issueTypeId = typeId,
                priorityCd = priorityCd,  // 드롭다운에서 선택한 값 사용
                importanceCd = importanceCd,  // 드롭다운에서 선택한 값 사용
                endDt = endDash,
                startDt = startDash,
                issueDt = issueDtIso, // 반드시 포함
                loginId = ctx.userId ?: "",
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,  // 선택된 프로젝트 사용
                charger = ctx.userId ?: "",
                chargerName = ctx.userName ?: ""
            )
            
            Log.d("IssueAPI", "이슈 등록 성공 - cnt: ${response.list.firstOrNull()?.cnt}")
            
            val success = response.list.firstOrNull()?.cnt == 1
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("이슈 등록 실패"))
            }
        } catch (e: Exception) {
            Log.e("IssueAPI", "이슈 등록 실패: ${e.message}")
            Result.failure(e)
        }
    }
    
    override suspend fun updateIssue(
        issueId: String,
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String
    ): Result<Boolean> {
        return try {
            Log.d("IssueAPI", "=== 이슈 수정 API 호출 시작 ===")
            Log.d("IssueAPI", "이슈ID: $issueId, 제목: $title")
            Log.d("IssueAPI", "제목 길이: ${title.length}, 제목이 비어있나: ${title.isBlank()}")
            
            // 제목이 비어있으면 오류
            if (title.isBlank()) {
                Log.e("IssueAPI", "이슈 제목이 비어있습니다")
                return Result.failure(Exception("이슈 제목을 입력해주세요"))
            }
            
            val ctx = UserContext.instance
            
            // 날짜 변환 (YYYYMMDD 또는 YYYY-MM-DD 입력 모두 YYYY-MM-DD로 변환)
            fun toDashDate(input: String): String = when {
                input.matches(Regex("\\d{8}")) -> input.substring(0,4) + "-" + input.substring(4,6) + "-" + input.substring(6,8)
                input.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> input
                else -> input // fallback
            }
            val startDash = toDashDate(startDate)
            val endDash = toDashDate(endDate)
            
            // 드롭다운에서 선택한 ID를 그대로 사용 (이미 올바른 ID임)
            val actualTypeId = typeId
            val priorityCode = priorityCd
            val importanceCode = importanceCd
            
            Log.d("IssueAPI", "수정 요청 파라미터 (ID 그대로 사용):")
            Log.d("IssueAPI", "- typeId: $actualTypeId")
            Log.d("IssueAPI", "- priorityCd: $priorityCode")
            Log.d("IssueAPI", "- importanceCd: $importanceCode")
            
            Log.d("IssueAPI", "수정 요청 파라미터:")
            Log.d("IssueAPI", "- issueId: $issueId")
            Log.d("IssueAPI", "- issueName: $title")
            Log.d("IssueAPI", "- issueTypeId: $actualTypeId (원본: $typeId)")
            Log.d("IssueAPI", "- priorityCd: $priorityCode (원본: $priorityCd)")
            Log.d("IssueAPI", "- importanceCd: $importanceCode (원본: $importanceCd)")
            Log.d("IssueAPI", "- startDt: $startDash (원본: $startDate)")
            Log.d("IssueAPI", "- endDt: $endDash (원본: $endDate)")
            
            val response = issueApi.updateIssue(
                issueId = issueId,
                issueName = title.ifEmpty { "제목 없음" },
                issueTypeId = actualTypeId,
                priorityCd = priorityCode,
                importanceCd = importanceCode,
                endDt = endDash,
                issueDesc = description,
                startDt = startDash,
                loginId = ctx.userId ?: "",
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,
                charger = ctx.userId ?: "",
                chargerName = ctx.userName ?: ""
            )
            
            Log.d("IssueAPI", "이슈 수정 성공 - cnt: ${response.list.firstOrNull()?.cnt}")
            
            val success = response.list.firstOrNull()?.cnt == 1
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("이슈 수정 실패"))
            }
        } catch (e: Exception) {
            Log.e("IssueAPI", "이슈 수정 실패: ${e.message}")
            Result.failure(e)
        }
    }
    
    override suspend fun deleteIssue(
        issueId: String,
        projectNo: String
    ): Result<Boolean> {
        return try {
            Log.d("IssueAPI", "=== 이슈 삭제 API 호출 시작 ===")
            Log.d("IssueAPI", "이슈ID: $issueId, 프로젝트: $projectNo")
            
            val ctx = UserContext.instance
            
            Log.d("IssueAPI", "삭제 요청 파라미터:")
            Log.d("IssueAPI", "- groupCode: SVCMGR")
            Log.d("IssueAPI", "- issueId: $issueId")
            Log.d("IssueAPI", "- loginId: ${ctx.userId}")
            Log.d("IssueAPI", "- projectNo: $projectNo")
            Log.d("IssueAPI", "- spUid: ${ctx.spUid}")
            
            val response = issueApi.deleteIssue(
                groupCode = "SVCMGR", // 서비스 관리자 권한으로 시도
                issueId = issueId,
                loginId = ctx.userId ?: "",
                projectNo = projectNo,
                spUid = ctx.spUid ?: ""
            )
            Log.d("IssueAPI", "이슈 삭제 성공 - cnt: ${response.list.firstOrNull()?.cnt}")
            
            val success = response.list.firstOrNull()?.cnt == 1
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("이슈 삭제 실패"))
            }
        } catch (e: Exception) {
            Log.e("IssueAPI", "이슈 삭제 실패: ${e.message}")
            Result.failure(e)
        }
    }
    
    override suspend fun getIssueOptions(projectNo: String): Result<IssueOptions> {
        return try {
            Log.d("IssueAPI", "=== 이슈 옵션 조회 시작 ===")
            Log.d("IssueAPI", "프로젝트: $projectNo")
            
            val ctx = UserContext.instance
            
            // 이슈 타입 조회
            val issueTypeResponse = issueApi.getIssueTypeList(
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,
                loginId = ctx.userId ?: "",
                idChk = "Y"
            )
            Log.d("IssueAPI", "이슈 타입 응답: $issueTypeResponse")
            
            // 우선순위 조회 (고정)
            val priorityResponse = codeApi.getPriorityList(
                codeGroupId = "CG_PRIORITY",
                spUid = ctx.spUid ?: ""
            )
            Log.d("IssueAPI", "우선순위 응답: $priorityResponse")
            
            // 중요도 조회 (고정)
            val importanceResponse = codeApi.getImportanceList(
                codeGroupId = "CG_IMPORTANCE",
                spUid = ctx.spUid ?: ""
            )
            Log.d("IssueAPI", "중요도 응답: $importanceResponse")
            
            val issueTypes = issueTypeResponse.list.map { dto ->
                IssueTypeOption(
                    id = dto.issueTypeId,
                    name = dto.issueTypeName,
                    description = dto.issueTypeDesc,
                    isActive = dto.useYn == "Y",
                    sortOrder = dto.sortOrder
                )
            }
            
            val priorities = priorityResponse.list
                .filter { !it.codeId.isNullOrBlank() }
                .map { dto ->
                    PriorityOption(
                        id = dto.codeId!!, // 반드시 codeId 사용
                        name = dto.codeName,
                        description = dto.codeDesc,
                        isActive = dto.useYn == "Y",
                        sortOrder = dto.sortOrder
                    )
                }

            val importances = importanceResponse.list
                .filter { !it.codeId.isNullOrBlank() }
                .map { dto ->
                    ImportanceOption(
                        id = dto.codeId!!, // 반드시 codeId 사용
                        name = dto.codeName,
                        description = dto.codeDesc,
                        isActive = dto.useYn == "Y",
                        sortOrder = dto.sortOrder
                    )
                }
            
            val issueOptions = IssueOptions(
                issueTypes = issueTypes,
                priorities = priorities,
                importances = importances
            )
            
            Log.d("IssueAPI", "이슈 옵션 조회 완료")
            Log.d("IssueAPI", "이슈 타입: ${issueOptions.issueTypes.size}개")
            Log.d("IssueAPI", "우선순위: ${issueOptions.priorities.size}개")
            Log.d("IssueAPI", "중요도: ${issueOptions.importances.size}개")
            
            Result.success(issueOptions)
        } catch (e: Exception) {
            Log.e("IssueAPI", "이슈 옵션 조회 실패: ${e.message}")
            Result.failure(e)
        }
    }
    
    private fun mapIssueType(typeName: String?): IssueType {
        return when (typeName) {
            "버그", "결함" -> IssueType.BUG
            "기능", "요구사항" -> IssueType.FEATURE
            "작업", "테스트 케이스" -> IssueType.TASK
            "검토" -> IssueType.REVIEW
            null, "" -> IssueType.BUG  // null이나 빈 값일 때 기본값
            else -> IssueType.BUG  // 알 수 없는 타입일 때도 기본값
        }
    }
    
    private fun mapIssueStatus(statusName: String?): IssueStatus {
        return when (statusName) {
            "등록" -> IssueStatus.REGISTERED
            "진행" -> IssueStatus.IN_PROGRESS
            "해결" -> IssueStatus.RESOLVED
            "완료" -> IssueStatus.CLOSED
            "확인" -> IssueStatus.IN_PROGRESS
            "승인" -> IssueStatus.IN_PROGRESS
            null, "" -> IssueStatus.REGISTERED  // null이나 빈 값일 때 기본값
            else -> IssueStatus.REGISTERED  // 알 수 없는 상태일 때도 기본값
        }
    }
    
    private fun mapIssuePriority(priority: String?): IssuePriority {
        return when (priority) {
            "긴급" -> IssuePriority.CRITICAL
            "높음" -> IssuePriority.HIGH
            "보통" -> IssuePriority.NORMAL
            "낮음" -> IssuePriority.LOW
            null, "" -> IssuePriority.NORMAL  // null이나 빈 값일 때 기본값
            else -> IssuePriority.NORMAL  // 알 수 없는 우선순위일 때도 기본값
        }
    }
    
    private fun mapIssueImportance(importance: String?): IssueImportance {
        return when (importance) {
            "심각" -> IssueImportance.CRITICAL
            "높음" -> IssueImportance.HIGH
            "보통" -> IssueImportance.NORMAL
            "낮음" -> IssueImportance.LOW
            null, "" -> IssueImportance.NORMAL  // null이나 빈 값일 때 기본값
            else -> IssueImportance.NORMAL  // 알 수 없는 중요도일 때도 기본값
        }
    }
} 