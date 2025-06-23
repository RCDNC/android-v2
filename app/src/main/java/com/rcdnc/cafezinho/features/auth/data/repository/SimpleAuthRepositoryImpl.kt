package com.rcdnc.cafezinho.features.auth.data.repository

import android.app.Activity
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.features.auth.domain.model.*
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação simples da AuthRepository
 * Funciona com a interface existente e AuthManager
 */
@Singleton
class SimpleAuthRepositoryImpl @Inject constructor(
    private val authManager: AuthManager
) : AuthRepository {
    
    private val _authStatus = MutableStateFlow(AuthStatus.UNAUTHENTICATED)
    private val _currentUser = MutableStateFlow<User?>(null)
    
    override fun observeAuthStatus(): Flow<AuthStatus> = _authStatus.asStateFlow()
    
    override fun observeCurrentUser(): Flow<User?> = _currentUser.asStateFlow()
    
    override fun getCurrentUser(): User? = _currentUser.value
    
    override fun getCurrentToken(): String? {
        return runCatching {
            kotlinx.coroutines.runBlocking { authManager.getCurrentToken() }
        }.getOrNull()
    }
    
    override fun isAuthenticated(): Boolean {
        return runCatching {
            kotlinx.coroutines.runBlocking { authManager.isAuthenticated() }
        }.getOrDefault(false)
    }
    
    override suspend fun login(credentials: LoginCredentials): Result<AuthResult> {
        return try {
            // Usando demo login do AuthManager
            val success = authManager.demoLogin(credentials.email, credentials.password)
            
            if (success) {
                val user = createDemoUser(credentials.email)
                _currentUser.value = user
                _authStatus.value = AuthStatus.AUTHENTICATED
                
                Result.success(
                    AuthResult(
                        user = user,
                        token = "demo-token-${System.currentTimeMillis()}",
                        refreshToken = "demo-refresh-token",
                        expiresIn = 3600
                    )
                )
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(registerData: RegisterData): Result<AuthResult> {
        return try {
            // Demo registration
            val user = User(
                id = "demo-${System.currentTimeMillis()}",
                email = registerData.email,
                firstName = registerData.firstName,
                lastName = registerData.lastName,
                phoneNumber = registerData.phoneNumber,
                dateOfBirth = registerData.dateOfBirth,
                isEmailVerified = false,
                isPhoneVerified = false,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString()
            )
            
            // Save to AuthManager
            authManager.saveAuthData(
                token = "demo-token-${System.currentTimeMillis()}",
                userId = user.id,
                email = user.email,
                name = "${user.firstName} ${user.lastName ?: ""}".trim()
            )
            
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            
            Result.success(
                AuthResult(
                    user = user,
                    token = "demo-token-${System.currentTimeMillis()}",
                    refreshToken = "demo-refresh-token",
                    expiresIn = 3600
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun socialLogin(socialData: SocialLoginData): Result<AuthResult> {
        return try {
            // Demo social login
            val user = createDemoUser(socialData.email ?: "social@example.com")
            
            authManager.saveAuthData(
                token = "demo-social-token-${System.currentTimeMillis()}",
                userId = user.id,
                email = user.email,
                name = "${user.firstName} ${user.lastName ?: ""}".trim()
            )
            
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            
            Result.success(
                AuthResult(
                    user = user,
                    token = "demo-social-token-${System.currentTimeMillis()}",
                    refreshToken = "demo-refresh-token",
                    expiresIn = 3600
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            authManager.logout()
            _currentUser.value = null
            _authStatus.value = AuthStatus.UNAUTHENTICATED
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(): Result<AuthResult> {
        return try {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                Result.success(
                    AuthResult(
                        user = currentUser,
                        token = "refreshed-token-${System.currentTimeMillis()}",
                        refreshToken = "new-refresh-token",
                        expiresIn = 3600
                    )
                )
            } else {
                Result.failure(Exception("No user to refresh"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            // Demo implementation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun confirmPasswordReset(resetData: PasswordResetConfirmData): Result<Unit> {
        return try {
            // Demo implementation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyEmail(token: String): Result<Unit> {
        return try {
            _currentUser.value?.let { user ->
                _currentUser.value = user.copy(isEmailVerified = true)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resendVerificationEmail(): Result<Unit> {
        return try {
            // Demo implementation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            // Demo implementation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserData(
        firstName: String,
        lastName: String?,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    updatedAt = System.currentTimeMillis().toString()
                )
                _currentUser.value = updatedUser
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("No user to update"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            authManager.logout()
            _currentUser.value = null
            _authStatus.value = AuthStatus.UNAUTHENTICATED
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateToken(): Result<Boolean> {
        return try {
            val isAuth = authManager.isAuthenticated()
            Result.success(isAuth)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearAuthData(): Result<Unit> {
        return try {
            authManager.logout()
            _currentUser.value = null
            _authStatus.value = AuthStatus.UNAUTHENTICATED
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createDemoUser(email: String): User {
        return User(
            id = "demo-${System.currentTimeMillis()}",
            email = email,
            firstName = "Demo",
            lastName = "User",
            phoneNumber = null,
            dateOfBirth = null,
            isEmailVerified = true,
            isPhoneVerified = false,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString()
        )
    }
    
    // Social Login Methods
    override suspend fun signInWithGoogle(): Result<User> {
        return try {
            val user = createDemoUser("google@example.com")
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signInWithFacebook(): Result<User> {
        return try {
            val user = createDemoUser("facebook@example.com")
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Phone Authentication Methods
    override suspend fun sendPhoneVerification(phoneNumber: String, activity: Activity?): Result<String> {
        return try {
            // Mock verification ID
            Result.success("mock_verification_id_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<User> {
        return try {
            if (otp == "123456") {
                val user = createDemoUser("phone@example.com")
                _currentUser.value = user
                _authStatus.value = AuthStatus.AUTHENTICATED
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resendPhoneVerification(phoneNumber: String): Result<String> {
        return try {
            Result.success("mock_verification_id_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Alternative Login Methods
    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val user = createDemoUser(email)
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signInWithPhone(phone: String, otp: String): Result<User> {
        return try {
            if (otp == "123456") {
                val user = createDemoUser("$phone@example.com")
                _currentUser.value = user
                _authStatus.value = AuthStatus.AUTHENTICATED
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signOut(): Result<Unit> {
        return logout()
    }
    
    // Registration Methods
    override suspend fun completeRegistration(registrationData: RegistrationData): Result<User> {
        return try {
            val user = User(
                id = "demo-${System.currentTimeMillis()}",
                email = "${registrationData.phoneNumber}@example.com",
                firstName = registrationData.firstName ?: "Demo",
                lastName = null,
                phoneNumber = registrationData.phoneNumber,
                dateOfBirth = registrationData.dateOfBirth?.toString(),
                gender = registrationData.gender,
                isEmailVerified = false,
                isPhoneVerified = true,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString()
            )
            
            _currentUser.value = user
            _authStatus.value = AuthStatus.AUTHENTICATED
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProfileCompletionStatus(userId: String): Result<Boolean> {
        return try {
            // Mock profile completion status
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Session Management
    override suspend fun refreshAuthToken(): Result<Unit> {
        return try {
            // Mock token refresh
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Validation Methods
    override suspend fun validatePhoneNumber(phoneNumber: String): Result<String> {
        return try {
            // Simple validation - just format
            val formatted = if (phoneNumber.startsWith("+")) phoneNumber else "+55$phoneNumber"
            Result.success(formatted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}