package com.rcdnc.cafezinho.features.chat.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val messageType: String = "text"
)