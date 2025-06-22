package com.rcdnc.cafezinho.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para API de autenticação
 * Baseados na estrutura da API Laravel real do Cafezinho
 */

/**
 * Resposta genérica da API
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

/**
 * Request de login
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("remember_me") val rememberMe: Boolean = true
)

/**
 * Request de registro
 */
data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("accept_terms") val acceptTerms: Boolean
)

/**
 * Request de login social
 */
data class SocialLoginRequest(
    @SerializedName("provider") val provider: String, // "google", "facebook", "apple"
    @SerializedName("token") val token: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null
)

/**
 * Resposta de autenticação
 */
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: AuthData?
)

/**
 * Dados de autenticação da resposta
 */
data class AuthData(
    @SerializedName("user") val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

/**
 * DTO do usuário
 */
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("is_verified") val isVerified: Boolean?,
    @SerializedName("is_premium") val isPremium: Boolean?,
    @SerializedName("profile_completion") val profileCompletion: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("last_login_at") val lastLoginAt: String?
)

/**
 * Resposta de usuário
 */
data class UserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserDto?
)

/**
 * Request de redefinição de senha
 */
data class PasswordResetRequest(
    @SerializedName("email") val email: String
)

/**
 * Request de confirmação de redefinição de senha
 */
data class PasswordResetConfirmRequest(
    @SerializedName("email") val email: String,
    @SerializedName("token") val token: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

/**
 * Request de verificação de email
 */
data class EmailVerificationRequest(
    @SerializedName("token") val token: String
)

/**
 * Request de atualização de senha
 */
data class UpdatePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("new_password_confirmation") val newPasswordConfirmation: String
)

/**
 * Request de atualização de dados do usuário
 */
data class UpdateUserRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?
)

/**
 * Request de exclusão de conta
 */
data class DeleteAccountRequest(
    @SerializedName("password") val password: String,
    @SerializedName("reason") val reason: String? = null
)

/**
 * Dados de validação de token
 */
data class TokenValidationData(
    @SerializedName("is_valid") val isValid: Boolean,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("user_id") val userId: Int?
)