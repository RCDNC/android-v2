package com.rcdnc.cafezinho.features.auth.domain.model

/**
 * Modelo de domínio para usuário autenticado
 * Baseado na estrutura da API Laravel do Cafezinho
 */
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val profileCompletion: Int = 0,
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String = System.currentTimeMillis().toString(),
    val lastLoginAt: Long? = null
)

/**
 * Credenciais de login
 */
data class LoginCredentials(
    val email: String,
    val password: String,
    val rememberMe: Boolean = true
)

/**
 * Dados para registro de usuário
 */
data class RegisterData(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val gender: String,
    val acceptTerms: Boolean
)

/**
 * Resultado da autenticação
 */
data class AuthResult(
    val user: User,
    val token: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 0
) {
    // Compatibility property
    val accessToken: String get() = token
}

/**
 * Estados de autenticação
 */
enum class AuthStatus {
    UNAUTHENTICATED,
    AUTHENTICATING,
    AUTHENTICATED,
    TOKEN_EXPIRED,
    AUTH_ERROR
}

/**
 * Erro de autenticação
 */
sealed class AuthError : Exception() {
    object NetworkError : AuthError()
    object InvalidCredentials : AuthError()
    object UserNotFound : AuthError()
    object EmailAlreadyExists : AuthError()
    object WeakPassword : AuthError()
    object TokenExpired : AuthError()
    object AccountNotVerified : AuthError()
    object TooManyAttempts : AuthError()
    data class ValidationError(val field: String, val message: String) : AuthError()
    data class UnknownError(override val message: String) : AuthError()
}

/**
 * Dados para redefinição de senha
 */
data class PasswordResetData(
    val email: String
)

/**
 * Dados para confirmação de reset
 */
data class PasswordResetConfirmData(
    val email: String,
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)

/**
 * Provider de login social
 */
enum class SocialProvider {
    GOOGLE,
    FACEBOOK,
    APPLE
}

/**
 * Dados de login social
 */
data class SocialLoginData(
    val provider: SocialProvider,
    val token: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)