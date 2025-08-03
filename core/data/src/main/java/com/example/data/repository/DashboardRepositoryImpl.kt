// core/data/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
package com.example.data.repository

import android.util.Log
import com.example.domain.model.home.DashboardData
import com.example.domain.model.home.StatusChartData
import com.example.domain.model.home.TodayTaskStats
import com.example.domain.model.home.TypeChartData
import com.example.domain.repository.DashboardRepository
import com.example.network.api.DashboardApiService
import com.example.network.dto.DashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardApi: DashboardApiService
) : DashboardRepository {

    override suspend fun getDashboardData(selectedProject: com.example.domain.model.home.Project?): Flow<DashboardData> = flow {
        try {
            Log.d("DashboardAPI", "=== API 호출 시작 ===")
            Log.d("DashboardAPI", "선택된 프로젝트: ${selectedProject?.projectName} (${selectedProject?.projectNo})")
            
            // UserContext에서 사용자 정보 가져오기
            val ctx = com.example.domain.context.UserContext.instance
            Log.d("DashboardAPI", "사용자 정보 - userId: ${ctx.userId}, spUid: ${ctx.spUid}, groupCode: ${ctx.groupCode}")
            
            // 모든 프로젝트에서 데이터 수집
            val testProjects = listOf("", "demo", "demo2", "test12454")
            val allIssues = mutableListOf<com.example.network.dto.IssueApiDto>()
            val allProjects = mutableSetOf<com.example.domain.model.home.Project>()
            
            for (testProject in testProjects) {
                Log.d("DashboardAPI", "====== 이슈 목록 API 테스트 ======")
                Log.d("DashboardAPI", "시도 중: project_no='$testProject'")
                Log.d("DashboardAPI", "API URL: GET its/devops/issuemgr/getMyIssueList")
                Log.d("DashboardAPI", "sp_uid: '${ctx.spUid ?: ""}'")
                Log.d("DashboardAPI", "loginId: '${ctx.userId ?: ""}'")
                Log.d("DashboardAPI", "groupCode: '${ctx.groupCode ?: "SVCMGR"}'")
                Log.d("DashboardAPI", "===================================")
                
                val response = dashboardApi.getMyIssueList(
                    projectNo = testProject,
                    spUid = ctx.spUid ?: "",
                    loginId = ctx.userId ?: "",
                    groupCode = ctx.groupCode ?: "SVCMGR"
                )
                
                Log.d("DashboardAPI", "project_no='$testProject' 결과: 이슈 ${response.listCnt}개")
                Log.d("DashboardAPI", "응답 JSON: {\"list_cnt\": ${response.listCnt}, \"total_cnt\": ${response.totalCnt}, \"list\": [${response.list.size}개]}")
                
                if (response.list.isNotEmpty()) {
                    Log.d("DashboardAPI", "✅ 데이터 발견! project_no='$testProject'에서 ${response.list.size}개 이슈")
                    
                    // 모든 이슈 수집
                    allIssues.addAll(response.list)
                    
                    // 프로젝트 정보 추출
                    response.list.forEach { issue ->
                        val projectName = issue.projectName ?: "프로젝트 ${issue.projectNo}"
                        allProjects.add(com.example.domain.model.home.Project(issue.projectNo, projectName))
                    }
                    
                    // 샘플 이슈 데이터 로그
                    response.list.take(2).forEachIndexed { index, issue ->
                        Log.d("DashboardAPI", "[샘플 이슈 $index] ${issue.issueName}")
                        Log.d("DashboardAPI", "  - 프로젝트: ${issue.projectName} (${issue.projectNo})")
                        Log.d("DashboardAPI", "  - 상태: ${issue.issueStateName}, 타입: ${issue.issueTypeName}")
                        Log.d("DashboardAPI", "  - 등록일: ${issue.crtrDtYYYYMMDD}, 지연: ${issue.isn}")
                    }
                } else {
                    Log.d("DashboardAPI", "project_no='$testProject'에서 데이터 없음")
                }
            }
            
            if (allIssues.isEmpty()) {
                Log.w("DashboardAPI", "⚠️ 모든 프로젝트에서 데이터 없음")
                Log.d("DashboardAPI", "가능한 원인:")
                Log.d("DashboardAPI", "1. 계정 권한 문제")
                Log.d("DashboardAPI", "2. 실제 데이터 없음")
                Log.d("DashboardAPI", "3. 파라미터 누락")
                Log.d("DashboardAPI", "Mock 데이터로 UI 테스트 진행")
                
                val mockData = getMockDashboardData(selectedProject)
                emit(mockData)
                return@flow
            }
            
            // 전체 이슈 데이터로 응답 구성
            val combinedResponse = DashboardResponse(
                listCnt = allIssues.size.toString(),
                totalCnt = allIssues.size.toString(),
                list = allIssues
            )
            
            Log.d("DashboardAPI", "전체 수집 결과: ${allIssues.size}개 이슈, ${allProjects.size}개 프로젝트")
            
            // 프로젝트 목록 정리
            val projects = allProjects.toList().sortedBy { it.projectName }
            
            Log.d("DashboardAPI", "추출된 프로젝트 목록: ${projects.map { "${it.projectName}(${it.projectNo})" }}")
            
            // 선택된 프로젝트 결정
            val targetProject = selectedProject ?: projects.firstOrNull()
            Log.d("DashboardAPI", "대상 프로젝트: ${targetProject?.projectName} (${targetProject?.projectNo})")
            
            // 선택된 프로젝트의 이슈만 필터링
            val filteredIssues = if (targetProject != null) {
                combinedResponse.list.filter { it.projectNo == targetProject.projectNo }
            } else {
                combinedResponse.list
            }
            
            Log.d("DashboardAPI", "필터링된 이슈: ${filteredIssues.size}개 (${targetProject?.projectName})")
            
            // 필터링된 데이터로 DashboardResponse 생성
            val filteredResponse = DashboardResponse(
                listCnt = filteredIssues.size.toString(),
                totalCnt = filteredIssues.size.toString(),
                list = filteredIssues
            )
            
            val dashboardData = mapToDashboardData(filteredResponse, projects, targetProject)
            
            // 최종 결과 로그
            Log.d("DashboardAPI", "====== 최종 결과 ======")
            Log.d("DashboardAPI", "변환된 데이터 - 잔여:${dashboardData.todayTasks.inProgress}, 지연:${dashboardData.todayTasks.delayed}, 등록:${dashboardData.todayTasks.registered}")
            Log.d("DashboardAPI", "최종 선택된 프로젝트: ${dashboardData.selectedProject?.projectName}")
            Log.d("DashboardAPI", "상태별 차트: ${dashboardData.statusChart.map { "${it.label}(${it.count})" }}")
            Log.d("DashboardAPI", "타입별 차트: ${dashboardData.typeChart.map { "${it.label}(${it.count})" }}")
            Log.d("DashboardAPI", "==================")
            emit(dashboardData)
        } catch (e: Exception) {
            Log.e("DashboardAPI", "API 실패 - Mock 데이터 사용: ${e.message}")
            val mockData = getMockDashboardData(selectedProject)
            Log.d("DashboardAPI", "Mock 데이터 프로젝트 목록: ${mockData.projects.map { "${it.projectName}(${it.projectNo})" }}")
            emit(mockData)
        }
    }

    private fun mapToDashboardData(
        response: DashboardResponse, 
        projects: List<com.example.domain.model.home.Project>,
        selectedProject: com.example.domain.model.home.Project?
    ): DashboardData {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        Log.d("DashboardAPI", "프로젝트 목록: ${projects.map { "${it.projectName}(${it.projectNo})" }}")

        // 전체 이슈 데이터 사용 (이미 프로젝트별로 필터링됨)
        val filteredList = response.list
        Log.d("DashboardAPI", "이슈 데이터 사용: ${filteredList.size}개")

        // 오늘의 할일 계산 (API 문서 기준)
        val todayTasks = TodayTaskStats(
            inProgress = filteredList.count { !isCompleteStatus(it.issueStateName) }, // 잔여 = 완료상태가 아닌 것들
            delayed = filteredList.count { it.isn == "지연" },                      // 지연 = isn == "지연"
            registered = filteredList.count { it.crtrDtYYYYMMDD == currentDate }    // 등록 = 오늘 등록된 것들
        )

        Log.d("DashboardAPI", "오늘의 할일 계산 완료 - 잔여:${todayTasks.inProgress}, 지연:${todayTasks.delayed}, 등록:${todayTasks.registered}")

        // 상태별 차트 데이터 (issueStateName으로 그룹핑)
        val statusGrouped = filteredList
            .filter { !it.issueStateName.isNullOrBlank() }
            .groupBy { it.issueStateName }
        val statusChart = statusGrouped.map { entry ->
            val status = entry.key
            val issues = entry.value
            StatusChartData(
                label = status,
                count = issues.count(),
                color = issues.firstOrNull()?.issueStateColor ?: "#666666"
            )
        }

        // 타입별 차트 데이터 (issueTypeName으로 그룹핑)
        val typeGrouped = filteredList
            .filter { !it.issueTypeName.isNullOrBlank() }
            .groupBy { it.issueTypeName }
        val typeChart = mutableListOf<TypeChartData>()
        var index = 0
        for (entry in typeGrouped) {
            val type = entry.key
            val issues = entry.value
            typeChart.add(
                TypeChartData(
                    label = type,
                    count = issues.count(),
                    color = getTypeColor(index)
                )
            )
            index++
        }
        
        Log.d("DashboardAPI", "차트 데이터 - 상태별:${statusChart.size}개, 타입별:${typeChart.size}개")
        
        return DashboardData(
            todayTasks = todayTasks,
            statusChart = statusChart,
            typeChart = typeChart,
            currentDate = currentDate,
            projects = projects,
            selectedProject = selectedProject
        )
    }

    private fun isCompleteStatus(status: String): Boolean {
        // API 문서에서 완료 상태로 간주되는 상태들
        return status in listOf("완료", "해결", "종료", "닫힘", "Done", "DONE", "done", "Complete", "COMPLETE", "complete")
    }

    private fun getTypeColor(index: Int): String {
        val colors = listOf("#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD")
        return colors[index % colors.count()]
    }

    private fun getMockDashboardData(selectedProject: com.example.domain.model.home.Project? = null): DashboardData {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val mockProjects = listOf(
            com.example.domain.model.home.Project("demo2", "데모 프로젝트2"),
            com.example.domain.model.home.Project("test12454", "테스트 프로젝트"),
            com.example.domain.model.home.Project("demo1", "데모 프로젝트1"),
            com.example.domain.model.home.Project("demo3", "데모 프로젝트3")
        )
        
        // 선택된 프로젝트가 있으면 해당 프로젝트를 사용, 없으면 기본값 사용
        val finalSelectedProject = selectedProject ?: mockProjects[0] // 기본값: 데모 프로젝트2
        
        Log.d("DashboardAPI", "Mock 데이터 - 선택된 프로젝트: ${finalSelectedProject.projectName}")

        return DashboardData(
            todayTasks = TodayTaskStats(
                inProgress = 3,
                delayed = 1,
                registered = 2
            ),
            statusChart = listOf(
                StatusChartData("등록", 2, "#FF6B6B"),
                StatusChartData("진행", 3, "#4ECDC4"),
                StatusChartData("완료", 1, "#45B7D1")
            ),
            typeChart = listOf(
                TypeChartData("버그", 3, "#FF6B6B"),
                TypeChartData("기능", 2, "#4ECDC4"),
                TypeChartData("개선", 1, "#45B7D1")
            ),
            currentDate = currentDate,
            projects = mockProjects,
            selectedProject = finalSelectedProject
        )
    }
}
