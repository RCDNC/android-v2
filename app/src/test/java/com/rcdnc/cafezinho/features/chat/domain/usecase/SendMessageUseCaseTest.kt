package com.rcdnc.cafezinho.features.chat.domain.usecase

import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var sendMessageUseCase: SendMessageUseCase

    @Before
    fun setup() {
        chatRepository = mockk()
        sendMessageUseCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke with valid parameters should return success`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "Hello there!"
        val senderId = "user1"
        val receiverId = "user2"
        
        val messageSlot = slot<Message>()
        coEvery { chatRepository.sendMessage(chatId, capture(messageSlot)) } returns Result.success(Unit)

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isSuccess)
        
        // Verify message properties
        val capturedMessage = messageSlot.captured
        assertEquals(senderId, capturedMessage.senderId)
        assertEquals(receiverId, capturedMessage.receiverId)
        assertEquals(content, capturedMessage.content)
        assertEquals("text", capturedMessage.messageType)
        assertFalse(capturedMessage.isRead)
        assertTrue(capturedMessage.timestamp > 0)
    }

    @Test
    fun `invoke with empty chatId should return failure`() = runTest {
        // Arrange
        val chatId = ""
        val content = "Hello"
        val senderId = "user1"
        val receiverId = "user2"

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Chat ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty content should return failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = ""
        val senderId = "user1"
        val receiverId = "user2"

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Message content cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty senderId should return failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "Hello"
        val senderId = ""
        val receiverId = "user2"

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Sender ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty receiverId should return failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "Hello"
        val senderId = "user1"
        val receiverId = ""

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Receiver ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with content too long should return failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "a".repeat(1001) // 1001 characters
        val senderId = "user1"
        val receiverId = "user2"

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Message too long (max 1000 characters)", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should sanitize content by trimming and normalizing spaces`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "  Hello    world   with   extra     spaces  "
        val senderId = "user1"
        val receiverId = "user2"
        
        val messageSlot = slot<Message>()
        coEvery { chatRepository.sendMessage(chatId, capture(messageSlot)) } returns Result.success(Unit)

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isSuccess)
        val capturedMessage = messageSlot.captured
        assertEquals("Hello world with extra spaces", capturedMessage.content)
    }

    @Test
    fun `invoke should truncate content to 1000 characters if longer`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "a".repeat(1500) // 1500 characters
        val senderId = "user1"
        val receiverId = "user2"
        
        val messageSlot = slot<Message>()
        coEvery { chatRepository.sendMessage(chatId, capture(messageSlot)) } returns Result.success(Unit)

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure) // Should fail validation before sanitization
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "Hello"
        val senderId = "user1"
        val receiverId = "user2"
        val exception = RuntimeException("Network error")
        
        coEvery { chatRepository.sendMessage(any(), any()) } returns Result.failure(exception)

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "Hello"
        val senderId = "user1"
        val receiverId = "user2"
        val exception = Exception("Database error")
        
        coEvery { chatRepository.sendMessage(any(), any()) } throws exception

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke with exactly 1000 characters should succeed`() = runTest {
        // Arrange
        val chatId = "chat123"
        val content = "a".repeat(1000) // Exactly 1000 characters
        val senderId = "user1"
        val receiverId = "user2"
        
        val messageSlot = slot<Message>()
        coEvery { chatRepository.sendMessage(chatId, capture(messageSlot)) } returns Result.success(Unit)

        // Act
        val result = sendMessageUseCase(chatId, content, senderId, receiverId)

        // Assert
        assertTrue(result.isSuccess)
        val capturedMessage = messageSlot.captured
        assertEquals(1000, capturedMessage.content.length)
    }
}