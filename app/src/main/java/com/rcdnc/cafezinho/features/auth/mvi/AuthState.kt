package com.rcdnc.cafezinho.features.auth.mvi

import com.rcdnc.cafezinho.features.auth.domain.model.User

/**
 * All possible states in the authentication flow
 * Following MVI pattern for reactive UI updates
 */
sealed class AuthState {
    
    // Initial States
    object Idle : AuthState()
    object CheckingAuthState : AuthState()
    
    // Social Login States
    object GoogleSignInLoading : AuthState()
    object FacebookSignInLoading : AuthState()
    data class SocialSignInSuccess(val user: User, val provider: String) : AuthState()
    data class SocialSignInError(val error: AuthError, val provider: String) : AuthState()
    
    // Phone Authentication States
    object PhoneVerificationLoading : AuthState()
    data class PhoneVerificationSent(val phoneNumber: String, val verificationId: String) : AuthState()
    object OtpVerificationLoading : AuthState()
    data class OtpVerificationSuccess(val user: User) : AuthState()
    data class OtpVerificationError(val error: AuthError, val phoneNumber: String) : AuthState()
    object ResendingOtp : AuthState()
    data class OtpResent(val phoneNumber: String) : AuthState()
    
    // Registration States
    object RegistrationLoading : AuthState()
    data class RegistrationSuccess(val user: User) : AuthState()
    data class RegistrationError(val error: AuthError) : AuthState()
    
    // Profile Completion States
    data class ProfileCompletion(
        val currentStep: ProfileStep,
        val registrationData: RegistrationData,
        val isLoading: Boolean = false,
        val error: AuthError? = null
    ) : AuthState()
    
    object ProfileCompletionLoading : AuthState()
    data class ProfileCompleted(val user: User) : AuthState()
    data class ProfileCompletionError(val error: AuthError, val step: ProfileStep) : AuthState()
    
    // Session States
    data class Authenticated(val user: User, val isProfileComplete: Boolean) : AuthState()
    object SessionExpired : AuthState()
    object SessionRefreshing : AuthState()
    object LoggedOut : AuthState()
    
    // Validation States
    data class PhoneValidationResult(val isValid: Boolean, val formattedPhone: String? = null) : AuthState()
    data class OtpValidationResult(val isValid: Boolean) : AuthState()
    
    // General States
    data class Loading(val message: String? = null) : AuthState()
    data class Error(val error: AuthError) : AuthState()
    data class Success(val user: User, val message: String? = null) : AuthState()
}

/**
 * Profile completion steps enum
 */
enum class ProfileStep(val stepNumber: Int, val totalSteps: Int = 8) {
    FIRST_NAME(1),
    DATE_OF_BIRTH(2),
    GENDER(3),
    GENDER_PREFERENCE(4),
    INTERESTS(5),
    PHOTOS(6),
    ADDITIONAL_INFO(7),
    COMPLETION(8);
    
    fun next(): ProfileStep? = values().getOrNull(ordinal + 1)
    fun previous(): ProfileStep? = values().getOrNull(ordinal - 1)
    fun progress(): Float = stepNumber.toFloat() / totalSteps
}

/**
 * Comprehensive error types for authentication
 */
sealed class AuthError(val message: String, val code: String? = null) {
    
    // Network Errors
    object NetworkError : AuthError("Erro de conexão. Verifique sua internet.")
    object ServerError : AuthError("Erro no servidor. Tente novamente.")
    object TimeoutError : AuthError("Operação expirou. Tente novamente.")
    
    // Social Login Errors
    object GoogleSignInCancelled : AuthError("Login com Google cancelado.")
    object GoogleSignInFailed : AuthError("Falha no login com Google.")
    object FacebookSignInCancelled : AuthError("Login com Facebook cancelado.")
    object FacebookSignInFailed : AuthError("Falha no login com Facebook.")
    object SocialAccountNotFound : AuthError("Conta não encontrada.")
    
    // Phone Authentication Errors
    object InvalidPhoneNumber : AuthError("Número de telefone inválido.")
    object PhoneVerificationFailed : AuthError("Falha na verificação do telefone.")
    object InvalidOtp : AuthError("Código de verificação inválido.")
    object OtpExpired : AuthError("Código de verificação expirado.")
    object TooManyRequests : AuthError("Muitas tentativas. Tente novamente mais tarde.")
    
    // Registration Errors
    object UserAlreadyExists : AuthError("Usuário já cadastrado.")
    object RegistrationFailed : AuthError("Falha no cadastro. Tente novamente.")
    object ProfileCompletionRequired : AuthError("Complete seu perfil para continuar.")
    
    // Validation Errors
    data class ValidationError(val field: String, val reason: String) : 
        AuthError("Erro de validação: $reason")
    
    // Session Errors
    object SessionExpired : AuthError("Sessão expirada. Faça login novamente.")
    object UnauthorizedAccess : AuthError("Acesso não autorizado.")
    object TokenRefreshFailed : AuthError("Falha ao renovar sessão.")
    
    // General Errors
    data class UnknownError(val throwable: Throwable? = null) : 
        AuthError("Erro inesperado. Tente novamente.")
    data class CustomError(val customMessage: String) : 
        AuthError(customMessage)
}