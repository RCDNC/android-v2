package com.rcdnc.cafezinho.features.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gerenciar permissão de notificações
 */
@HiltViewModel
class NotificationPermissionViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationPermissionUiState())
    val uiState: StateFlow<NotificationPermissionUiState> = _uiState.asStateFlow()
    
    /**
     * Verifica status atual da permissão
     */
    fun checkPermissionStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val isEnabled = notificationRepository.areNotificationsEnabled()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPermissionGranted = isEnabled
            )
        }
    }
    
    /**
     * Chamado quando permissão é concedida
     */
    fun onPermissionGranted() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isPermissionGranted = true
            )
            
            // Registra token FCM
            notificationRepository.registerFCMToken()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isTokenRegistered = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    
    /**
     * Chamado quando permissão é negada
     */
    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            isPermissionGranted = false,
            isLoading = false
        )
    }
    
    /**
     * Limpa erro atual
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado da UI para permissão de notificações
 */
data class NotificationPermissionUiState(
    val isLoading: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val isTokenRegistered: Boolean = false,
    val error: String? = null
)