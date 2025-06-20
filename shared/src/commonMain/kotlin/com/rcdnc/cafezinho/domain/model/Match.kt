package com.rcdnc.cafezinho.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val timestamp: Long,
    val isSuperLike: Boolean = false,
    val messages: List<Message> = emptyList(),
    val lastActivityTimestamp: Long? = null
)

@Serializable
data class Message(
    val id: String,
    val matchId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val messageType: MessageType = MessageType.TEXT
)

@Serializable
enum class MessageType {
    TEXT, IMAGE, GIF, STICKER
}

@Serializable
data class SwipeAction(
    val userId: String,
    val targetUserId: String,
    val action: SwipeType,
    val timestamp: Long,
    val isSuperLike: Boolean = false
)

@Serializable
enum class SwipeType {
    LIKE, PASS, SUPER_LIKE
}