package com.rcdnc.cafezinho.features.chat.domain.model

enum class MessageType {
    TEXT, IMAGE, AUDIO, FILE
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENT,
    val senderAvatar: String? = null
)