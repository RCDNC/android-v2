package com.rcdnc.cafezinho.features.auth.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.auth.domain.usecase.LoginUseCase
import com.rcdnc.cafezinho.features.auth.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.phone, intent.otp)
            is AuthIntent.Signup -> signup(intent.phone)
            is AuthIntent.VerifyOtp -> verifyOtp(intent.phone, intent.otp)
            is AuthIntent.Logout -> logout()
        }
    }

    private fun login(phone: String, otp: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            loginUseCase.loginWithPhone(phone, otp)
                .onSuccess { user ->
                    _state.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    private fun signup(phone: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signupUseCase.signup(phone)
                .onSuccess { otpId ->
                    _state.value = AuthState.OtpSent(phone)
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "Signup failed")
                }
        }
    }

    private fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signupUseCase.verifyOtp(phone, otp)
                .onSuccess { user ->
                    _state.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "OTP verification failed")
                }
        }
    }

    private fun logout() {
        _state.value = AuthState.LoggedOut
    }
}