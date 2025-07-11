package com.rcdnc.cafezinho.features.auth.domain.repository

import android.app.Activity
import com.rcdnc.cafezinho.features.auth.domain.model.*
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface para autenticação
 * Abstração da camada de dados para auth
 */
interface AuthRepository {
    
    /**
     * Observa o estado de autenticação atual
     */
    fun observeAuthStatus(): Flow<AuthStatus>
    
    /**
     * Observa o usuário atual autenticado
     */
    fun observeCurrentUser(): Flow<User?>
    
    /**
     * Obtém o usuário atual (sincrono)
     */
    fun getCurrentUser(): User?
    
    /**
     * Obtém o token de acesso atual
     */
    fun getCurrentToken(): String?
    
    /**
     * Verifica se o usuário está autenticado
     */
    fun isAuthenticated(): Boolean
    
    /**
     * Login com email e senha
     */
    suspend fun login(credentials: LoginCredentials): Result<AuthResult>
    
    /**
     * Registro de novo usuário
     */
    suspend fun register(registerData: RegisterData): Result<AuthResult>
    
    /**
     * Login social (Google, Facebook, Apple)
     */
    suspend fun socialLogin(socialData: SocialLoginData): Result<AuthResult>
    
    /**
     * Logout do usuário atual
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Refresh do token de acesso
     */
    suspend fun refreshToken(): Result<AuthResult>
    
    /**
     * Solicitação de redefinição de senha
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>
    
    /**
     * Confirmação de redefinição de senha
     */
    suspend fun confirmPasswordReset(resetData: PasswordResetConfirmData): Result<Unit>
    
    /**
     * Verificação de email
     */
    suspend fun verifyEmail(token: String): Result<Unit>
    
    /**
     * Reenvio de email de verificação
     */
    suspend fun resendVerificationEmail(): Result<Unit>
    
    /**
     * Atualização de senha
     */
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>
    
    /**
     * Atualização de dados básicos do usuário
     */
    suspend fun updateUserData(
        firstName: String,
        lastName: String?,
        phoneNumber: String?
    ): Result<User>
    
    /**
     * Exclusão de conta
     */
    suspend fun deleteAccount(password: String): Result<Unit>
    
    /**
     * Validação de token (verifica se ainda é válido)
     */
    suspend fun validateToken(): Result<Boolean>
    
    /**
     * Clear de todos os dados de autenticação (logout local)
     */
    suspend fun clearAuthData(): Result<Unit>
    
    // Social Login Methods
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithFacebook(): Result<User>
    
    // Phone Authentication Methods
    suspend fun sendPhoneVerification(phoneNumber: String, activity: Activity? = null): Result<String>
    suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<User>
    suspend fun resendPhoneVerification(phoneNumber: String): Result<String>
    
    // Alternative Login Methods
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithPhone(phone: String, otp: String): Result<User>
    suspend fun signOut(): Result<Unit>
    
    // Registration Methods
    suspend fun completeRegistration(registrationData: RegistrationData): Result<User>
    suspend fun getProfileCompletionStatus(userId: String): Result<Boolean>
    
    // Session Management
    suspend fun refreshAuthToken(): Result<Unit>
    
    // Validation Methods
    suspend fun validatePhoneNumber(phoneNumber: String): Result<String>
}