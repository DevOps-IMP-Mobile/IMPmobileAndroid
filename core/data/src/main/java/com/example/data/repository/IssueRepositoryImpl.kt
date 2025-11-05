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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.example.data.notification.IssueNotificationManager
class IssueRepositoryImpl @Inject constructor(
    private val issueApi: IssueApiService,
    private val codeApi: CodeApiService,
    private val notificationManager: IssueNotificationManager
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
                    title = dto.issueName.ifEmpty { "제목 없음" },
                    description = "",

                    // 화면 표시용 enum
                    type = mapIssueType(dto.issueTypeName),
                    status = mapIssueStatus(dto.issueStateName),
                    priority = mapIssuePriority(dto.priority),
                    importance = mapIssueImportance(dto.importance),

                    // ✅ 실제 API ID/코드 추가
                    typeId = dto.issueTypeId,          // API 응답의 실제 타입 ID
                    statusId = dto.issueStateId,           // API 응답의 실제 상태 ID
                    priorityCd = dto.priorityCd,           // API 응답의 실제 우선순위 코드
                    importanceCd = dto.importanceCd,       // API 응답의 실제 중요도 코드

                    assigneeId = "",
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

    /**
     * 최근 2주간 생성된 이슈 조회
     * API 응답에서 전체 이슈를 가져온 후 클라이언트에서 필터링
     */
    suspend fun getRecentIssues(projectNo: String): Flow<List<Issue>> = flow {
        try {
            Log.d("IssueAPI", "=== 최근 이슈 조회 시작 ===")
            val ctx = UserContext.instance

            // 전체 이슈 조회 (Dashboard API 사용)
            val response = issueApi.getMyIssueList(
                projectNo = projectNo,
                spUid = ctx.spUid,
                loginId = ctx.userId,
                groupCode = ctx.groupCode
            )

            Log.d("IssueAPI", "전체 이슈 조회 성공 - 총 ${response.list.size}개")

            // 2주 전 날짜 계산
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -14)
            val twoWeeksAgo = calendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val twoWeeksAgoStr = dateFormat.format(twoWeeksAgo)

            Log.d("IssueAPI", "2주 전 날짜: $twoWeeksAgoStr")

            // 최근 2주 이슈 필터링 (crtrDtYYYYMMDD 기준)
            val recentIssues = response.list
                .filter { dto ->
                    try {
                        // crtrDtYYYYMMDD는 "yyyy-MM-dd HH:mm:ss" 형식
                        val issueDateStr = dto.crtrDtYYYYMMDD.substring(0, 10) // "yyyy-MM-dd" 부분만 추출
                        val issueDate = dateFormat.parse(issueDateStr)
                        issueDate != null && !issueDate.before(twoWeeksAgo)
                    } catch (e: Exception) {
                        Log.e("IssueAPI", "날짜 파싱 실패: ${dto.crtrDtYYYYMMDD}", e)
                        false
                    }
                }
                .map { dto ->
                    Issue(
                        id = dto.issueUid,
                        title = dto.issueName.ifEmpty { "제목 없음" },
                        description = "",

                        type = mapIssueType(dto.issueTypeName),
                        status = mapIssueStatus(dto.issueStateName),
                        priority = mapIssuePriority(dto.priority),
                        importance = mapIssueImportance(dto.importance),

                        // ✅ 실제 API ID/코드 추가
                        typeId = dto.issueTypeId,
                        statusId = dto.issueStateId,
                        priorityCd = dto.priorityCd,
                        importanceCd = dto.importanceCd,

                        assigneeId = "",
                        assigneeName = dto.chargerName,
                        reporterId = "",
                        reporterName = "",
                        createdDate = dto.crtrDtYYYYMMDD,
                        dueDate = dto.endDt,
                        repository = "",
                        projectId = dto.projectNo
                    )
                }
                .sortedByDescending { it.createdDate } // 최신순 정렬

            Log.d("IssueAPI", "최근 2주 이슈 필터링 완료 - ${recentIssues.size}개")
            recentIssues.take(5).forEach {
                Log.d("IssueAPI", "- ${it.title} (${it.createdDate})")
            }

            emit(recentIssues)
        } catch (e: Exception) {
            Log.e("IssueAPI", "최근 이슈 조회 실패: ${e.message}")
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
            Log.d("IssueAPI", "API URL: POST http://coverdreamit.iptime.org:8402/its/devops/issuemgr/createIssue")

            // 날짜 변환 (YYYYMMDD 또는 YYYY-MM-DD 입력 모두 YYYY-MM-DD로 변환)
            fun toDashDate(input: String): String = when {
                input.matches(Regex("\\d{8}")) -> input.substring(0,4) + "-" + input.substring(4,6) + "-" + input.substring(6,8)
                input.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> input
                else -> input // fallback
            }
            val startDash = toDashDate(startDate)
            val endDash = toDashDate(endDate)
            val issueDtIso = startDash + "T15:30:00.000Z"

            Log.d("IssueAPI", "이슈 등록 파라미터: title=$title, description=$description, typeId=$typeId, priorityCd=$priorityCd, importanceCd=$importanceCd, startDate=$startDash, endDate=$endDash, issueDt=$issueDtIso, projectNo=$projectNo, loginId=${ctx.userId}, spUid=${ctx.spUid}")

            val response = issueApi.createIssue(
                issueName = title,
                issueDesc = description,
                issueTypeId = typeId,
                priorityCd = priorityCd,
                importanceCd = importanceCd,
                endDt = endDash,
                startDt = startDash,
                issueDt = issueDtIso,
                loginId = ctx.userId ?: "",
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,
                charger = ctx.userId ?: "",
                chargerName = ctx.userName ?: ""
            )

            Log.d("IssueAPI", "이슈 등록 성공 - cnt: ${response.list.firstOrNull()?.cnt}")

            val success = response.list.firstOrNull()?.cnt == 1
            if (success) {
                // ✅ 알림 코드
                try {
                    val priority = mapIssuePriorityFromCode(priorityCd)
                    notificationManager.showIssueCreatedNotification(
                        issueId = title,
                        issueTitle = title,
                        priority = priority,
                        projectName = null
                    )
                    Log.d("IssueAPI", "이슈 생성 알림 표시 완료")
                } catch (e: Exception) {
                    Log.e("IssueAPI", "알림 표시 실패: ${e.message}")
                }
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
        projectNo: String,
        statusCd: String?
    ): Result<Boolean> {
        return try {
            Log.d("IssueAPI", "=== 이슈 수정 API 호출 시작 ===")
            Log.d("IssueAPI", "이슈ID: $issueId, 제목: $title")
            Log.d("IssueAPI", "제목 길이: ${title.length}, 제목이 비어있나: ${title.isBlank()}")

            if (title.isBlank()) {
                Log.e("IssueAPI", "이슈 제목이 비어있습니다")
                return Result.failure(Exception("이슈 제목을 입력해주세요"))
            }

            val ctx = UserContext.instance

            fun toDashDate(input: String): String = when {
                input.matches(Regex("\\d{8}")) -> input.substring(0,4) + "-" + input.substring(4,6) + "-" + input.substring(6,8)
                input.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> input
                else -> input
            }
            val startDash = toDashDate(startDate)
            val endDash = toDashDate(endDate)

            val actualTypeId = typeId
            val priorityCode = priorityCd
            val importanceCode = importanceCd

            Log.d("IssueAPI", "수정 요청 파라미터 (ID 그대로 사용):")
            Log.d("IssueAPI", "- typeId: $actualTypeId")
            Log.d("IssueAPI", "- priorityCd: $priorityCode")
            Log.d("IssueAPI", "- importanceCd: $importanceCode")

            val response = issueApi.updateIssue(
                issueId = issueId,
                issueName = title,
                issueDesc = description,
                issueTypeId = actualTypeId,
                priorityCd = priorityCode,
                importanceCd = importanceCode,
                endDt = endDash,
                startDt = startDash,
                loginId = ctx.userId ?: "",
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,
                charger = ctx.userId ?: "",
                chargerName = ctx.userName ?: "",
                issueStateId = statusCd  // ✅ 추가 (API가 issue_state_id로 받는다고 가정)
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
            val ctx = UserContext.instance
            Log.d("IssueAPI", "=== 이슈 삭제 API 호출 시작 ===")
            Log.d("IssueAPI", "issueId=$issueId, projectNo=$projectNo")

            val response = issueApi.deleteIssue(
                groupCode = ctx.groupCode ?: "",
                issueId = issueId,
                loginId = ctx.userId ?: "",
                projectNo = projectNo,
                spUid = ctx.spUid ?: ""
            )

            Log.d("IssueAPI", "이슈 삭제 성공")
            Result.success(true)
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

            val issueTypeResponse = issueApi.getIssueTypeList(
                spUid = ctx.spUid ?: "",
                projectNo = projectNo,
                loginId = ctx.userId ?: "",
                idChk = "Y"
            )
            Log.d("IssueAPI", "이슈 타입 응답: $issueTypeResponse")

            val priorityResponse = codeApi.getPriorityList(
                codeGroupId = "CG_PRIORITY",
                spUid = ctx.spUid ?: ""
            )
            Log.d("IssueAPI", "우선순위 응답: $priorityResponse")

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
                        id = dto.codeId!!,
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
                        id = dto.codeId!!,
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
            null, "" -> IssueType.BUG
            else -> IssueType.BUG
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
            null, "" -> IssueStatus.REGISTERED
            else -> IssueStatus.REGISTERED
        }
    }

    private fun mapIssuePriority(priority: String?): IssuePriority {
        return when (priority) {
            "긴급" -> IssuePriority.CRITICAL
            "높음" -> IssuePriority.HIGH
            "보통" -> IssuePriority.NORMAL
            "낮음" -> IssuePriority.LOW
            null, "" -> IssuePriority.NORMAL
            else -> IssuePriority.NORMAL
        }
    }

    private fun mapIssueImportance(importance: String?): IssueImportance {
        return when (importance) {
            "심각" -> IssueImportance.CRITICAL
            "높음" -> IssueImportance.HIGH
            "보통" -> IssueImportance.NORMAL
            "낮음" -> IssueImportance.LOW
            null, "" -> IssueImportance.NORMAL
            else -> IssueImportance.NORMAL
        }
    }
    /**
     * 우선순위 코드를 IssuePriority enum으로 변환
     */
    private fun mapIssuePriorityFromCode(priorityCode: String): IssuePriority {
        return when (priorityCode) {
            "001" -> IssuePriority.CRITICAL  // 긴급
            "002" -> IssuePriority.HIGH      // 높음
            "003" -> IssuePriority.NORMAL    // 보통
            "004" -> IssuePriority.LOW       // 낮음
            else -> IssuePriority.NORMAL     // 기본값
        }
    }
}