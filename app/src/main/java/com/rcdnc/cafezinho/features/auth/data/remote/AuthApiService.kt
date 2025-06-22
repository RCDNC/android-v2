package com.rcdnc.cafezinho.features.auth.data.remote

import com.rcdnc.cafezinho.features.auth.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para autenticação
 * Endpoints baseados na API Laravel real do Cafezinho
 */
interface AuthApiService {
    
    /**
     * Login com email e senha
     * Endpoint: POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    /**
     * Registro de novo usuário
     * Endpoint: POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    /**
     * Login social (Google, Facebook)
     * Endpoint: POST /api/auth/social-login
     */
    @POST("auth/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest
    ): Response<AuthResponse>
    
    /**
     * Logout do usuário atual
     * Endpoint: POST /api/auth/logout
     */
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
    
    /**
     * Refresh do token de acesso
     * Endpoint: POST /api/auth/refresh
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<AuthResponse>
    
    /**
     * Validação de token atual
     * Endpoint: GET /api/auth/me
     */
    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>
    
    /**
     * Solicitação de redefinição de senha
     * Endpoint: POST /api/auth/password/reset
     */
    @POST("auth/password/reset")
    suspend fun requestPasswordReset(
        @Body request: PasswordResetRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Confirmação de redefinição de senha
     * Endpoint: POST /api/auth/password/reset/confirm
     */
    @POST("auth/password/reset/confirm")
    suspend fun confirmPasswordReset(
        @Body request: PasswordResetConfirmRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Verificação de email
     * Endpoint: POST /api/auth/email/verify
     */
    @POST("auth/email/verify")
    suspend fun verifyEmail(
        @Body request: EmailVerificationRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Reenvio de email de verificação
     * Endpoint: POST /api/auth/email/resend
     */
    @POST("auth/email/resend")
    suspend fun resendVerificationEmail(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
    
    /**
     * Atualização de senha
     * Endpoint: PUT /api/auth/password
     */
    @PUT("auth/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Atualização de dados do usuário
     * Endpoint: PUT /api/auth/user
     */
    @PUT("auth/user")
    suspend fun updateUserData(
        @Header("Authorization") token: String,
        @Body request: UpdateUserRequest
    ): Response<UserResponse>
    
    /**
     * Exclusão de conta
     * Endpoint: DELETE /api/auth/account
     */
    @DELETE("auth/account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String,
        @Body request: DeleteAccountRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Validação de token (health check)
     * Endpoint: GET /api/auth/validate
     */
    @GET("auth/validate")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<ApiResponse<TokenValidationData>>
}