package com.rcdnc.cafezinho.features.chat.mvi

import com.rcdnc.cafezinho.features.chat.domain.model.Message

sealed class ChatState {
    object Loading : ChatState()
    data class Success(val messages: List<Message>) : ChatState()
    data class Error(val message: String) : ChatState()
}