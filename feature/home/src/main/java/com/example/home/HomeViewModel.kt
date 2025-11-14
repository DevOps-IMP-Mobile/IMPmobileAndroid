package com.example.home

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.home.GetDashboardDataUseCase
import com.example.domain.usecase.issue.GetIssueListUseCase
import com.example.domain.usecase.unified.GetUnifiedItemsUseCase
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.unified.ItemSource
import com.example.data.local.CompletedIssuesManager  // ✅ 추가
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.net.HttpURLConnection
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
 import android.net.ConnectivityManager
 import java.net.URL
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getIssueListUseCase: GetIssueListUseCase,
    private val getUnifiedItemsUseCase: GetUnifiedItemsUseCase,
    private val completedIssuesManager: CompletedIssuesManager  // ✅ 추가
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadDashboard)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDashboard -> loadDashboard()
            is HomeIntent.RefreshDashboard -> refreshDashboard()
            is HomeIntent.SelectProject -> selectProject(intent.project)
            is HomeIntent.NavigateToTaskDetail -> {
                sendEffect(HomeEffect.NavigateToTaskList)
            }
            is HomeIntent.NavigateToStatusDetail -> {
                sendEffect(HomeEffect.NavigateToStatusChart)
            }
            is HomeIntent.NavigateToTypeDetail -> {
                sendEffect(HomeEffect.NavigateToTypeChart)
            }
            is HomeIntent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
            }
            is HomeIntent.CloseDrawer -> {
                _state.update { it.copy(isDrawerOpen = false) }
            }
            is HomeIntent.OpenRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = true) }
                handleIntent(HomeIntent.LoadRecentIssues)
            }
            is HomeIntent.CloseRecentIssuesSheet -> {
                _state.update { it.copy(isRecentIssuesSheetOpen = false) }
            }
            is HomeIntent.LoadRecentIssues -> loadRecentIssues()
            is HomeIntent.OpenUnifiedSheet -> {
                _state.update { it.copy(isUnifiedSheetOpen = true) }
                handleIntent(HomeIntent.LoadUnifiedItems)
            }
            is HomeIntent.CloseUnifiedSheet -> {
                _state.update { it.copy(isUnifiedSheetOpen = false) }
            }
            is HomeIntent.LoadUnifiedItems -> loadUnifiedItems()
            is HomeIntent.ToggleSource -> toggleSource(intent.source)

            // 상태 변경 관련
            is HomeIntent.SelectIssueForStatusChange -> {
                _state.update {
                    it.copy(
                        selectedIssue = intent.issue,
                        showStatusChangeDialog = true
                    )
                }
            }
            is HomeIntent.CancelStatusChange -> {
                _state.update {
                    it.copy(
                        selectedIssue = null,
                        showStatusChangeDialog = false
                    )
                }
            }
            is HomeIntent.UpdateIssueStatus -> {
                updateIssueStatus(intent.issue, intent.newStatusCode)
            }
            is HomeIntent.ConfirmCompleteIssue -> {
                _state.update {
                    it.copy(
                        selectedIssue = intent.issue,
                        showStatusChangeDialog = false,
                        showCompleteConfirmDialog = true
                    )
                }
            }
            is HomeIntent.CancelCompleteIssue -> {
                _state.update {
                    it.copy(
                        selectedIssue = null,
                        showCompleteConfirmDialog = false
                    )
                }
            }
            is HomeIntent.CompleteIssue -> {
                completeIssue(intent.issue)
            }
        }
    }

    private fun selectProject(project: com.example.domain.model.home.Project) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    dashboardData = it.dashboardData.copy(selectedProject = project),
                    isDrawerOpen = false
                )
            }
            loadDashboardWithSelectedProject(project)
        }
    }

    private fun loadDashboard() {
        loadDashboardWithSelectedProject(_state.value.dashboardData.selectedProject)
    }

    private fun loadDashboardWithSelectedProject(selectedProject: com.example.domain.model.home.Project?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getDashboardDataUseCase(selectedProject)
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                    sendEffect(HomeEffect.ShowError(throwable.message ?: "오류가 발생했습니다."))
                }
                .collect { dashboardData ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dashboardData = dashboardData.copy(selectedProject = selectedProject),
                            error = null
                        )
                    }
                }
        }
    }

    private fun loadRecentIssues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRecentIssues = true) }

            val selectedProject = _state.value.dashboardData.selectedProject
            if (selectedProject == null) {
                _state.update {
                    it.copy(
                        isLoadingRecentIssues = false,
                        recentIssues = emptyList()
                    )
                }
                return@launch
            }

            try {
                val filter = IssueFilter(projectId = selectedProject.projectNo)
                getIssueListUseCase(filter, IssueSortType.CREATED_DATE)
                    .catch { throwable ->
                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = emptyList()
                            )
                        }
                    }
                    .collect { issues ->
                        val twoWeeksAgo = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -14)
                        }.time

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val filteredIssues = issues.filter { issue ->
                            try {
                                val issueDate = dateFormat.parse(issue.createdDate.take(10))
                                val isRecent = issueDate != null && issueDate.after(twoWeeksAgo)
                                val isNotCompleted = !completedIssuesManager.isCompleted(issue.id)  // ✅ 완료한 이슈 필터링
                                isRecent && isNotCompleted
                            } catch (e: Exception) {
                                false
                            }
                        }

                        _state.update {
                            it.copy(
                                isLoadingRecentIssues = false,
                                recentIssues = filteredIssues
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingRecentIssues = false,
                        recentIssues = emptyList()
                    )
                }
            }
        }
    }

    private fun loadUnifiedItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingUnifiedItems = true) }

            try {
                val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val startDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -14)
                }.time.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }

                getUnifiedItemsUseCase(
                    startDate = startDate,
                    endDate = endDate,
                    sources = _state.value.selectedSources
                )
                    .catch { throwable ->
                        _state.update {
                            it.copy(
                                isLoadingUnifiedItems = false,
                                unifiedItems = emptyList()
                            )
                        }
                    }
                    .collect { items ->
                        _state.update {
                            it.copy(
                                isLoadingUnifiedItems = false,
                                unifiedItems = items
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingUnifiedItems = false,
                        unifiedItems = emptyList()
                    )
                }
            }
        }
    }

    private fun toggleSource(source: ItemSource) {
        val currentSources = _state.value.selectedSources.toMutableList()
        if (source in currentSources) {
            currentSources.remove(source)
        } else {
            currentSources.add(source)
        }
        _state.update { it.copy(selectedSources = currentSources) }
        loadUnifiedItems()
    }

    // ✅ 상태 변경 (UI만)
    private fun updateIssueStatus(issue: com.example.domain.model.issue.Issue, newStatusCode: String) {
        viewModelScope.launch {
            val newStatus = mapStatusCode(newStatusCode)

            // 업데이트된 이슈 객체 생성
            val updatedIssue = issue.copy(status = newStatus)

            // UI에서만 상태 변경
            val updatedIssues = _state.value.recentIssues.map { currentIssue ->
                if (currentIssue.id == issue.id) {
                    updatedIssue
                } else {
                    currentIssue
                }
            }

            // recentIssues 업데이트 및 다이얼로그 닫기
            _state.update {
                it.copy(
                    recentIssues = updatedIssues,
                    showStatusChangeDialog = false,  // ✅ 다이얼로그 닫기
                    selectedIssue = null  // ✅ 선택 해제
                )
            }

            sendEffect(HomeEffect.ShowStatusUpdateSuccess("상태가 변경되었습니다"))
        }
    }

    // ✅ 완료 처리 (로컬 저장 + UI 제거)
    private fun completeIssue(issue: com.example.domain.model.issue.Issue) {
        viewModelScope.launch {
            // 다이얼로그 닫기
            _state.update {
                it.copy(
                    showCompleteConfirmDialog = false,
                    selectedIssue = null
                )
            }

            // 완료한 이슈 ID를 로컬에 저장
            completedIssuesManager.addCompletedIssue(issue.id)

            // UI에서 제거
            val updatedIssues = _state.value.recentIssues.filter { it.id != issue.id }
            _state.update {
                it.copy(recentIssues = updatedIssues)
            }

            sendEffect(HomeEffect.ShowStatusUpdateSuccess("이슈가 완료되었습니다"))
        }
    }

    private fun mapStatusCode(code: String): com.example.domain.model.issue.IssueStatus {
        return when (code) {
            "REGISTERED" -> com.example.domain.model.issue.IssueStatus.REGISTERED
            "IN_PROGRESS" -> com.example.domain.model.issue.IssueStatus.IN_PROGRESS
            "RESOLVED" -> com.example.domain.model.issue.IssueStatus.RESOLVED
            "CLOSED" -> com.example.domain.model.issue.IssueStatus.CLOSED
            else -> com.example.domain.model.issue.IssueStatus.REGISTERED
        }
    }

    private fun refreshDashboard() {
        loadDashboard()
        sendEffect(HomeEffect.ShowRefreshComplete)
    }

    private fun sendEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
    fun testAWSConnectionSimple() {
        viewModelScope.launch {
            Log.d("AWS_TEST", "========================================")
            Log.d("AWS_TEST", "🚀 AWS 연결 테스트 시작")
            Log.d("AWS_TEST", "========================================")

            try {
                withContext(Dispatchers.IO) {
                    Log.d("AWS_TEST", "1️⃣ IO 스레드 진입 성공")

                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build()

                    Log.d("AWS_TEST", "2️⃣ OkHttpClient 생성 완료")

                    val url = "http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com/"
                    Log.d("AWS_TEST", "3️⃣ 접속 URL: $url")

                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("User-Agent", "Android-App")
                        .build()

                    Log.d("AWS_TEST", "4️⃣ Request 생성 완료")
                    Log.d("AWS_TEST", "5️⃣ 서버에 요청 전송 중...")

                    val response = client.newCall(request).execute()

                    Log.d("AWS_TEST", "========================================")
                    Log.d("AWS_TEST", "✅ 서버 응답 성공!")
                    Log.d("AWS_TEST", "========================================")
                    Log.d("AWS_TEST", "📊 응답 코드: ${response.code}")
                    Log.d("AWS_TEST", "📊 응답 메시지: ${response.message}")
                    Log.d("AWS_TEST", "📊 프로토콜: ${response.protocol}")

                    val responseBody = response.body?.string()
                    Log.d("AWS_TEST", "📄 응답 본문 길이: ${responseBody?.length ?: 0}")
                    if (responseBody != null && responseBody.length < 500) {
                        Log.d("AWS_TEST", "📄 응답 본문: $responseBody")
                    } else if (responseBody != null) {
                        Log.d("AWS_TEST", "📄 응답 본문 미리보기: ${responseBody.take(200)}...")
                    }

                    Log.d("AWS_TEST", "========================================")
                    Log.d("AWS_TEST", "🎉 연결 테스트 완료!")
                    Log.d("AWS_TEST", "========================================")

                    response.close()
                }
            } catch (e: java.net.UnknownHostException) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ DNS 오류: 서버 주소를 찾을 수 없습니다")
                Log.e("AWS_TEST", "💡 해결방법: 서버 URL이 올바른지 확인하세요")
                Log.e("AWS_TEST", "에러: ${e.message}")
                Log.e("AWS_TEST", "========================================")
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ 타임아웃: 서버가 응답하지 않습니다")
                Log.e("AWS_TEST", "💡 해결방법: 서버가 실행 중인지 확인하세요")
                Log.e("AWS_TEST", "에러: ${e.message}")
                Log.e("AWS_TEST", "========================================")
            } catch (e: java.net.ConnectException) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ 연결 실패: 서버에 연결할 수 없습니다")
                Log.e("AWS_TEST", "💡 해결방법:")
                Log.e("AWS_TEST", "  1. 서버가 실행 중인지 확인")
                Log.e("AWS_TEST", "  2. 방화벽 설정 확인")
                Log.e("AWS_TEST", "  3. 서버 포트 확인")
                Log.e("AWS_TEST", "에러: ${e.message}")
                Log.e("AWS_TEST", "========================================")
            } catch (e: javax.net.ssl.SSLException) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ SSL 오류: HTTPS 인증서 문제")
                Log.e("AWS_TEST", "에러: ${e.message}")
                Log.e("AWS_TEST", "========================================")
            } catch (e: java.io.IOException) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ IO 오류: 네트워크 문제")
                Log.e("AWS_TEST", "💡 해결방법:")
                Log.e("AWS_TEST", "  1. 인터넷 연결 확인")
                Log.e("AWS_TEST", "  2. AndroidManifest.xml에 INTERNET 권한 확인")
                Log.e("AWS_TEST", "  3. Cleartext traffic 설정 확인")
                Log.e("AWS_TEST", "에러: ${e.message}")
                Log.e("AWS_TEST", "========================================")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("AWS_TEST", "========================================")
                Log.e("AWS_TEST", "❌ 알 수 없는 에러 발생!")
                Log.e("AWS_TEST", "에러 타입: ${e.javaClass.simpleName}")
                Log.e("AWS_TEST", "에러 메시지: ${e.message}")
                Log.e("AWS_TEST", "========================================")
                e.printStackTrace()
            }
        }
    }
    // ✅ HomeViewModel.kt에 추가할 수 있는 고급 테스트 함수

    // 1️⃣ 네트워크 상태 확인
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }

    // 2️⃣ Ping 테스트 (간단한 연결 확인)
    fun testAWSPing() {
        viewModelScope.launch {
            Log.d("AWS_PING", "🏓 Ping 테스트 시작")

            try {
                withContext(Dispatchers.IO) {
                    val url = URL("http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com/")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.requestMethod = "HEAD"

                    val responseCode = connection.responseCode
                    Log.d("AWS_PING", "✅ Ping 성공: $responseCode")
                    connection.disconnect()
                }
            } catch (e: Exception) {
                Log.e("AWS_PING", "❌ Ping 실패: ${e.message}")
            }
        }
    }

    // 3️⃣ 다양한 엔드포인트 테스트
    fun testMultipleEndpoints() {
        viewModelScope.launch {
            val endpoints = listOf(
                "/",
                "/health",
                "/api/status",
                "/actuator/health"
            )

            endpoints.forEach { endpoint ->
                testEndpoint(endpoint)
            }
        }
    }

    private suspend fun testEndpoint(endpoint: String) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val url = "http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com$endpoint"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                Log.d("AWS_TEST", "📍 $endpoint -> ${response.code} ${response.message}")
                response.close()
            } catch (e: Exception) {
                Log.e("AWS_TEST", "📍 $endpoint -> ❌ ${e.message}")
            }
        }
    }

    // 4️⃣ 상세 로깅을 위한 OkHttp Interceptor
    private fun createLoggingClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("AWS_HTTP", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 5️⃣ POST 요청 테스트
    fun testAWSPost() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val json = """{"test": "data"}"""
                    val body = json.toRequestBody("application/json".toMediaType())

                    val request = Request.Builder()
                        .url("http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com/api/test")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()
                    Log.d("AWS_POST", "응답: ${response.code} ${response.body?.string()}")
                    response.close()
                }
            } catch (e: Exception) {
                Log.e("AWS_POST", "POST 오류: ${e.message}")
            }
        }
    }


}