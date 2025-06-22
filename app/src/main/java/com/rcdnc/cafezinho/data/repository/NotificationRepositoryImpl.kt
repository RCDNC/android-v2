package com.rcdnc.cafezinho.data.repository

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.rcdnc.cafezinho.BuildConfig
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.data.api.NotificationApiService
import com.rcdnc.cafezinho.data.dto.NotificationTokenDto
import com.rcdnc.cafezinho.data.dto.SendNotificationDto
import com.rcdnc.cafezinho.domain.repository.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de notificações
 * Integra FCM com Laravel API
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationApiService: NotificationApiService,
    private val authManager: AuthManager
) : NotificationRepository {
    
    companion object {
        private const val TAG = "NotificationRepo"
    }
    
    /**
     * Obtém token FCM atual e registra no servidor
     */
    override suspend fun registerFCMToken(): Result<Unit> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM token obtained: $token")
            
            val deviceId = getDeviceId()
            val tokenDto = NotificationTokenDto(
                fcmToken = token,
                deviceType = "android",
                deviceId = deviceId,
                appVersion = BuildConfig.VERSION_NAME
            )
            
            val response = notificationApiService.registerFCMToken(tokenDto)
            
            if (response.isSuccessful) {
                Log.d(TAG, "FCM token registered successfully")
                Result.success(Unit)
            } else {
                val error = "Failed to register FCM token: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error registering FCM token", e)
            Result.failure(e)
        }
    }
    
    /**
     * Registra token FCM específico (usado pelo FCM Service)
     */
    suspend fun registerFCMToken(token: String): Result<Unit> {
        return try {
            val deviceId = getDeviceId()
            val tokenDto = NotificationTokenDto(
                fcmToken = token,
                deviceType = "android",
                deviceId = deviceId,
                appVersion = BuildConfig.VERSION_NAME
            )
            
            val response = notificationApiService.registerFCMToken(tokenDto)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Specific FCM token registered successfully")
                Result.success(Unit)
            } else {
                val error = "Failed to register specific FCM token: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error registering specific FCM token", e)
            Result.failure(e)
        }
    }
    
    /**
     * Remove token FCM do servidor (logout)
     */
    override suspend fun removeFCMToken(): Result<Unit> {
        return try {
            val response = notificationApiService.removeFCMToken()
            
            if (response.isSuccessful) {
                Log.d(TAG, "FCM token removed successfully")
                Result.success(Unit)
            } else {
                val error = "Failed to remove FCM token: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error removing FCM token", e)
            Result.failure(e)
        }
    }
    
    /**
     * Envia notificação para outro usuário
     */
    override suspend fun sendNotification(notification: SendNotificationDto): Result<Unit> {
        return try {
            val response = notificationApiService.sendNotification(notification)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Notification sent successfully")
                Result.success(Unit)
            } else {
                val error = "Failed to send notification: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
            Result.failure(e)
        }
    }
    
    /**
     * Atualiza configurações de notificação
     */
    override suspend fun updateNotificationSettings(settings: Map<String, Boolean>): Result<Unit> {
        return try {
            val response = notificationApiService.updateNotificationSettings(settings)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Notification settings updated successfully")
                Result.success(Unit)
            } else {
                val error = "Failed to update notification settings: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification settings", e)
            Result.failure(e)
        }
    }
    
    /**
     * Busca configurações de notificação
     */
    override suspend fun getNotificationSettings(): Result<Map<String, Boolean>> {
        return try {
            val response = notificationApiService.getNotificationSettings()
            
            if (response.isSuccessful) {
                val settings = response.body() ?: emptyMap()
                Log.d(TAG, "Notification settings retrieved successfully")
                Result.success(settings)
            } else {
                val error = "Failed to get notification settings: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting notification settings", e)
            Result.failure(e)
        }
    }
    
    /**
     * Marca notificação como lida
     */
    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            val response = notificationApiService.markNotificationAsRead(notificationId)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Notification marked as read: $notificationId")
                Result.success(Unit)
            } else {
                val error = "Failed to mark notification as read: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read", e)
            Result.failure(e)
        }
    }
    
    /**
     * Obtém ID único do device
     */
    private fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) 
                ?: "unknown_device"
        } catch (e: Exception) {
            Log.w(TAG, "Could not get device ID", e)
            "unknown_device"
        }
    }
    
    /**
     * Verifica se as notificações estão habilitadas no sistema
     */
    override suspend fun areNotificationsEnabled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ requer permissão POST_NOTIFICATIONS
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == 
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                // Android < 13
                androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notification permission", e)
            false
        }
    }
}