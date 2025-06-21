package com.rcdnc.cafezinho.features.auth.data.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rcdnc.cafezinho.R
import kotlinx.coroutines.CancellationException

/**
 * Real Google authentication service using Credential Manager API
 * Matches the implementation from legacy Android app
 */
class RealGoogleAuthService(private val context: Context) {
    
    private val credentialManager = CredentialManager.create(context)
    
    /**
     * Sign in with Google using Credential Manager
     * Returns GoogleIdTokenCredential on success
     */
    suspend fun signInWithGoogle(): Result<GoogleIdTokenCredential> {
        return try {
            val webClientId = context.getString(R.string.google_web_client_id)
            
            // Create Google ID option with fallback strategy
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false) // Allow all accounts
                .setAutoSelectEnabled(true)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result = credentialManager.getCredential(
                request = request,
                context = context as androidx.activity.ComponentActivity
            )
            
            val credential = result.credential
            
            if (credential is GoogleIdTokenCredential) {
                Result.success(credential)
            } else {
                Result.failure(Exception("Received unexpected credential type"))
            }
            
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In failed: ${e.message}"))
        } catch (e: CancellationException) {
            Result.failure(Exception("Google Sign-In was cancelled"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error during Google Sign-In: ${e.message}"))
        }
    }
    
    /**
     * Extract user data from Google ID token credential
     */
    fun extractUserData(credential: GoogleIdTokenCredential): GoogleUserData {
        return GoogleUserData(
            socialId = credential.id,
            email = credential.id, // Google ID is usually email
            firstName = credential.givenName,
            lastName = credential.familyName,
            displayName = credential.displayName,
            profilePictureUri = credential.profilePictureUri?.toString(),
            idToken = credential.idToken
        )
    }
}

/**
 * Data class for Google user information
 */
data class GoogleUserData(
    val socialId: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val displayName: String?,
    val profilePictureUri: String?,
    val idToken: String
)