package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.home.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
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
            is HomeIntent.NavigateToTaskDetail -> {
                sendEffect(HomeEffect.NavigateToTaskList)
            }
            is HomeIntent.NavigateToStatusDetail -> {
                sendEffect(HomeEffect.NavigateToStatusChart)
            }
            is HomeIntent.NavigateToTypeDetail -> {
                sendEffect(HomeEffect.NavigateToTypeChart)
            }
        }
    }
    
    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getDashboardDataUseCase()
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
                            dashboardData = dashboardData,
                            error = null
                        )
                    }
                }
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
}