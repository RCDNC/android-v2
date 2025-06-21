package com.rcdnc.cafezinho.features.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.rcdnc.cafezinho.features.auth.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore implementation for user session management
 * Replaces SharedPreferences for modern, type-safe storage
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
        
        // Auth Keys
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        
        // User Profile Keys
        private val FIRST_NAME = stringPreferencesKey("first_name")
        private val LAST_NAME = stringPreferencesKey("last_name")
        private val EMAIL = stringPreferencesKey("email")
        private val PHONE_NUMBER = stringPreferencesKey("phone_number")
        private val IS_VERIFIED = booleanPreferencesKey("is_verified")
        private val IS_PROFILE_COMPLETE = booleanPreferencesKey("is_profile_complete")
        
        // Social Auth Keys
        private val SOCIAL_ID = stringPreferencesKey("social_id")
        private val SOCIAL_TYPE = stringPreferencesKey("social_type")
        
        // Profile Data Keys
        private val DATE_OF_BIRTH = longPreferencesKey("date_of_birth")
        private val GENDER = stringPreferencesKey("gender")
        private val GENDER_PREFERENCE = stringPreferencesKey("gender_preference")
        private val BIO = stringPreferencesKey("bio")
        private val SCHOOL = stringPreferencesKey("school")
        private val PROFILE_PHOTO_URL = stringPreferencesKey("profile_photo_url")
        
        // App Settings Keys
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val LOCATION_ENABLED = booleanPreferencesKey("location_enabled")
        
        // Onboarding Keys
        private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        private val LAST_LOGIN_TIMESTAMP = longPreferencesKey("last_login_timestamp")
    }

    private val dataStore = context.dataStore

    // Session Management
    suspend fun saveUserSession(user: User, authToken: String? = null) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = user.id
            preferences[FIRST_NAME] = user.firstName ?: ""
            preferences[EMAIL] = user.email ?: ""
            preferences[PHONE_NUMBER] = user.phone ?: ""
            preferences[IS_VERIFIED] = user.isVerified
            preferences[LAST_LOGIN_TIMESTAMP] = System.currentTimeMillis()
            
            authToken?.let { token ->
                preferences[AUTH_TOKEN] = token
            }
        }
    }

    suspend fun updateAuthTokens(authToken: String, refreshToken: String? = null) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = authToken
            refreshToken?.let { refresh ->
                preferences[REFRESH_TOKEN] = refresh
            }
        }
    }

    suspend fun getUserSession(): User? {
        return dataStore.data.map { prefs ->
            val isLoggedIn = prefs[IS_LOGGED_IN] ?: false
            if (!isLoggedIn) return@map null
            
            User(
                id = prefs[USER_ID] ?: return@map null,
                firstName = prefs[FIRST_NAME],
                email = prefs[EMAIL],
                phone = prefs[PHONE_NUMBER],
                isVerified = prefs[IS_VERIFIED] ?: false,
                token = prefs[AUTH_TOKEN]
            )
        }.first()
    }

    fun getUserSessionFlow(): Flow<User?> {
        return dataStore.data.map { prefs ->
            val isLoggedIn = prefs[IS_LOGGED_IN] ?: false
            if (!isLoggedIn) return@map null
            
            User(
                id = prefs[USER_ID] ?: return@map null,
                firstName = prefs[FIRST_NAME],
                email = prefs[EMAIL],
                phone = prefs[PHONE_NUMBER],
                isVerified = prefs[IS_VERIFIED] ?: false,
                token = prefs[AUTH_TOKEN]
            )
        }
    }

    suspend fun getAuthToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[AUTH_TOKEN]
        }.first()
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }.first()
    }

    fun isLoggedInFlow(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            // Clear auth data
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(USER_ID)
            preferences.remove(AUTH_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            
            // Clear user profile data
            preferences.remove(FIRST_NAME)
            preferences.remove(LAST_NAME)
            preferences.remove(EMAIL)
            preferences.remove(PHONE_NUMBER)
            preferences.remove(IS_VERIFIED)
            preferences.remove(IS_PROFILE_COMPLETE)
            
            // Clear social auth data
            preferences.remove(SOCIAL_ID)
            preferences.remove(SOCIAL_TYPE)
            
            // Clear profile data
            preferences.remove(DATE_OF_BIRTH)
            preferences.remove(GENDER)
            preferences.remove(GENDER_PREFERENCE)
            preferences.remove(BIO)
            preferences.remove(SCHOOL)
            preferences.remove(PROFILE_PHOTO_URL)
            
            // Keep app settings and onboarding state
        }
    }

    // Profile Management
    suspend fun updateProfileCompletion(isComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PROFILE_COMPLETE] = isComplete
        }
    }

    suspend fun isProfileComplete(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[IS_PROFILE_COMPLETE] ?: false
        }.first()
    }

    suspend fun updateProfileField(field: String, value: Any) {
        dataStore.edit { preferences ->
            when (field) {
                "first_name" -> preferences[FIRST_NAME] = value as String
                "last_name" -> preferences[LAST_NAME] = value as String
                "email" -> preferences[EMAIL] = value as String
                "phone" -> preferences[PHONE_NUMBER] = value as String
                "bio" -> preferences[BIO] = value as String
                "school" -> preferences[SCHOOL] = value as String
                "gender" -> preferences[GENDER] = value as String
                "gender_preference" -> preferences[GENDER_PREFERENCE] = value as String
                "date_of_birth" -> preferences[DATE_OF_BIRTH] = value as Long
                "profile_photo_url" -> preferences[PROFILE_PHOTO_URL] = value as String
                "is_verified" -> preferences[IS_VERIFIED] = value as Boolean
            }
        }
    }

    // Social Auth Management
    suspend fun saveSocialAuthData(socialId: String, socialType: String) {
        dataStore.edit { preferences ->
            preferences[SOCIAL_ID] = socialId
            preferences[SOCIAL_TYPE] = socialType
        }
    }

    suspend fun getSocialAuthData(): Pair<String?, String?> {
        return dataStore.data.map { prefs ->
            Pair(prefs[SOCIAL_ID], prefs[SOCIAL_TYPE])
        }.first()
    }

    // App Settings
    suspend fun updateThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode
        }
    }

    suspend fun getThemeMode(): String {
        return dataStore.data.map { prefs ->
            prefs[THEME_MODE] ?: "system"
        }.first()
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun areNotificationsEnabled(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[NOTIFICATIONS_ENABLED] ?: true
        }.first()
    }

    suspend fun updateLocationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOCATION_ENABLED] = enabled
        }
    }

    suspend fun isLocationEnabled(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[LOCATION_ENABLED] ?: false
        }.first()
    }

    // Onboarding
    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = true
        }
    }

    suspend fun hasCompletedOnboarding(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[HAS_COMPLETED_ONBOARDING] ?: false
        }.first()
    }

    // Session Analytics
    suspend fun getLastLoginTimestamp(): Long {
        return dataStore.data.map { prefs ->
            prefs[LAST_LOGIN_TIMESTAMP] ?: 0L
        }.first()
    }

    // Backup/Restore Methods (for migration from SharedPreferences)
    suspend fun getAllPreferences(): Map<String, Any> {
        return dataStore.data.map { prefs ->
            prefs.asMap().mapKeys { it.key.name }
        }.first()
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}