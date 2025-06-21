package com.rcdnc.cafezinho.features.auth.data.service

import android.content.Context
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Facebook Authentication service for social login
 * Handles Facebook SDK integration and token management
 */
@Singleton
class FacebookAuthService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val loginManager = LoginManager.getInstance()
    
    /**
     * Sign in with Facebook
     * @param activity Activity for Facebook login
     * @return Facebook access token for Firebase authentication
     */
    suspend fun signInWithFacebook(activity: android.app.Activity): Result<AccessToken> {
        return suspendCancellableCoroutine { continuation ->
            val callbackManager = CallbackManager.Factory.create()
            
            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    continuation.resume(Result.success(accessToken))
                }
                
                override fun onCancel() {
                    continuation.resume(Result.failure(Exception("Facebook login cancelled")))
                }
                
                override fun onError(error: FacebookException) {
                    continuation.resume(Result.failure(error))
                }
            })
            
            // Request basic permissions
            loginManager.logInWithReadPermissions(
                activity,
                listOf("email", "public_profile")
            )
            
            continuation.invokeOnCancellation {
                // Cleanup if coroutine is cancelled
            }
        }
    }
    
    /**
     * Create Firebase AuthCredential from Facebook access token
     * @param accessToken Facebook access token
     * @return Firebase AuthCredential for authentication
     */
    fun createFirebaseCredential(accessToken: AccessToken): AuthCredential {
        return FacebookAuthProvider.getCredential(accessToken.token)
    }
    
    /**
     * Sign in with Facebook and get Firebase credential
     * @param activity Activity for Facebook login
     * @return Firebase AuthCredential ready for authentication
     */
    suspend fun signInAndGetCredential(activity: android.app.Activity): Result<AuthCredential> {
        return signInWithFacebook(activity).mapCatching { accessToken ->
            createFirebaseCredential(accessToken)
        }
    }
    
    /**
     * Get user profile information from Facebook Graph API
     * @param accessToken Facebook access token
     * @return User profile data
     */
    suspend fun getUserProfile(accessToken: AccessToken): Result<FacebookUserProfile> {
        return suspendCancellableCoroutine { continuation ->
            val request = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
                if (response.error != null) {
                    continuation.resume(Result.failure(Exception(response.error.errorMessage)))
                } else {
                    try {
                        val profile = FacebookUserProfile(
                            id = jsonObject.getString("id"),
                            firstName = jsonObject.optString("first_name", ""),
                            lastName = jsonObject.optString("last_name", ""),
                            email = jsonObject.optString("email", ""),
                            profilePictureUrl = "https://graph.facebook.com/${jsonObject.getString("id")}/picture?type=large"
                        )
                        continuation.resume(Result.success(profile))
                    } catch (e: Exception) {
                        continuation.resume(Result.failure(e))
                    }
                }
            }
            
            val parameters = Bundle().apply {
                putString("fields", "id,first_name,last_name,email")
            }
            request.parameters = parameters
            request.executeAsync()
            
            continuation.invokeOnCancellation {
                // Cleanup if coroutine is cancelled
            }
        }
    }
    
    /**
     * Get current Facebook access token
     */
    fun getCurrentAccessToken(): AccessToken? {
        return AccessToken.getCurrentAccessToken()
    }
    
    /**
     * Check if user is logged in to Facebook
     */
    fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }
    
    /**
     * Log out from Facebook
     */
    fun logOut() {
        loginManager.logOut()
    }
    
    /**
     * Refresh Facebook access token
     */
    suspend fun refreshAccessToken(): Result<AccessToken> {
        return suspendCancellableCoroutine { continuation ->
            AccessToken.refreshCurrentAccessTokenAsync(object : AccessToken.AccessTokenRefreshCallback {
                override fun OnTokenRefreshed(accessToken: AccessToken?) {
                    if (accessToken != null) {
                        continuation.resume(Result.success(accessToken))
                    } else {
                        continuation.resume(Result.failure(Exception("Token refresh failed")))
                    }
                }
                
                override fun OnTokenRefreshFailed(exception: FacebookException?) {
                    continuation.resume(Result.failure(exception ?: Exception("Token refresh failed")))
                }
            })
            
            continuation.invokeOnCancellation {
                // Cleanup if coroutine is cancelled
            }
        }
    }
}

/**
 * Data class for Facebook user profile information
 */
data class FacebookUserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profilePictureUrl: String
)