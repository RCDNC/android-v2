package com.rcdnc.cafezinho.features.chat.data.local

import com.rcdnc.cafezinho.features.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache local para mensagens
 * Necessário porque a API Laravel não tem endpoints completos para mensagens
 */
@Singleton
class ChatLocalDataSource @Inject constructor() {
    
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    private val messages = _messages.asStateFlow()
    
    private val _typingUsers = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    private val typingUsers = _typingUsers.asStateFlow()
    
    /**
     * Obter mensagens de um chat específico
     */
    fun getMessages(chatId: String): Flow<List<Message>> {
        return messages.map { it[chatId] ?: emptyList() }
    }
    
    /**
     * Adicionar mensagem ao cache local
     */
    suspend fun addMessage(chatId: String, message: Message) {
        val currentMessages = _messages.value[chatId]?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
        
        _messages.value = _messages.value.toMutableMap().apply {
            this[chatId] = currentMessages.sortedBy { it.timestamp }
        }
    }
    
    /**
     * Atualizar status de uma mensagem
     */
    suspend fun updateMessageStatus(messageId: String, newStatus: com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus) {
        val updatedMessages = _messages.value.toMutableMap()
        
        updatedMessages.forEach { (chatId, messagesList) ->
            val updatedList = messagesList.map { message ->
                if (message.id == messageId) {
                    message.copy(
                        status = newStatus,
                        isRead = newStatus == com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus.READ
                    )
                } else {
                    message
                }
            }
            updatedMessages[chatId] = updatedList
        }
        
        _messages.value = updatedMessages
    }
    
    /**
     * Deletar mensagem do cache
     */
    suspend fun deleteMessage(messageId: String) {
        val updatedMessages = _messages.value.toMutableMap()
        
        updatedMessages.forEach { (chatId, messagesList) ->
            val updatedList = messagesList.filter { it.id != messageId }
            updatedMessages[chatId] = updatedList
        }
        
        _messages.value = updatedMessages
    }
    
    /**
     * Limpar mensagens de um chat
     */
    suspend fun clearChatMessages(chatId: String) {
        _messages.value = _messages.value.toMutableMap().apply {
            remove(chatId)
        }
    }
    
    /**
     * Obter última mensagem de um chat
     */
    fun getLastMessage(chatId: String): Message? {
        return _messages.value[chatId]?.lastOrNull()
    }
    
    /**
     * Contar mensagens não lidas de um chat
     */
    fun getUnreadCount(chatId: String, currentUserId: String): Int {
        return _messages.value[chatId]?.count { message ->
            message.receiverId == currentUserId && !message.isRead
        } ?: 0
    }
    
    /**
     * Marcar todas as mensagens de um chat como lidas
     */
    suspend fun markAllAsRead(chatId: String, currentUserId: String) {
        val currentMessages = _messages.value[chatId] ?: return
        
        val updatedMessages = currentMessages.map { message ->
            if (message.receiverId == currentUserId && !message.isRead) {
                message.copy(
                    isRead = true,
                    status = com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus.READ
                )
            } else {
                message
            }
        }
        
        _messages.value = _messages.value.toMutableMap().apply {
            this[chatId] = updatedMessages
        }
    }
    
    // TYPING INDICATORS
    
    /**
     * Definir usuário como digitando
     */
    suspend fun setUserTyping(chatId: String, userId: String, isTyping: Boolean) {
        val currentTyping = _typingUsers.value[chatId]?.toMutableSet() ?: mutableSetOf()
        
        if (isTyping) {
            currentTyping.add(userId)
        } else {
            currentTyping.remove(userId)
        }
        
        _typingUsers.value = _typingUsers.value.toMutableMap().apply {
            this[chatId] = currentTyping
        }
    }
    
    /**
     * Verificar se usuário está digitando
     */
    fun isUserTyping(chatId: String, userId: String): Flow<Boolean> {
        return typingUsers.map { it[chatId]?.contains(userId) == true }
    }
    
    /**
     * Obter todos os usuários digitando em um chat
     */
    fun getTypingUsers(chatId: String): Flow<Set<String>> {
        return typingUsers.map { it[chatId] ?: emptySet() }
    }
}