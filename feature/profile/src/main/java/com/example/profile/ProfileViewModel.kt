package com.example.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.api.MeApiService
import com.example.network.dto.MeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.compose.runtime.*

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val meApiService: MeApiService
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

    fun logout() {
        //tokenManager.clearToken() 토큰 삭제?
    }
}
