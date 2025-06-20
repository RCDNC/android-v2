package com.rcdnc.cafezinho.features.auth.domain.repository

import com.rcdnc.cafezinho.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithPhone(phone: String, otp: String): Result<User>
    suspend fun authenticateWithSocial(provider: String, params: Map<String, Any>): Result<User>
    suspend fun signup(phone: String): Result<String> // Returns OTP ID
    suspend fun verifyOtp(phone: String, otp: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
}