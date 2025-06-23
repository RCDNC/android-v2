/*
TEMPORARIAMENTE DESABILITADO - Conflitos com interface AuthRepository
Será corrigido quando a interface estiver estabilizada
*/

/*
package com.rcdnc.cafezinho.features.auth.data.repository

import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simplified implementation of AuthRepository for compilation and testing
 * This allows us to build and test the UI flow without external dependencies
 */
@Singleton
class SimplifiedAuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signInWithGoogle(): Result<User> {
        return try {
            val mockUser = User(
                id = "google_user_${System.currentTimeMillis()}",
                firstName = "Google",
                lastName = "User",
                email = "google.user@example.com",
                isVerified = true
            )
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithFacebook(): Result<User> {
        return try {
            val mockUser = User(
                id = "facebook_user_${System.currentTimeMillis()}",
                firstName = "Facebook",
                lastName = "User",
                email = "facebook.user@example.com",
                isVerified = true
            )
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithSocialCredential(provider: String, credential: Any): Result<User> {
        return try {
            val mockUser = User(
                id = "${provider}_user_${System.currentTimeMillis()}",
                firstName = provider.capitalize(),
                lastName = "User",
                email = "$provider.user@example.com",
                isVerified = true
            )
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPhoneVerification(phoneNumber: String): Result<String> {
        return try {
            // Mock verification ID
            Result.success("mock_verification_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<User> {
        return try {
            if (otp == "123456") {
                val mockUser = User(
                    id = "phone_user_${System.currentTimeMillis()}",
                    firstName = "Phone",
                    lastName = "User",
                    phone = "+5511999999999",
                    isVerified = true
                )
                Result.success(mockUser)
            } else {
                Result.failure(Exception("Invalid OTP. Use 123456 for testing."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendPhoneVerification(phoneNumber: String): Result<String> {
        return sendPhoneVerification(phoneNumber)
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return Result.failure(Exception("Email/password authentication not supported"))
    }

    override suspend fun signInWithPhone(phone: String, otp: String): Result<User> {
        return verifyPhoneOtp("mock_verification_id", otp)
    }

    override suspend fun registerWithPhone(phoneNumber: String): Result<String> {
        return sendPhoneVerification(phoneNumber)
    }

    override suspend fun completeRegistration(registrationData: RegistrationData): Result<User> {
        return try {
            val mockUser = User(
                id = "registered_user_${System.currentTimeMillis()}",
                firstName = registrationData.firstName,
                dateOfBirth = registrationData.dateOfBirth?.let { java.util.Date(it) },
                gender = registrationData.gender,
                genderPreference = registrationData.genderPreference,
                bio = registrationData.bio,
                school = registrationData.school,
                profilePhotos = registrationData.photos,
                isProfileComplete = true,
                isVerified = true
            )
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(userId: String, profileData: Map<String, Any>): Result<User> {
        return try {
            val mockUser = User(
                id = userId,
                firstName = "Updated",
                lastName = "User",
                isVerified = true
            )
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(userId: String, photoUri: String): Result<String> {
        return try {
            Result.success("mock_photo_url_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfileField(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfileCompletionStatus(userId: String): Result<Boolean> {
        return try {
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return User(
            id = "current_user_mock",
            firstName = "Current",
            lastName = "User",
            email = "current@example.com",
            isVerified = true
        )
    }

    override suspend fun refreshAuthToken(): Result<String> {
        return try {
            Result.success("mock_token_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(userId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validatePhoneNumber(phoneNumber: String): Result<String> {
        return try {
            // Simple validation - just check if it has numbers
            if (phoneNumber.any { it.isDigit() } && phoneNumber.length >= 10) {
                Result.success(phoneNumber)
            } else {
                Result.failure(Exception("Invalid phone number format"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkUserExists(phoneNumber: String): Result<Boolean> {
        return try {
            Result.success(false) // Always return false for testing
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkEmailAvailable(email: String): Result<Boolean> {
        return try {
            Result.success(true) // Always return true for testing
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Legacy methods (for backward compatibility)
    override suspend fun authenticateWithSocial(provider: String, params: Map<String, Any>): Result<User> {
        return when (provider.lowercase()) {
            "google" -> signInWithGoogle()
            "facebook" -> signInWithFacebook()
            else -> Result.failure(IllegalArgumentException("Unsupported provider: $provider"))
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return signInWithEmail(email, password)
    }

    override suspend fun loginWithPhone(phone: String, otp: String): Result<User> {
        return signInWithPhone(phone, otp)
    }

    override suspend fun signup(phone: String): Result<String> {
        return registerWithPhone(phone)
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        return verifyPhoneOtp("mock_verification_id", otp)
    }

    override suspend fun logout(): Result<Unit> {
        return signOut()
    }
}*/
