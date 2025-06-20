package com.rcdnc.cafezinho.features.auth.domain.usecase

import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        provider: String,
        params: Map<String, Any>
    ): Result<User> {
        return try {
            when {
                provider.isBlank() -> {
                    Result.failure(Exception("Provider cannot be empty"))
                }
                params.isEmpty() -> {
                    Result.failure(Exception("Authentication parameters are required"))
                }
                else -> {
                    val authResult = authRepository.authenticateWithSocial(provider, params)
                    authResult.fold(
                        onSuccess = { user -> validateUserData(user) },
                        onFailure = { exception -> Result.failure(exception) }
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithPhone(phone: String, otp: String): Result<User> {
        return try {
            when {
                phone.isBlank() -> {
                    Result.failure(Exception("Phone number cannot be empty"))
                }
                otp.isBlank() -> {
                    Result.failure(Exception("OTP cannot be empty"))
                }
                else -> {
                    val authResult = authRepository.loginWithPhone(phone, otp)
                    authResult.fold(
                        onSuccess = { user -> validateUserData(user) },
                        onFailure = { exception -> Result.failure(exception) }
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateUserData(user: User): Result<User> {
        return when {
            user.id.isBlank() -> {
                Result.failure(Exception("Invalid user ID received from authentication"))
            }
            user.firstName.isNullOrBlank() -> {
                Result.failure(Exception("User first name is required"))
            }
            else -> {
                Result.success(user)
            }
        }
    }
}