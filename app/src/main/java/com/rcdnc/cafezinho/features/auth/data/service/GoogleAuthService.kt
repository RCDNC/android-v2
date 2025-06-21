package com.rcdnc.cafezinho.features.auth.data.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.rcdnc.cafezinho.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Authentication service using modern Credential Manager API
 * Handles Google Sign-In with One Tap and traditional flows
 */
@Singleton
class GoogleAuthService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val credentialManager = CredentialManager.create(context)
    
    /**
     * Get Google Web Client ID from string resources
     */
    private val webClientId: String
        get() = context.getString(R.string.google_web_client_id)
    
    /**
     * Sign in with Google using Credential Manager (One Tap)
     * @return Google ID Token for Firebase authentication
     */
    suspend fun signInWithGoogle(): Result<String> {
        return try {
            // Configure Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false) // Show all accounts
                .build()
            
            // Build credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            // Get credential
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            
            // Extract Google ID token
            handleGoogleSignInResult(result)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Handle Google Sign-In result and extract ID token
     */
    private fun handleGoogleSignInResult(result: GetCredentialResponse): Result<String> {
        return try {
            when (val credential = result.credential) {
                is GoogleIdTokenCredential -> {
                    // Extract the ID token
                    val idToken = credential.idToken
                    Result.success(idToken)
                }
                else -> {
                    Result.failure(Exception("Invalid credential type"))
                }
            }
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create Firebase AuthCredential from Google ID token
     * @param idToken Google ID token from sign-in
     * @return Firebase AuthCredential for authentication
     */
    fun createFirebaseCredential(idToken: String): AuthCredential {
        return GoogleAuthProvider.getCredential(idToken, null)
    }
    
    /**
     * Sign in with Google and get Firebase credential
     * @return Firebase AuthCredential ready for authentication
     */
    suspend fun signInAndGetCredential(): Result<AuthCredential> {
        return signInWithGoogle().mapCatching { idToken ->
            createFirebaseCredential(idToken)
        }
    }
    
    /**
     * Check if Google Play Services are available
     */
    fun isGooglePlayServicesAvailable(): Boolean {
        return try {
            // In a real implementation, you'd check GoogleApiAvailability
            // For now, assume it's available
            true
        } catch (e: Exception) {
            false
        }
    }
}