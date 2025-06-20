package com.rcdnc.cafezinho.features.chat.domain.usecase

import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkMessageAsReadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(messageId: String): Result<Unit> {
        return if (messageId.isBlank()) {
            Result.failure(Exception("Message ID cannot be empty"))
        } else {
            chatRepository.markMessageAsRead(messageId)
        }
    }
}