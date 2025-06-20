package com.rcdnc.cafezinho.features.chat.domain.usecase

import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<Result<List<Message>>> {
        return if (chatId.isBlank()) {
            throw IllegalArgumentException("Chat ID cannot be empty")
        } else {
            chatRepository.getMessages(chatId)
        }
    }
}