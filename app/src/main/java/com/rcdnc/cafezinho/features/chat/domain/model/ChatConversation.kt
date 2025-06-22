package com.rcdnc.cafezinho.features.chat.domain.model

/**
 * Modelo de conversa para a lista de chats
 * Migração de InboxModel.kt do projeto legacy
 */
data class ChatConversation(
    val id: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserAvatar: String?,
    val lastMessage: Message?,
    val lastMessageTimestamp: Long,
    val hasUnreadMessages: Boolean = false,
    val unreadCount: Int = 0,
    val isYourTurn: Boolean = false,
    val isPremium: Boolean = false,
    val isOnline: Boolean = false,
    val isMatch: Boolean = true
)