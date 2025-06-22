package com.rcdnc.cafezinho.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para integração com API Laravel do Cafezinho
 * Baseado na estrutura real da API analisada
 */

/**
 * Representa um par de conversa da API
 */
data class ChatPairDto(
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("receiver_id")
    val receiverId: Int
)

/**
 * Representa uma mensagem no formato da API Laravel
 * Baseado no modelo Inbox do Laravel
 */
data class InboxMessageDto(
    val id: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("receiver_id")
    val receiverId: Int,
    val message: String,
    val read: Boolean,
    val created: String,
    @SerializedName("lastUpdate")
    val lastUpdate: String,
    @SerializedName("firstMessageFromCafeteria")
    val firstMessageFromCafeteria: Boolean = false
)

/**
 * DTO para atualizar contador de mensagens no match
 */
data class MatchUpdateDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("other_user_id")
    val otherUserId: Int,
    @SerializedName("user_messages_count")
    val userMessagesCount: Int,
    @SerializedName("other_user_messages_count")
    val otherUserMessagesCount: Int
)

/**
 * DTO para pergunta aleatória da cafeteria
 */
data class RandomQuestionDto(
    val id: Int,
    val question: String,
    val category: String? = null
)

/**
 * DTO para dados do usuário (simplificado para chat)
 */
data class UserDto(
    val id: Int,
    val name: String,
    val avatar: String? = null,
    val isPremium: Boolean = false,
    val isOnline: Boolean = false
)

/**
 * DTO para informações completas de uma conversa
 * Combinação de dados de múltiplas APIs
 */
data class ConversationDto(
    val id: String,
    val otherUser: UserDto,
    val lastMessage: InboxMessageDto? = null,
    val unreadCount: Int = 0,
    val matchData: MatchDto? = null
)

/**
 * DTO para match entre usuários
 */
data class MatchDto(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("other_user_id")
    val otherUserId: Int,
    @SerializedName("user_messages_count")
    val userMessagesCount: Int,
    @SerializedName("other_user_messages_count")
    val otherUserMessagesCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * DTO para notificação FCM
 */
data class NotificationDto(
    val title: String,
    val body: String,
    val senderId: Int,
    val receiverId: Int,
    val type: String // "message", "preMatch", etc.
)