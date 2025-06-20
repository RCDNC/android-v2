package com.rcdnc.cafezinho.features.auth.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.rcdnc.binderstatic.ApiClasses.ApiService
import com.rcdnc.binderstatic.ApiClasses.ApiUrl
import com.rcdnc.binderstatic.ApiClasses.helpers.ApiCommonHelper
import com.rcdnc.binderstatic.SimpleClasses.Variables
import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // TODO: Implement standard email/password login if needed
            Result.failure(Exception("Email/password login not implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithPhone(phone: String, otp: String): Result<User> {
        return try {
            // This would typically be used for social logins in the current system
            // Phone/OTP login might need to be implemented as a new endpoint
            Result.failure(Exception("Phone/OTP login not implemented in current API"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(phone: String): Result<String> {
        return try {
            // Phone signup would need to be implemented as a new endpoint
            Result.failure(Exception("Phone signup not implemented in current API"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        return try {
            // OTP verification would need to be implemented as a new endpoint
            Result.failure(Exception("OTP verification not implemented in current API"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun authenticateWithSocial(
        provider: String, // "google", "facebook", "debug"
        params: Map<String, Any>
    ): Result<User> = suspendCancellableCoroutine { continuation ->
        try {
            // Using existing ApiCommonHelper for social authentication
            val apiHelper = ApiCommonHelper(context)
            
            apiHelper.authenticateUser(
                provider,
                params,
                { response ->
                    try {
                        val jsonResponse = response.body
                        val success = jsonResponse.optBoolean("success", false)
                        
                        if (success) {
                            val userData = jsonResponse.optJSONObject("user")
                            if (userData != null) {
                                val user = User(
                                    id = userData.optString("id", ""),
                                    email = userData.optString("email", ""),
                                    firstName = userData.optString("firstName", ""),
                                    phone = userData.optString("phone", ""),
                                    isVerified = userData.optInt("verified", 0) == 1
                                )
                                
                                // Save auth token
                                val token = jsonResponse.optString("token")
                                if (token.isNotEmpty()) {
                                    sharedPreferences.edit()
                                        .putString(Variables.authToken, token)
                                        .apply()
                                    ApiService.authToken = token
                                }
                                
                                continuation.resume(Result.success(user))
                            } else {
                                continuation.resume(Result.failure(Exception("User data not found in response")))
                            }
                        } else {
                            val message = jsonResponse.optString("message", "Authentication failed")
                            continuation.resume(Result.failure(Exception(message)))
                        }
                    } catch (e: Exception) {
                        continuation.resume(Result.failure(e))
                    }
                },
                { error ->
                    continuation.resume(Result.failure(Exception(error.message ?: "Authentication failed")))
                }
            )
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // Clear auth token and user data
            sharedPreferences.edit()
                .remove(Variables.authToken)
                .remove("socialId")
                .remove("socialType")
                .apply()
            
            ApiService.authToken = null
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val token = sharedPreferences.getString(Variables.authToken, null)
            if (token.isNullOrEmpty()) {
                null
            } else {
                // Return user data from SharedPreferences using actual Variables constants
                User(
                    id = sharedPreferences.getString(Variables.uid, "") ?: "",
                    email = sharedPreferences.getString("email", "") ?: "",
                    firstName = sharedPreferences.getString(Variables.fName, "") ?: "",
                    phone = sharedPreferences.getString(Variables.uPhone, "") ?: "",
                    isVerified = sharedPreferences.getBoolean("verified", false),
                    token = token
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}