package com.rcdnc.cafezinho.features.chat.presentation.viewmodel

import com.rcdnc.cafezinho.features.chat.domain.model.ChatConversation
import com.rcdnc.cafezinho.features.chat.domain.model.ChatMessage
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Testes unitários para ChatViewModel
 * Testa MVI pattern, messaging e integração com repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    
    // Test dependencies
    private lateinit var mockRepository: ChatRepository
    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val testUserId = "123"
    private val testConversationId = "conv123"
    private val testReceiverId = "456"
    
    private val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            conversationId = testConversationId,
            senderId = testUserId,
            receiverId = testReceiverId,
            content = "Oi, tudo bem?",
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis() - 60000,
            isRead = true
        ),
        ChatMessage(
            id = "2",
            conversationId = testConversationId,
            senderId = testReceiverId,
            receiverId = testUserId,
            content = "Tudo ótimo! E você?",
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis() - 30000,
            isRead = false
        )
    )
    
    private val sampleConversations = listOf(
        ChatConversation(
            id = testConversationId,
            participantId = testReceiverId,
            participantName = "Ana Silva",
            participantPhotoUrl = "https://example.com/photo.jpg",
            lastMessage = sampleMessages.last(),
            unreadCount = 1,
            isOnline = true
        )
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        
        // Default mocks
        coEvery { mockRepository.getConversations(any()) } returns Result.success(sampleConversations)
        coEvery { mockRepository.getMessages(any()) } returns Result.success(sampleMessages)
        coEvery { mockRepository.observeMessages(any()) } returns flowOf(sampleMessages)
        
        viewModel = ChatViewModel(mockRepository)
        viewModel.setCurrentUserId(testUserId)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
    
    @Test
    fun `initial state should be idle`() = runTest {
        // Given - fresh ViewModel
        val freshViewModel = ChatViewModel(mockRepository)
        
        // When - checking initial state
        val initialState = freshViewModel.state.first()
        
        // Then - should be idle
        assertEquals(ChatState.Idle, initialState)
    }
    
    @Test
    fun `loadConversations should fetch and update conversations`() = runTest {
        // Given - mocked successful response
        coEvery { mockRepository.getConversations(testUserId) } returns Result.success(sampleConversations)
        
        // When - loading conversations
        viewModel.handleIntent(ChatIntent.LoadConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update conversations
        val conversations = viewModel.conversations.value
        assertEquals(1, conversations.size)
        assertEquals("Ana Silva", conversations[0].participantName)
        assertEquals(1, conversations[0].unreadCount)
        
        // And - state should be ConversationsLoaded
        assertTrue(viewModel.state.value is ChatState.ConversationsLoaded)
    }
    
    @Test
    fun `loadMessages should fetch and update messages`() = runTest {
        // Given - mocked successful response
        coEvery { mockRepository.getMessages(testConversationId) } returns Result.success(sampleMessages)
        
        // When - loading messages
        viewModel.handleIntent(ChatIntent.LoadMessages(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update messages
        val messages = viewModel.messages.value
        assertEquals(2, messages.size)
        assertEquals("Oi, tudo bem?", messages[0].content)
        assertEquals("Tudo ótimo! E você?", messages[1].content)
        
        // And - state should be MessagesLoaded
        assertTrue(viewModel.state.value is ChatState.MessagesLoaded)
    }
    
    @Test
    fun `sendMessage should call repository and update local messages`() = runTest {
        // Given - conversation loaded
        viewModel.handleIntent(ChatIntent.LoadMessages(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val messageContent = "Nova mensagem de teste"
        val newMessage = ChatMessage(
            id = "3",
            conversationId = testConversationId,
            senderId = testUserId,
            receiverId = testReceiverId,
            content = messageContent,
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        
        coEvery { 
            mockRepository.sendMessage(testUserId, testReceiverId, messageContent, MessageType.TEXT) 
        } returns Result.success(newMessage)
        
        // When - sending message
        viewModel.handleIntent(ChatIntent.SendMessage(testReceiverId, messageContent))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.sendMessage(testUserId, testReceiverId, messageContent, MessageType.TEXT) }
        
        // And - state should be MessageSent
        assertTrue(viewModel.state.value is ChatState.MessageSent)
    }
    
    @Test
    fun `markAsRead should call repository`() = runTest {
        // Given - messages loaded
        viewModel.handleIntent(ChatIntent.LoadMessages(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val messageId = "2"
        coEvery { mockRepository.markMessageAsRead(messageId) } returns Result.success(Unit)
        
        // When - marking message as read
        viewModel.handleIntent(ChatIntent.MarkAsRead(messageId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.markMessageAsRead(messageId) }
    }
    
    @Test
    fun `startTyping should call repository and update typing state`() = runTest {
        // Given - conversation setup
        val conversationId = testConversationId
        coEvery { mockRepository.sendTypingIndicator(testUserId, conversationId, true) } returns Result.success(Unit)
        
        // When - starting typing
        viewModel.handleIntent(ChatIntent.StartTyping(conversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.sendTypingIndicator(testUserId, conversationId, true) }
        
        // And - typing state should be updated
        assertTrue(viewModel.isTyping.value)
    }
    
    @Test
    fun `stopTyping should call repository and update typing state`() = runTest {
        // Given - user is typing
        viewModel.handleIntent(ChatIntent.StartTyping(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        coEvery { mockRepository.sendTypingIndicator(testUserId, testConversationId, false) } returns Result.success(Unit)
        
        // When - stopping typing
        viewModel.handleIntent(ChatIntent.StopTyping(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.sendTypingIndicator(testUserId, testConversationId, false) }
        
        // And - typing state should be updated
        assertFalse(viewModel.isTyping.value)
    }
    
    @Test
    fun `deleteMessage should call repository and update messages`() = runTest {
        // Given - messages loaded
        viewModel.handleIntent(ChatIntent.LoadMessages(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val messageToDelete = sampleMessages[0]
        coEvery { mockRepository.deleteMessage(messageToDelete.id) } returns Result.success(Unit)
        
        // When - deleting message
        viewModel.handleIntent(ChatIntent.DeleteMessage(messageToDelete.id))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.deleteMessage(messageToDelete.id) }
        
        // And - state should be MessageDeleted
        assertTrue(viewModel.state.value is ChatState.MessageDeleted)
    }
    
    @Test
    fun `observeMessages should update messages flow`() = runTest {
        // Given - observable messages
        val newMessage = ChatMessage(
            id = "new",
            conversationId = testConversationId,
            senderId = testReceiverId,
            receiverId = testUserId,
            content = "Nova mensagem em tempo real",
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        
        val updatedMessages = sampleMessages + newMessage
        coEvery { mockRepository.observeMessages(testConversationId) } returns flowOf(updatedMessages)
        
        // When - starting to observe messages
        viewModel.handleIntent(ChatIntent.ObserveMessages(testConversationId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - messages should be updated via flow
        val messages = viewModel.messages.value
        assertTrue(messages.any { it.content == "Nova mensagem em tempo real" })
    }
    
    @Test
    fun `error in repository should update state to error`() = runTest {
        // Given - repository that returns error
        val errorMessage = "Failed to load conversations"
        coEvery { mockRepository.getConversations(testUserId) } returns Result.failure(Exception(errorMessage))
        
        // When - loading conversations
        viewModel.handleIntent(ChatIntent.LoadConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - state should be Error
        val state = viewModel.state.value
        assertTrue(state is ChatState.Error)
        assertEquals(errorMessage, (state as ChatState.Error).message)
    }
    
    @Test
    fun `clearError should reset state to idle`() = runTest {
        // Given - error state
        coEvery { mockRepository.getConversations(testUserId) } returns Result.failure(Exception("Error"))
        viewModel.handleIntent(ChatIntent.LoadConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value is ChatState.Error)
        
        // When - clearing error
        viewModel.handleIntent(ChatIntent.ClearError)
        
        // Then - state should be Idle
        assertEquals(ChatState.Idle, viewModel.state.value)
    }
    
    @Test
    fun `getCurrentConversation should return correct conversation`() {
        // Given - conversations loaded
        viewModel.handleIntent(ChatIntent.LoadConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting current conversation
        val conversation = viewModel.getCurrentConversation(testConversationId)
        
        // Then - should return correct conversation
        assertNotNull(conversation)
        assertEquals(testConversationId, conversation?.id)
        assertEquals("Ana Silva", conversation?.participantName)
    }
    
    @Test
    fun `getUnreadCount should calculate total unread messages`() = runTest {
        // Given - conversations with unread messages
        viewModel.handleIntent(ChatIntent.LoadConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting unread count
        val unreadCount = viewModel.getUnreadCount()
        
        // Then - should return total unread
        assertEquals(1, unreadCount) // Sample has 1 unread
    }
    
    @Test
    fun `canSendMessage should return false for empty content`() {
        // When - checking if can send empty message
        val canSend = viewModel.canSendMessage("")
        
        // Then - should return false
        assertFalse(canSend)
    }
    
    @Test
    fun `canSendMessage should return false for blank content`() {
        // When - checking if can send blank message
        val canSend = viewModel.canSendMessage("   ")
        
        // Then - should return false
        assertFalse(canSend)
    }
    
    @Test
    fun `canSendMessage should return true for valid content`() {
        // When - checking if can send valid message
        val canSend = viewModel.canSendMessage("Mensagem válida")
        
        // Then - should return true
        assertTrue(canSend)
    }
}