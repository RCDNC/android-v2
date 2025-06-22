package com.rcdnc.cafezinho.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rcdnc.cafezinho.MainActivity
import com.rcdnc.cafezinho.R
import com.rcdnc.cafezinho.core.config.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador central de notificações push
 * Resolve Blocker Crítico #3 - Push Notifications
 */
@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        // Notification Channels
        private const val CHANNEL_MATCHES = "matches"
        private const val CHANNEL_MESSAGES = "messages"  
        private const val CHANNEL_LIKES = "likes"
        private const val CHANNEL_GENERAL = "general"
        
        // Notification IDs
        private const val NOTIFICATION_ID_MATCH = 1001
        private const val NOTIFICATION_ID_MESSAGE = 1002
        private const val NOTIFICATION_ID_LIKE = 1003
        private const val NOTIFICATION_ID_GENERAL = 1004
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Cria canais de notificação (Android 8+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_MATCHES,
                    "Matches",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificações de novos matches"
                    enableVibration(true)
                    enableLights(true)
                },
                
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    "Mensagens",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificações de novas mensagens"
                    enableVibration(true)
                    enableLights(true)
                },
                
                NotificationChannel(
                    CHANNEL_LIKES,
                    "Likes",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notificações de curtidas"
                    enableVibration(false)
                    enableLights(true)
                },
                
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "Geral",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notificações gerais do app"
                }
            )
            
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { channel ->
                systemNotificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Mostra notificação de novo match
     */
    fun showMatchNotification(
        matchId: String,
        userName: String,
        userPhotoUrl: String? = null
    ) {
        val intent = createDeepLinkIntent("match", mapOf("matchId" to matchId))
        val pendingIntent = createPendingIntent(intent, matchId.hashCode())
        
        val notification = NotificationCompat.Builder(context, CHANNEL_MATCHES)
            .setContentTitle("🎉 Novo Match!")
            .setContentText("Você e $userName curtiram um ao outro!")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
            
        notificationManager.notify(NOTIFICATION_ID_MATCH + matchId.hashCode(), notification)
    }
    
    /**
     * Mostra notificação de nova mensagem
     */
    fun showMessageNotification(
        chatId: String,
        senderName: String,
        messageText: String,
        senderPhotoUrl: String? = null
    ) {
        val intent = createDeepLinkIntent("chat", mapOf("chatId" to chatId))
        val pendingIntent = createPendingIntent(intent, chatId.hashCode())
        
        val notification = NotificationCompat.Builder(context, CHANNEL_MESSAGES)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .build()
            
        notificationManager.notify(NOTIFICATION_ID_MESSAGE + chatId.hashCode(), notification)
    }
    
    /**
     * Mostra notificação de like
     */
    fun showLikeNotification(
        userId: String,
        userName: String,
        isSuper: Boolean = false
    ) {
        val intent = createDeepLinkIntent("profile", mapOf("userId" to userId))
        val pendingIntent = createPendingIntent(intent, userId.hashCode())
        
        val title = if (isSuper) "💙 Super Like!" else "❤️ Curtida!"
        val text = if (isSuper) "$userName te deu um super like!" else "$userName curtiu você!"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_LIKES)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
            
        notificationManager.notify(NOTIFICATION_ID_LIKE + userId.hashCode(), notification)
    }
    
    /**
     * Mostra notificação geral
     */
    fun showGeneralNotification(
        title: String,
        message: String,
        actionData: Map<String, String>? = null
    ) {
        val intent = if (actionData != null) {
            createDeepLinkIntent("general", actionData)
        } else {
            Intent(context, MainActivity::class.java)
        }
        val pendingIntent = createPendingIntent(intent, title.hashCode())
        
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
            
        notificationManager.notify(NOTIFICATION_ID_GENERAL + title.hashCode(), notification)
    }
    
    /**
     * Cria intent com deep link para navegação
     */
    private fun createDeepLinkIntent(type: String, data: Map<String, String>): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", type)
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
    }
    
    /**
     * Cria PendingIntent para notificação
     */
    private fun createPendingIntent(intent: Intent, requestCode: Int): PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
    
    /**
     * Limpa todas as notificações
     */
    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }
    
    /**
     * Limpa notificação específica por tipo e ID
     */
    fun clearNotification(type: String, id: String) {
        val notificationId = when(type) {
            "match" -> NOTIFICATION_ID_MATCH + id.hashCode()
            "message" -> NOTIFICATION_ID_MESSAGE + id.hashCode()
            "like" -> NOTIFICATION_ID_LIKE + id.hashCode()
            else -> NOTIFICATION_ID_GENERAL + id.hashCode()
        }
        notificationManager.cancel(notificationId)
    }
    
    /**
     * Verifica se as notificações estão habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}