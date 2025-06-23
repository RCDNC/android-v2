package com.rcdnc.cafezinho.core.notifications

/*
 * FIREBASE TEMPORARIAMENTE DESABILITADO
 * Firebase está comentado no build.gradle para testes
 * Esta classe será reativada quando Firebase for habilitado
 */

/*
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.data.repository.NotificationRepositoryImpl
// import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// import javax.inject.Inject

/**
 * Serviço FCM para receber push notifications
 * Integração com Laravel API para registro de tokens
 * 
 * TEMPORARIAMENTE DESABILITADO - Firebase comentado no build.gradle
 */
// @AndroidEntryPoint
class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    
    // @Inject
    lateinit var notificationManager: NotificationManager
    
    // @Inject
    lateinit var authManager: AuthManager
    
    // @Inject
    lateinit var notificationRepository: NotificationRepositoryImpl
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "FCMService"
    }
    
    /**
     * Chamado quando um novo token FCM é gerado
     * Envia token para o Laravel API
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        serviceScope.launch {
            try {
                if (authManager.isAuthenticated()) {
                    notificationRepository.registerFCMToken(token)
                    Log.d(TAG, "FCM token registered successfully")
                } else {
                    Log.d(TAG, "User not authenticated, token will be registered on login")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token", e)
            }
        }
    }
    
    /**
     * Chamado quando uma push notification é recebida
     * Processa diferentes tipos de notificação
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Processa data payload (sempre presente)
        remoteMessage.data.let { data ->
            Log.d(TAG, "Message data: $data")
            handleDataMessage(data)
        }
        
        // Processa notification payload (opcional)
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message notification: ${notification.title} - ${notification.body}")
            
            // Se app está em foreground, usa data payload
            // Se app está em background, o sistema mostra a notificação automaticamente
        }
    }
    
    /**
     * Processa mensagem com dados personalizados
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        
        when (type) {
            "match" -> handleMatchNotification(data)
            "message" -> handleMessageNotification(data)
            "like" -> handleLikeNotification(data)
            "super_like" -> handleSuperLikeNotification(data)
            "general" -> handleGeneralNotification(data)
            else -> {
                Log.w(TAG, "Unknown notification type: $type")
            }
        }
    }
    
    /**
     * Processa notificação de match
     */
    private fun handleMatchNotification(data: Map<String, String>) {
        val matchId = data["match_id"] ?: return
        val userName = data["user_name"] ?: "Alguém"
        val userPhoto = data["user_photo"]
        
        notificationManager.showMatchNotification(
            matchId = matchId,
            userName = userName,
            userPhotoUrl = userPhoto
        )
        
        Log.d(TAG, "Match notification shown for: $userName")
    }
    
    /**
     * Processa notificação de mensagem
     */
    private fun handleMessageNotification(data: Map<String, String>) {
        val chatId = data["chat_id"] ?: return
        val senderName = data["sender_name"] ?: "Alguém"
        val messageText = data["message_text"] ?: "Nova mensagem"
        val senderPhoto = data["sender_photo"]
        
        notificationManager.showMessageNotification(
            chatId = chatId,
            senderName = senderName,
            messageText = messageText,
            senderPhotoUrl = senderPhoto
        )
        
        Log.d(TAG, "Message notification shown from: $senderName")
    }
    
    /**
     * Processa notificação de like
     */
    private fun handleLikeNotification(data: Map<String, String>) {
        val userId = data["user_id"] ?: return
        val userName = data["user_name"] ?: "Alguém"
        
        notificationManager.showLikeNotification(
            userId = userId,
            userName = userName,
            isSuper = false
        )
        
        Log.d(TAG, "Like notification shown from: $userName")
    }
    
    /**
     * Processa notificação de super like
     */
    private fun handleSuperLikeNotification(data: Map<String, String>) {
        val userId = data["user_id"] ?: return
        val userName = data["user_name"] ?: "Alguém"
        
        notificationManager.showLikeNotification(
            userId = userId,
            userName = userName,
            isSuper = true
        )
        
        Log.d(TAG, "Super like notification shown from: $userName")
    }
    
    /**
     * Processa notificação geral
     */
    private fun handleGeneralNotification(data: Map<String, String>) {
        val title = data["title"] ?: "Cafezinho"
        val message = data["message"] ?: "Você tem uma nova notificação"
        
        val actionData = data.filterKeys { 
            it !in listOf("type", "title", "message") 
        }
        
        notificationManager.showGeneralNotification(
            title = title,
            message = message,
            actionData = actionData.ifEmpty { null }
        )
        
        Log.d(TAG, "General notification shown: $title")
    }
    
    /**
     * Chamado quando mensagem é deletada no servidor
     */
    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "Messages deleted on server")
        
        // Implementar lógica para sincronizar mensagens
        serviceScope.launch {
            try {
                // TODO: Sincronizar com servidor
                Log.d(TAG, "Message synchronization completed")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to synchronize messages", e)
            }
        }
    }
    
    /**
     * Chamado quando falha ao enviar mensagem upstream
     */
    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d(TAG, "Message sent successfully: $msgId")
    }
    
    /**
     * Chamado quando falha ao enviar mensagem upstream
     */
    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, "Failed to send message: $msgId", exception)
    }
}
*/