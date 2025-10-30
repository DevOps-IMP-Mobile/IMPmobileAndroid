package com.example.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.FcmRepository
import com.example.domain.context.UserContext  // ✅ 수정
import com.example.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val fcmRepository: FcmRepository,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateUserId(userId: String) {
        _uiState.value = _uiState.value.copy(userId = userId, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            loginUseCase(
                userId = _uiState.value.userId,
                password = _uiState.value.password
            ).fold(
                onSuccess = { token ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )

                    // ✅ 로그인 성공 시 FCM 토큰 저장
                    savePendingFcmToken()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "로그인에 실패했습니다."
                    )
                }
            )
        }
    }

    /**
     * SharedPreferences에 임시 저장된 FCM 토큰을 DB로 이동
     */
    private fun savePendingFcmToken() {
        viewModelScope.launch {
            try {
                // SharedPreferences에서 임시 저장된 토큰 가져오기
                val prefs = application.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
                val pendingToken = prefs.getString("pending_fcm_token", null)

                if (pendingToken.isNullOrEmpty()) {
                    Log.d(TAG, "임시 저장된 FCM 토큰이 없습니다")
                    return@launch
                }

                // UserContext에서 로그인 정보 가져오기
                val ctx = UserContext.instance
                val userId = ctx.userId
                val spUid = ctx.spUid

                if (userId.isNullOrEmpty()) {
                    Log.w(TAG, "⚠️ 사용자 정보가 없어서 FCM 토큰을 저장할 수 없습니다")
                    return@launch
                }

                // Room DB에 FCM 토큰 저장
                val result = fcmRepository.saveFcmToken(
                    userId = userId,
                    fcmToken = pendingToken,
                    spUid = spUid
                )

                if (result.isSuccess) {
                    // 저장 성공 시 SharedPreferences에서 삭제
                    prefs.edit().remove("pending_fcm_token").apply()
                    Log.d(TAG, "✅ 로그인 후 FCM 토큰 DB 저장 완료")
                    Log.d(TAG, "저장된 토큰: ${pendingToken.substring(0, 20)}...")
                } else {
                    Log.e(TAG, "❌ FCM 토큰 DB 저장 실패", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ FCM 토큰 저장 중 예외 발생", e)
            }
        }
    }
}