package com.rcdnc.cafezinho.data.api

import com.rcdnc.cafezinho.data.dto.NotificationTokenDto
import com.rcdnc.cafezinho.data.dto.SendNotificationDto
import retrofit2.Response
import retrofit2.http.*

/**
 * API service para integração de notificações com Laravel
 * Endpoints para registro de tokens FCM e envio de notificações
 */
interface NotificationApiService {
    
    /**
     * Registra token FCM do usuário no servidor Laravel
     */
    @POST("user/fcm-token")
    suspend fun registerFCMToken(
        @Body tokenData: NotificationTokenDto
    ): Response<Unit>
    
    /**
     * Remove token FCM do usuário (logout)
     */
    @DELETE("user/fcm-token")
    suspend fun removeFCMToken(): Response<Unit>
    
    /**
     * Atualiza configurações de notificação do usuário
     */
    @PUT("user/notification-settings")
    suspend fun updateNotificationSettings(
        @Body settings: Map<String, Boolean>
    ): Response<Unit>
    
    /**
     * Busca configurações de notificação do usuário
     */
    @GET("user/notification-settings")
    suspend fun getNotificationSettings(): Response<Map<String, Boolean>>
    
    /**
     * Envia notificação para outro usuário (matches, likes)
     * Usado internamente pelo app para notificar ações
     */
    @POST("notification/send")
    suspend fun sendNotification(
        @Body notification: SendNotificationDto
    ): Response<Unit>
    
    /**
     * Marca notificação como lida
     */
    @POST("notification/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: String
    ): Response<Unit>
    
    /**
     * Busca histórico de notificações do usuário
     */
    @GET("user/notifications")
    suspend fun getNotificationHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<Map<String, Any>>>
    
    /**
     * Testa envio de notificação (desenvolvimento)
     */
    @POST("notification/test")
    suspend fun sendTestNotification(
        @Body testData: Map<String, String>
    ): Response<Unit>
}