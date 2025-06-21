package com.rcdnc.cafezinho.features.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.auth.data.repository.RealAuthRepositoryImpl
import com.rcdnc.cafezinho.features.auth.mvi.AuthIntent
import com.rcdnc.cafezinho.features.auth.mvi.AuthState
import com.rcdnc.cafezinho.features.auth.mvi.AuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Simplified AuthViewModel for real authentication
 * Works without Hilt dependency injection
 */
class SimpleAuthViewModel(context: Context) : ViewModel() {
    
    private val authRepository = RealAuthRepositoryImpl(context)
    
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    /**
     * Main entry point for handling authentication intents
     */
    fun handleIntent(intent: AuthIntent) {
        viewModelScope.launch {
            when (intent) {
                is AuthIntent.LoginWithGoogle -> handleGoogleSignIn()
                is AuthIntent.LoginWithFacebook -> handleFacebookSignIn()
                is AuthIntent.SendPhoneVerification -> handlePhoneVerification(intent.phoneNumber)
                is AuthIntent.Logout -> handleLogout()
                else -> {
                    // Other intents not implemented yet
                }
            }
        }
    }
    
    private suspend fun handleGoogleSignIn() {
        _state.value = AuthState.GoogleSignInLoading
        
        val result = authRepository.signInWithGoogle()
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.Authenticated(user, user.isProfileComplete)
            },
            onFailure = { error ->
                _state.value = AuthState.Error(AuthError.GoogleSignInFailed)
            }
        )
    }
    
    private suspend fun handleFacebookSignIn() {
        _state.value = AuthState.FacebookSignInLoading
        
        val result = authRepository.signInWithFacebook()
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.Authenticated(user, user.isProfileComplete)
            },
            onFailure = { error ->
                _state.value = AuthState.Error(AuthError.FacebookSignInFailed)
            }
        )
    }
    
    private suspend fun handlePhoneVerification(phoneNumber: String) {
        _state.value = AuthState.PhoneVerificationLoading
        
        val result = authRepository.sendPhoneVerification(phoneNumber)
        
        result.fold(
            onSuccess = { verificationId ->
                _state.value = AuthState.PhoneVerificationSent(phoneNumber, verificationId)
            },
            onFailure = { error ->
                _state.value = AuthState.Error(AuthError.PhoneVerificationFailed)
            }
        )
    }
    
    private suspend fun handleLogout() {
        _state.value = AuthState.Loading("Fazendo logout...")
        
        val result = authRepository.signOut()
        
        result.fold(
            onSuccess = {
                _state.value = AuthState.LoggedOut
            },
            onFailure = { error ->
                _state.value = AuthState.Error(AuthError.UnknownError())
            }
        )
    }
}