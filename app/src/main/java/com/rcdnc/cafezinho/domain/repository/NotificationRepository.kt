package com.rcdnc.cafezinho.domain.repository

import com.rcdnc.cafezinho.data.dto.SendNotificationDto

/**
 * Repositório de domínio para notificações push
 * Interface limpa para FCM e API integration
 */
interface NotificationRepository {
    
    /**
     * Registra token FCM no servidor Laravel
     */
    suspend fun registerFCMToken(): Result<Unit>
    
    /**
     * Remove token FCM do servidor (logout)
     */
    suspend fun removeFCMToken(): Result<Unit>
    
    /**
     * Envia notificação para outro usuário
     */
    suspend fun sendNotification(notification: SendNotificationDto): Result<Unit>
    
    /**
     * Atualiza configurações de notificação do usuário
     */
    suspend fun updateNotificationSettings(settings: Map<String, Boolean>): Result<Unit>
    
    /**
     * Busca configurações de notificação do usuário
     */
    suspend fun getNotificationSettings(): Result<Map<String, Boolean>>
    
    /**
     * Marca notificação como lida
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
    
    /**
     * Verifica se as notificações estão habilitadas no sistema
     */
    suspend fun areNotificationsEnabled(): Boolean
}