package com.rcdnc.cafezinho.features.chat.domain.repository

import com.rcdnc.cafezinho.features.chat.domain.model.ChatConversation
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversations(userId: String): Flow<Result<List<ChatConversation>>>
    fun getMessages(chatId: String): Flow<Result<List<Message>>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun markMessageAsRead(messageId: String): Result<Unit>
    suspend fun startTyping(chatId: String, userId: String): Result<Unit>
    suspend fun stopTyping(chatId: String, userId: String): Result<Unit>
    suspend fun blockUser(userId: String, blockedUserId: String): Result<Unit>
    suspend fun unblockUser(userId: String, unblockedUserId: String): Result<Unit>
}