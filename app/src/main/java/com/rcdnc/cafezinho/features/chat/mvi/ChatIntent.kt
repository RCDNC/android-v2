package com.rcdnc.cafezinho.features.chat.mvi

sealed class ChatIntent {
    data class LoadMessages(val matchId: String) : ChatIntent()
    data class SendMessage(val matchId: String, val content: String) : ChatIntent()
    data class MarkAsRead(val messageId: String) : ChatIntent()
}