package com.rcdnc.cafezinho.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para integração de notificações com Laravel API
 */

/**
 * DTO para registro de token FCM
 */
data class NotificationTokenDto(
    @SerializedName("fcm_token")
    val fcmToken: String,
    
    @SerializedName("device_type")
    val deviceType: String = "android",
    
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("app_version")
    val appVersion: String
)

/**
 * DTO para envio de notificação
 */
data class SendNotificationDto(
    @SerializedName("recipient_id")
    val recipientId: String,
    
    @SerializedName("type")
    val type: NotificationType,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: Map<String, String> = emptyMap(),
    
    @SerializedName("sender_id")
    val senderId: String? = null
)

/**
 * Tipos de notificação suportados
 */
enum class NotificationType(val value: String) {
    @SerializedName("match")
    MATCH("match"),
    
    @SerializedName("message")
    MESSAGE("message"),
    
    @SerializedName("like")
    LIKE("like"),
    
    @SerializedName("super_like")
    SUPER_LIKE("super_like"),
    
    @SerializedName("general")
    GENERAL("general"),
    
    @SerializedName("promo")
    PROMO("promo")
}

/**
 * DTO para configurações de notificação
 */
data class NotificationSettingsDto(
    @SerializedName("matches_enabled")
    val matchesEnabled: Boolean = true,
    
    @SerializedName("messages_enabled")
    val messagesEnabled: Boolean = true,
    
    @SerializedName("likes_enabled")
    val likesEnabled: Boolean = true,
    
    @SerializedName("super_likes_enabled")
    val superLikesEnabled: Boolean = true,
    
    @SerializedName("promotions_enabled")
    val promotionsEnabled: Boolean = false,
    
    @SerializedName("sound_enabled")
    val soundEnabled: Boolean = true,
    
    @SerializedName("vibration_enabled")
    val vibrationEnabled: Boolean = true
)

/**
 * DTO para resposta de histórico de notificações
 */
data class NotificationHistoryDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: Map<String, String>?,
    
    @SerializedName("read_at")
    val readAt: String?,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("sender")
    val sender: NotificationSenderDto?
)

/**
 * DTO para dados do remetente da notificação
 */
data class NotificationSenderDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("photo_url")
    val photoUrl: String?
)