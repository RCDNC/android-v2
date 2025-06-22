package com.rcdnc.cafezinho.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.chat.domain.model.ChatConversation
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para o Chat usando MVI pattern
 * Migração de ChatViewModel.kt do projeto legacy
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<ChatState>(ChatState.Idle)
    val state: StateFlow<ChatState> = _state.asStateFlow()
    
    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()
    
    private val _currentChatMessages = MutableStateFlow<List<Message>>(emptyList())
    val currentChatMessages: StateFlow<List<Message>> = _currentChatMessages.asStateFlow()
    
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    private var currentChatId: String? = null
    private var currentUserId: String = "me" // TODO: Get from auth service
    
    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadConversations -> loadConversations()
            is ChatIntent.LoadChatMessages -> loadChatMessages(intent.chatId)
            is ChatIntent.SendMessage -> sendMessage(intent.chatId, intent.content, intent.type)
            is ChatIntent.MarkAsRead -> markMessageAsRead(intent.messageId)
            is ChatIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is ChatIntent.StartTyping -> setTypingStatus(intent.chatId, true)
            is ChatIntent.StopTyping -> setTypingStatus(intent.chatId, false)
            is ChatIntent.BlockUser -> blockUser(intent.userId)
            is ChatIntent.UnblockUser -> unblockUser(intent.userId)
        }
    }
    
    private fun loadConversations() {
        _state.value = ChatState.Loading
        
        viewModelScope.launch {
            chatRepository.getConversations(currentUserId)
                .catch { exception ->
                    _state.value = ChatState.Error(exception.message ?: "Erro ao carregar conversas")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { conversations ->
                            _conversations.value = conversations
                            _state.value = ChatState.ConversationsLoaded(conversations)
                        },
                        onFailure = { exception ->
                            _state.value = ChatState.Error(exception.message ?: "Erro ao carregar conversas")
                        }
                    )
                }
        }
    }
    
    private fun loadChatMessages(chatId: String) {
        currentChatId = chatId
        _state.value = ChatState.Loading
        
        viewModelScope.launch {
            chatRepository.getMessages(chatId)
                .catch { exception ->
                    _state.value = ChatState.Error(exception.message ?: "Erro ao carregar mensagens")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { messages ->
                            _currentChatMessages.value = messages
                            _state.value = ChatState.MessagesLoaded(messages)
                            
                            // Marcar mensagens como lidas automaticamente
                            markUnreadMessagesAsRead(messages)
                        },
                        onFailure = { exception ->
                            _state.value = ChatState.Error(exception.message ?: "Erro ao carregar mensagens")
                        }
                    )
                }
        }
    }
    
    private fun sendMessage(chatId: String, content: String, type: MessageType = MessageType.TEXT) {
        if (content.isBlank() && type == MessageType.TEXT) return
        
        val message = Message(
            id = generateMessageId(),
            senderId = currentUserId,
            receiverId = getOtherUserId(chatId),
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            status = MessageStatus.SENDING
        )
        
        // Adiciona mensagem localmente primeiro (otimistic update)
        val currentMessages = _currentChatMessages.value.toMutableList()
        currentMessages.add(message)
        _currentChatMessages.value = currentMessages
        
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, message)
                .fold(
                    onSuccess = {
                        // Atualiza status da mensagem para enviada
                        updateMessageStatus(message.id, MessageStatus.SENT)
                        _state.value = ChatState.MessageSent
                    },
                    onFailure = { exception ->
                        // Atualiza status da mensagem para falha
                        updateMessageStatus(message.id, MessageStatus.FAILED)
                        _state.value = ChatState.Error(exception.message ?: "Erro ao enviar mensagem")
                    }
                )
        }
    }
    
    private fun markMessageAsRead(messageId: String) {
        viewModelScope.launch {
            chatRepository.markMessageAsRead(messageId)
                .fold(
                    onSuccess = {
                        updateMessageStatus(messageId, MessageStatus.READ)
                    },
                    onFailure = { 
                        // Silently fail for read receipts
                    }
                )
        }
    }
    
    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
                .fold(
                    onSuccess = {
                        val updatedMessages = _currentChatMessages.value.filter { it.id != messageId }
                        _currentChatMessages.value = updatedMessages
                        _state.value = ChatState.MessageDeleted
                    },
                    onFailure = { exception ->
                        _state.value = ChatState.Error(exception.message ?: "Erro ao deletar mensagem")
                    }
                )
        }
    }
    
    private fun setTypingStatus(chatId: String, isTyping: Boolean) {
        this._isTyping.value = isTyping
        
        viewModelScope.launch {
            if (isTyping) {
                chatRepository.startTyping(chatId, currentUserId)
            } else {
                chatRepository.stopTyping(chatId, currentUserId)
            }
        }
    }
    
    private fun blockUser(userId: String) {
        viewModelScope.launch {
            chatRepository.blockUser(currentUserId, userId)
                .fold(
                    onSuccess = {
                        _state.value = ChatState.UserBlocked
                    },
                    onFailure = { exception ->
                        _state.value = ChatState.Error(exception.message ?: "Erro ao bloquear usuário")
                    }
                )
        }
    }
    
    private fun unblockUser(userId: String) {
        viewModelScope.launch {
            chatRepository.unblockUser(currentUserId, userId)
                .fold(
                    onSuccess = {
                        _state.value = ChatState.UserUnblocked
                    },
                    onFailure = { exception ->
                        _state.value = ChatState.Error(exception.message ?: "Erro ao desbloquear usuário")
                    }
                )
        }
    }
    
    // Helper functions
    private fun markUnreadMessagesAsRead(messages: List<Message>) {
        val unreadMessages = messages.filter { 
            it.receiverId == currentUserId && !it.isRead 
        }
        
        unreadMessages.forEach { message ->
            markMessageAsRead(message.id)
        }
    }
    
    private fun updateMessageStatus(messageId: String, newStatus: MessageStatus) {
        val updatedMessages = _currentChatMessages.value.map { message ->
            if (message.id == messageId) {
                message.copy(
                    status = newStatus,
                    isRead = newStatus == MessageStatus.READ
                )
            } else {
                message
            }
        }
        _currentChatMessages.value = updatedMessages
    }
    
    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(0..999).random()}"
    }
    
    private fun getOtherUserId(chatId: String): String {
        // Extract other user ID from chat ID format: "user1-user2"
        val userIds = chatId.split("-")
        return userIds.find { it != currentUserId } ?: ""
    }
    
    // Public helpers for UI
    fun getCurrentUserId(): String = currentUserId
    
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
    
    fun clearError() {
        _state.value = ChatState.Idle
    }
}

/**
 * Estados do Chat (MVI pattern)
 */
sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class ConversationsLoaded(val conversations: List<ChatConversation>) : ChatState()
    data class MessagesLoaded(val messages: List<Message>) : ChatState()
    object MessageSent : ChatState()
    object MessageDeleted : ChatState()
    object UserBlocked : ChatState()
    object UserUnblocked : ChatState()
    data class Error(val message: String) : ChatState()
}

/**
 * Intenções do usuário (MVI pattern)
 */
sealed class ChatIntent {
    object LoadConversations : ChatIntent()
    data class LoadChatMessages(val chatId: String) : ChatIntent()
    data class SendMessage(val chatId: String, val content: String, val type: MessageType = MessageType.TEXT) : ChatIntent()
    data class MarkAsRead(val messageId: String) : ChatIntent()
    data class DeleteMessage(val messageId: String) : ChatIntent()
    data class StartTyping(val chatId: String) : ChatIntent()
    data class StopTyping(val chatId: String) : ChatIntent()
    data class BlockUser(val userId: String) : ChatIntent()
    data class UnblockUser(val userId: String) : ChatIntent()
}