package com.rcdnc.cafezinho.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de preferências do app usando DataStore
 */
@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cafezinho_preferences")
    
    companion object {
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }
    
    /**
     * Verifica se o usuário já viu o onboarding
     */
    fun hasSeenOnboarding(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[HAS_SEEN_ONBOARDING] ?: false
        }
    }
    
    /**
     * Marca que o usuário viu o onboarding
     */
    suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING] = seen
        }
    }
    
    /**
     * Verifica se é o primeiro launch do app
     */
    fun isFirstLaunch(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }
    }
    
    /**
     * Marca que não é mais o primeiro launch
     */
    suspend fun setNotFirstLaunch() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }
}