package com.rcdnc.cafezinho.core.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de autenticação global
 * Simplificado para implementação rápida dos blockers críticos
 */
@Singleton
class AuthManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }
    
    /**
     * Observa se o usuário está autenticado
     */
    fun isAuthenticatedFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]?.isNotBlank() == true
        }
    }
    
    /**
     * Verifica se está autenticado (sincrono)
     */
    suspend fun isAuthenticated(): Boolean {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY]?.isNotBlank() == true
    }
    
    /**
     * Obtém o token atual
     */
    suspend fun getCurrentToken(): String? {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY]
    }
    
    /**
     * Obtém o ID do usuário atual
     */
    suspend fun getCurrentUserId(): String? {
        return context.dataStore.data.first()[USER_ID_KEY]
    }
    
    /**
     * Obtém dados básicos do usuário
     */
    suspend fun getCurrentUserData(): Triple<String?, String?, String?> {
        val prefs = context.dataStore.data.first()
        return Triple(
            prefs[USER_ID_KEY],
            prefs[USER_EMAIL_KEY],
            prefs[USER_NAME_KEY]
        )
    }
    
    /**
     * Salva dados de autenticação
     */
    suspend fun saveAuthData(
        token: String,
        userId: String,
        email: String,
        name: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
        }
    }
    
    /**
     * Limpa dados de autenticação (logout)
     */
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Demo login para desenvolvimento
     */
    suspend fun demoLogin(email: String, password: String): Boolean {
        // Demo validation
        if (email.isNotBlank() && password.isNotBlank()) {
            saveAuthData(
                token = "demo-token-${System.currentTimeMillis()}",
                userId = "demo-user-${email.hashCode()}",
                email = email,
                name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            )
            return true
        }
        return false
    }
    
    /**
     * Logout compatível
     */
    suspend fun logout() {
        clearAuthData()
    }
    
    /**
     * Formata token para header Authorization
     */
    suspend fun getAuthHeader(): String? {
        val token = getCurrentToken()
        return if (token != null) "Bearer $token" else null
    }
    
    /**
     * Login simples para desenvolvimento/testes
     * TODO: Integrar com API real quando disponível
     */
    suspend fun loginDemo(email: String, password: String): Boolean {
        return try {
            // Demo: aceita qualquer email/senha válidos
            if (email.contains("@") && password.length >= 6) {
                saveAuthData(
                    token = "demo_token_${System.currentTimeMillis()}",
                    userId = "demo_user_123",
                    email = email,
                    name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}