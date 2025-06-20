package com.rcdnc.cafezinho.features.chat.domain.repository

import com.rcdnc.cafezinho.features.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(chatId: String): Flow<Result<List<Message>>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun markMessageAsRead(messageId: String): Result<Unit>
}