package com.rcdnc.cafezinho.features.auth.data.repository

import android.content.Context
import com.rcdnc.cafezinho.features.auth.data.model.LoginRequest
import com.rcdnc.cafezinho.features.auth.data.network.NetworkModule
import com.rcdnc.cafezinho.features.auth.data.service.RealGoogleAuthService
import com.rcdnc.cafezinho.features.auth.data.service.RealFacebookAuthService
import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Real authentication repository that connects to the API
 * Integrates Google/Facebook auth with Laravel Sanctum API
 */
class RealAuthRepositoryImpl(private val context: Context) : AuthRepository {
    
    private val apiService = NetworkModule.createApiService()
    private val googleAuthService = RealGoogleAuthService(context)
    private val facebookAuthService = RealFacebookAuthService(context)
    
    override suspend fun signInWithGoogle(): Result<User> {
        return try {
            // Step 1: Get Google credential
            val credentialResult = googleAuthService.signInWithGoogle()
            if (credentialResult.isFailure) {
                return Result.failure(credentialResult.exceptionOrNull() ?: Exception("Google sign-in failed"))
            }
            
            val credential = credentialResult.getOrThrow()
            val userData = googleAuthService.extractUserData(credential)
            
            // Step 2: Login with API
            val loginRequest = LoginRequest(
                socialId = userData.socialId,
                social = "google",
                email = userData.email,
                webId = context.getString(com.rcdnc.cafezinho.R.string.google_web_client_id),
                authToken = userData.idToken
            )
            
            val apiResponse = apiService.login("google", loginRequest)
            
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                val authResponse = apiResponse.body()!!
                val apiUser = authResponse.msg.user
                
                // Convert API user to domain user
                val user = User(
                    id = apiUser.id.toString(),
                    firstName = apiUser.firstName,
                    lastName = apiUser.lastName,
                    email = apiUser.email,
                    phone = apiUser.phone,
                    gender = apiUser.gender,
                    genderPreference = apiUser.showMeGender,
                    bio = apiUser.bio,
                    dateOfBirth = apiUser.dob?.let { parseDate(it) },
                    profilePhotos = authResponse.msg.userImages?.map { it.imageUrl } ?: emptyList(),
                    isVerified = true,
                    isProfileComplete = !apiUser.firstName.isNullOrEmpty()
                )
                
                // Save auth token
                saveAuthToken(apiUser.authToken)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${apiResponse.message()}"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("Google authentication failed: ${e.message}"))
        }
    }
    
    override suspend fun signInWithFacebook(): Result<User> {
        return try {
            // Step 1: Get Facebook data
            val facebookResult = facebookAuthService.signInWithFacebook()
            if (facebookResult.isFailure) {
                return Result.failure(facebookResult.exceptionOrNull() ?: Exception("Facebook sign-in failed"))
            }
            
            val userData = facebookResult.getOrThrow()
            
            // Step 2: Login with API  
            val loginRequest = LoginRequest(
                socialId = userData.socialId,
                social = "facebook",
                email = userData.email
            )
            
            val apiResponse = apiService.login("facebook", loginRequest)
            
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                val authResponse = apiResponse.body()!!
                val apiUser = authResponse.msg.user
                
                // Convert API user to domain user
                val user = User(
                    id = apiUser.id.toString(),
                    firstName = apiUser.firstName,
                    lastName = apiUser.lastName,
                    email = apiUser.email,
                    phone = apiUser.phone,
                    gender = apiUser.gender,
                    genderPreference = apiUser.showMeGender,
                    bio = apiUser.bio,
                    dateOfBirth = apiUser.dob?.let { parseDate(it) },
                    profilePhotos = authResponse.msg.userImages?.map { it.imageUrl } ?: emptyList(),
                    isVerified = true,
                    isProfileComplete = !apiUser.firstName.isNullOrEmpty()
                )
                
                // Save auth token
                saveAuthToken(apiUser.authToken)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${apiResponse.message()}"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("Facebook authentication failed: ${e.message}"))
        }
    }
    
    // Utility methods
    private fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun saveAuthToken(token: String) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("auth_token", token).apply()
    }
    
    private fun getAuthToken(): String? {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
    
    // Placeholder implementations for other methods
    override suspend fun signInWithSocialCredential(provider: String, credential: Any): Result<User> {
        return Result.failure(NotImplementedError("Not implemented yet"))
    }
    
    override suspend fun sendPhoneVerification(phoneNumber: String): Result<String> {
        return Result.failure(NotImplementedError("Phone verification not implemented"))
    }
    
    override suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<User> {
        return Result.failure(NotImplementedError("Phone verification not implemented"))
    }
    
    override suspend fun resendPhoneVerification(phoneNumber: String): Result<String> {
        return Result.failure(NotImplementedError("Phone verification not implemented"))
    }
    
    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return Result.failure(NotImplementedError("Email login not supported"))
    }
    
    override suspend fun signInWithPhone(phone: String, otp: String): Result<User> {
        return Result.failure(NotImplementedError("Phone login not supported"))
    }
    
    override suspend fun registerWithPhone(phoneNumber: String): Result<String> {
        return Result.failure(NotImplementedError("Phone registration not implemented"))
    }
    
    override suspend fun completeRegistration(registrationData: RegistrationData): Result<User> {
        return Result.failure(NotImplementedError("Registration not implemented"))
    }
    
    override suspend fun updateUserProfile(userId: String, profileData: Map<String, Any>): Result<User> {
        return Result.failure(NotImplementedError("Profile update not implemented"))
    }
    
    override suspend fun uploadProfilePhoto(userId: String, photoUri: String): Result<String> {
        return Result.failure(NotImplementedError("Photo upload not implemented"))
    }
    
    override suspend fun updateProfileField(userId: String, field: String, value: Any): Result<Unit> {
        return Result.failure(NotImplementedError("Profile field update not implemented"))
    }
    
    override suspend fun getProfileCompletionStatus(userId: String): Result<Boolean> {
        return Result.success(true)
    }
    
    override suspend fun getCurrentUser(): User? {
        return null // TODO: Implement user session retrieval
    }
    
    override suspend fun refreshAuthToken(): Result<String> {
        return Result.failure(NotImplementedError("Token refresh not implemented"))
    }
    
    override suspend fun signOut(): Result<Unit> {
        // Clear local auth token
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("auth_token").apply()
        
        // Logout from social providers
        facebookAuthService.logout()
        
        return Result.success(Unit)
    }
    
    override suspend fun deleteAccount(userId: String): Result<Unit> {
        return Result.failure(NotImplementedError("Account deletion not implemented"))
    }
    
    override suspend fun validatePhoneNumber(phoneNumber: String): Result<String> {
        return if (phoneNumber.length >= 10) {
            Result.success(phoneNumber)
        } else {
            Result.failure(Exception("Invalid phone number"))
        }
    }
    
    override suspend fun checkUserExists(phoneNumber: String): Result<Boolean> {
        return Result.success(false)
    }
    
    override suspend fun checkEmailAvailable(email: String): Result<Boolean> {
        return Result.success(true)
    }
    
    // Legacy methods
    override suspend fun authenticateWithSocial(provider: String, params: Map<String, Any>): Result<User> {
        return when (provider.lowercase()) {
            "google" -> signInWithGoogle()
            "facebook" -> signInWithFacebook()
            else -> Result.failure(IllegalArgumentException("Unsupported provider: $provider"))
        }
    }
    
    override suspend fun login(email: String, password: String): Result<User> = signInWithEmail(email, password)
    override suspend fun loginWithPhone(phone: String, otp: String): Result<User> = signInWithPhone(phone, otp)
    override suspend fun signup(phone: String): Result<String> = registerWithPhone(phone)
    override suspend fun verifyOtp(phone: String, otp: String): Result<User> = signInWithPhone(phone, otp)
    override suspend fun logout(): Result<Unit> = signOut()
}