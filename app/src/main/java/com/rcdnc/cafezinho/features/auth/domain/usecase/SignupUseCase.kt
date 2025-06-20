package com.rcdnc.cafezinho.features.auth.domain.usecase

import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun signup(phone: String): Result<String> {
        return try {
            authRepository.signup(phone)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        return try {
            authRepository.verifyOtp(phone, otp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}