package com.rcdnc.cafezinho.features.auth.domain.repository

import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData

/**
 * Authentication repository interface
 * Defines all authentication operations following Clean Architecture
 */
interface AuthRepository {
    
    // Social Authentication
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithFacebook(): Result<User>
    suspend fun signInWithSocialCredential(provider: String, credential: Any): Result<User>
    
    // Phone Authentication
    suspend fun sendPhoneVerification(phoneNumber: String): Result<String> // Returns verification ID
    suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<User>
    suspend fun resendPhoneVerification(phoneNumber: String): Result<String>
    
    // Traditional Authentication (if needed)
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithPhone(phone: String, otp: String): Result<User>
    
    // Registration
    suspend fun registerWithPhone(phoneNumber: String): Result<String> // Returns verification ID
    suspend fun completeRegistration(registrationData: RegistrationData): Result<User>
    suspend fun updateUserProfile(userId: String, profileData: Map<String, Any>): Result<User>
    
    // Profile Management
    suspend fun uploadProfilePhoto(userId: String, photoUri: String): Result<String> // Returns photo URL
    suspend fun updateProfileField(userId: String, field: String, value: Any): Result<Unit>
    suspend fun getProfileCompletionStatus(userId: String): Result<Boolean>
    
    // Session Management
    suspend fun getCurrentUser(): User?
    suspend fun refreshAuthToken(): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(userId: String): Result<Unit>
    
    // Validation
    suspend fun validatePhoneNumber(phoneNumber: String): Result<String> // Returns formatted phone
    suspend fun checkUserExists(phoneNumber: String): Result<Boolean>
    suspend fun checkEmailAvailable(email: String): Result<Boolean>
    
    // Legacy support (for migration)
    suspend fun authenticateWithSocial(provider: String, params: Map<String, Any>): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithPhone(phone: String, otp: String): Result<User>
    suspend fun signup(phone: String): Result<String>
    suspend fun verifyOtp(phone: String, otp: String): Result<User>
    suspend fun logout(): Result<Unit>
}