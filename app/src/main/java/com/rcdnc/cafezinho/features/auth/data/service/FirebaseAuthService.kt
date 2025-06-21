package com.rcdnc.cafezinho.features.auth.data.service

import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.rcdnc.cafezinho.features.auth.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Authentication service for phone verification
 * Handles SMS OTP verification and user authentication
 */
// @Singleton
class FirebaseAuthService /* @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) */ {
    
    // Temporary simplified constructor for compilation
    constructor() {}
    
    /**
     * Current authenticated user
     */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    
    /**
     * Authentication state flow
     */
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
    
    /**
     * Send phone verification code via SMS
     * @param phoneNumber International format phone number (+55...)
     * @param activity Activity for SMS retrieval
     * @return Verification ID for OTP confirmation
     */
    suspend fun sendPhoneVerification(
        phoneNumber: String,
        activity: android.app.Activity
    ): Result<String> {
        return try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // Auto-verification completed
                    }
                    
                    override fun onVerificationFailed(e: FirebaseException) {
                        // Verification failed
                    }
                    
                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Code sent successfully
                    }
                })
                .build()
            
            // This is a simplified version - in practice, you'd need to handle the callback flow
            PhoneAuthProvider.verifyPhoneNumber(options)
            
            // For now, return a placeholder - this needs proper callback handling
            Result.failure(Exception("Phone verification needs proper callback implementation"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verify phone OTP code
     * @param verificationId From sendPhoneVerification
     * @param otp 6-digit verification code
     * @return Firebase User if successful
     */
    suspend fun verifyPhoneOtp(
        verificationId: String,
        otp: String
    ): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign in with existing Firebase credential (Google, Facebook)
     * @param credential AuthCredential from provider
     * @return Firebase User if successful
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Link phone credential to existing user
     * @param verificationId From phone verification
     * @param otp 6-digit verification code
     * @return Updated Firebase User
     */
    suspend fun linkPhoneCredential(
        verificationId: String,
        otp: String
    ): Result<FirebaseUser> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No authenticated user"))
            
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = user.linkWithCredential(credential).await()
            val updatedUser = authResult.user
            
            if (updatedUser != null) {
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("Phone linking failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convert Firebase User to domain User model
     */
    fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email,
            phone = phoneNumber,
            firstName = displayName?.split(" ")?.firstOrNull(),
            isVerified = isEmailVerified || !phoneNumber.isNullOrEmpty(),
            socialId = providerId,
            socialType = when (providerId) {
                GoogleAuthProvider.PROVIDER_ID -> com.rcdnc.cafezinho.features.auth.domain.model.SocialAuthType.GOOGLE
                FacebookAuthProvider.PROVIDER_ID -> com.rcdnc.cafezinho.features.auth.domain.model.SocialAuthType.FACEBOOK
                else -> null
            }
        )
    }
    
    /**
     * Get current user auth token
     */
    suspend fun getCurrentUserToken(): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No authenticated user"))
            
            val token = user.getIdToken(false).await()
            Result.success(token.token ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign out current user
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete current user account
     */
    suspend fun deleteUser(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No authenticated user"))
            
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}