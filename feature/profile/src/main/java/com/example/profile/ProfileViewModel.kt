package com.example.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.api.MeApiService
import com.example.network.dto.MeResponse
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.compose.runtime.*

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val meApiService: MeApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
    val uiState: State<ProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val response = meApiService.getMe()
                val userProfile = UserProfile.fromMeResponse(response)
                _uiState.value = ProfileUiState.Success(userProfile)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "사용자 정보를 불러오는데 실패했습니다.")
            }
        }
    }

    //로그아웃
    fun logout() {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "로그아웃 시작")
                // AuthRepository의 logout() 함수 호출
                authRepository.logout()
                Log.d("ProfileViewModel", "로그아웃 완료")
                // UI 상태를 로딩으로 변경 (또는 초기 상태)
                _uiState.value = ProfileUiState.Loading

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("로그아웃 중 오류가 발생했습니다.")
            }
        }
    }
}