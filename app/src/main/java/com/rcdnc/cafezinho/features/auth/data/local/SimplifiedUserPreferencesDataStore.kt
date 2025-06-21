package com.rcdnc.cafezinho.features.auth.data.local

import com.rcdnc.cafezinho.features.auth.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simplified DataStore implementation for compilation
 * This allows us to build and test without DataStore dependencies
 */
@Singleton
class SimplifiedUserPreferencesDataStore @Inject constructor() {
    
    // In-memory storage for demo purposes
    private var currentUser: User? = null
    private var authToken: String? = null
    private var isLoggedIn: Boolean = false
    private var isProfileComplete: Boolean = false
    private val profileData = mutableMapOf<String, Any>()
    
    suspend fun saveUserSession(user: User, authToken: String? = null) {
        this.currentUser = user
        this.authToken = authToken
        this.isLoggedIn = true
    }
    
    suspend fun updateAuthTokens(authToken: String, refreshToken: String? = null) {
        this.authToken = authToken
    }
    
    suspend fun getUserSession(): User? {
        return if (isLoggedIn) currentUser else null
    }
    
    suspend fun getAuthToken(): String? {
        return authToken
    }
    
    suspend fun isLoggedIn(): Boolean {
        return isLoggedIn
    }
    
    suspend fun clearSession() {
        currentUser = null
        authToken = null
        isLoggedIn = false
        isProfileComplete = false
        profileData.clear()
    }
    
    suspend fun updateProfileCompletion(isComplete: Boolean) {
        isProfileComplete = isComplete
    }
    
    suspend fun isProfileComplete(): Boolean {
        return isProfileComplete
    }
    
    suspend fun updateProfileField(field: String, value: Any) {
        profileData[field] = value
    }
    
    suspend fun saveSocialAuthData(socialId: String, socialType: String) {
        profileData["social_id"] = socialId
        profileData["social_type"] = socialType
    }
    
    suspend fun getSocialAuthData(): Pair<String?, String?> {
        return Pair(
            profileData["social_id"] as? String,
            profileData["social_type"] as? String
        )
    }
    
    suspend fun updateThemeMode(themeMode: String) {
        profileData["theme_mode"] = themeMode
    }
    
    suspend fun getThemeMode(): String {
        return profileData["theme_mode"] as? String ?: "system"
    }
    
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        profileData["notifications_enabled"] = enabled
    }
    
    suspend fun areNotificationsEnabled(): Boolean {
        return profileData["notifications_enabled"] as? Boolean ?: true
    }
    
    suspend fun updateLocationEnabled(enabled: Boolean) {
        profileData["location_enabled"] = enabled
    }
    
    suspend fun isLocationEnabled(): Boolean {
        return profileData["location_enabled"] as? Boolean ?: false
    }
    
    suspend fun setOnboardingCompleted() {
        profileData["onboarding_completed"] = true
    }
    
    suspend fun hasCompletedOnboarding(): Boolean {
        return profileData["onboarding_completed"] as? Boolean ?: false
    }
    
    suspend fun getLastLoginTimestamp(): Long {
        return profileData["last_login_timestamp"] as? Long ?: 0L
    }
    
    suspend fun getAllPreferences(): Map<String, Any> {
        return profileData.toMap()
    }
    
    suspend fun clearAllData() {
        currentUser = null
        authToken = null
        isLoggedIn = false
        isProfileComplete = false
        profileData.clear()
    }
}