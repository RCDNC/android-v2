package com.rcdnc.cafezinho.features.auth.data.remote

import com.rcdnc.cafezinho.features.auth.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
    
    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<LoginResponse>
}

data class LoginRequest(
    val phone: String,
    val otp: String
)

data class SignupRequest(
    val phone: String
)

data class VerifyOtpRequest(
    val phone: String,
    val otp: String
)

data class LoginResponse(
    val success: Boolean,
    val user: User?,
    val token: String?,
    val message: String?
)

data class SignupResponse(
    val success: Boolean,
    val otpId: String?,
    val message: String?
)