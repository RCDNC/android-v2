package com.rcdnc.cafezinho.features.chat.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import com.rcdnc.cafezinho.features.chat.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var currentChatId: String = ""
    private var currentUserId: String = ""

    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadMessages -> {
                loadMessages(intent.matchId)
            }
            is ChatIntent.SendMessage -> {
                sendMessage(intent.matchId, intent.content)
            }
            is ChatIntent.MarkAsRead -> {
                markMessageAsRead(intent.messageId)
            }
        }
    }

    private fun loadMessages(matchId: String) {
        currentChatId = matchId
        _state.value = ChatState.Loading
        
        viewModelScope.launch {
            chatRepository.getMessages(matchId).collect { result ->
                result.fold(
                    onSuccess = { messages ->
                        _state.value = ChatState.Success(messages)
                    },
                    onFailure = { error ->
                        _state.value = ChatState.Error(error.message ?: "Failed to load messages")
                    }
                )
            }
        }
    }

    private fun sendMessage(matchId: String, content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            // TODO: Get current user ID and receiver ID from proper source
            val result = sendMessageUseCase(
                chatId = matchId,
                content = content,
                senderId = currentUserId, // Should be injected from user session
                receiverId = "" // Should be derived from match data
            )
            
            result.fold(
                onSuccess = {
                    // Message sent successfully
                    // The messages will be updated through the Flow in loadMessages
                },
                onFailure = { error ->
                    _state.value = ChatState.Error(error.message ?: "Failed to send message")
                }
            )
        }
    }

    private fun markMessageAsRead(messageId: String) {
        viewModelScope.launch {
            chatRepository.markMessageAsRead(messageId)
        }
    }

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
}