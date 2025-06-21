package com.rcdnc.cafezinho.features.auth.data.service

import android.content.Context
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Real Facebook authentication service using Facebook SDK
 * Matches the implementation from legacy Android app
 */
class RealFacebookAuthService(private val context: Context) {
    
    private val loginManager = LoginManager.getInstance()
    
    /**
     * Sign in with Facebook
     * Returns FacebookUserData on success
     */
    suspend fun signInWithFacebook(): Result<FacebookUserData> {
        return try {
            // First get Facebook access token
            val accessToken = getCurrentAccessToken()
            
            if (accessToken != null && !accessToken.isExpired) {
                // Already logged in, get user data
                getUserData(accessToken)
            } else {
                Result.failure(Exception("Facebook login required - call loginWithActivity() first"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("Facebook Sign-In failed: ${e.message}"))
        }
    }
    
    /**
     * Login with Facebook using Activity (must be called from Activity)
     * This starts the Facebook login flow
     */
    fun loginWithActivity(activity: androidx.fragment.app.FragmentActivity, callback: (Result<FacebookUserData>) -> Unit) {
        val callbackManager = CallbackManager.Factory.create()
        
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // Get user data after successful login
                val accessToken = result.accessToken
                getUserDataSync(accessToken) { userDataResult ->
                    callback(userDataResult)
                }
            }
            
            override fun onCancel() {
                callback(Result.failure(Exception("Facebook login was cancelled")))
            }
            
            override fun onError(error: FacebookException) {
                callback(Result.failure(Exception("Facebook login error: ${error.message}")))
            }
        })
        
        // Start login with required permissions
        loginManager.logInWithReadPermissions(activity, listOf("public_profile", "email"))
    }
    
    /**
     * Get current Facebook access token
     */
    private fun getCurrentAccessToken(): AccessToken? {
        return AccessToken.getCurrentAccessToken()
    }
    
    /**
     * Get user data from Facebook Graph API
     */
    private suspend fun getUserData(accessToken: AccessToken): Result<FacebookUserData> {
        return suspendCancellableCoroutine { continuation ->
            val request = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
                try {
                    if (response?.error != null) {
                        continuation.resume(Result.failure(Exception("Facebook API error: ${response.error?.errorMessage}")))
                        return@newMeRequest
                    }
                    
                    val facebookData = FacebookUserData(
                        socialId = jsonObject?.getString("id") ?: "",
                        email = jsonObject?.optString("email"),
                        firstName = jsonObject?.optString("first_name"),
                        lastName = jsonObject?.optString("last_name"),
                        accessToken = accessToken.token
                    )
                    
                    continuation.resume(Result.success(facebookData))
                    
                } catch (e: Exception) {
                    continuation.resume(Result.failure(Exception("Error parsing Facebook response: ${e.message}")))
                }
            }
            
            // Set parameters for the request
            val parameters = android.os.Bundle()
            parameters.putString("fields", "last_name,first_name,email")
            request.parameters = parameters
            request.executeAsync()
        }
    }
    
    /**
     * Synchronous version for callback-based API
     */
    private fun getUserDataSync(accessToken: AccessToken, callback: (Result<FacebookUserData>) -> Unit) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
            try {
                if (response?.error != null) {
                    callback(Result.failure(Exception("Facebook API error: ${response.error?.errorMessage}")))
                    return@newMeRequest
                }
                
                val facebookData = FacebookUserData(
                    socialId = jsonObject?.getString("id") ?: "",
                    email = jsonObject?.optString("email"),
                    firstName = jsonObject?.optString("first_name"),
                    lastName = jsonObject?.optString("last_name"),
                    accessToken = accessToken.token
                )
                
                callback(Result.success(facebookData))
                
            } catch (e: Exception) {
                callback(Result.failure(Exception("Error parsing Facebook response: ${e.message}")))
            }
        }
        
        // Set parameters for the request
        val parameters = android.os.Bundle()
        parameters.putString("fields", "last_name,first_name,email")
        request.parameters = parameters
        request.executeAsync()
    }
    
    /**
     * Logout from Facebook
     */
    fun logout() {
        loginManager.logOut()
    }
}

/**
 * Data class for Facebook user information
 */
data class FacebookUserData(
    val socialId: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val accessToken: String
)