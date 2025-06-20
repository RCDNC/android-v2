package com.rcdnc.cafezinho.features.auth.mvi

import com.rcdnc.cafezinho.features.auth.domain.model.User

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class OtpSent(val phone: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}