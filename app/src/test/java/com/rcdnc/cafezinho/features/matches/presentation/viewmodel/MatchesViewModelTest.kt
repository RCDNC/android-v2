package com.rcdnc.cafezinho.features.matches.presentation.viewmodel

import com.rcdnc.cafezinho.features.matches.domain.model.Match
import com.rcdnc.cafezinho.features.matches.domain.model.MatchType
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchesRepository
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
 * Testes unitários para MatchesViewModel
 * Testa MVI pattern, match management e integração com repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MatchesViewModelTest {
    
    // Test dependencies
    private lateinit var mockRepository: MatchesRepository
    private lateinit var viewModel: MatchesViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val testUserId = "123"
    
    private val sampleMatches = listOf(
        Match(
            id = "1",
            userId = testUserId,
            matchedUserId = "456",
            matchedUserName = "Ana Silva",
            matchedUserPhoto = "https://example.com/ana.jpg",
            type = MatchType.MUTUAL_LIKE,
            timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
            isNew = true,
            lastMessage = "Oi! Como você está?",
            lastMessageTime = System.currentTimeMillis() - 1800000, // 30 min ago
            unreadCount = 2
        ),
        Match(
            id = "2",
            userId = testUserId,
            matchedUserId = "789",
            matchedUserName = "Bruno Costa",
            matchedUserPhoto = "https://example.com/bruno.jpg",
            type = MatchType.SUPER_LIKE,
            timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
            isNew = false,
            lastMessage = "Adorei seu perfil!",
            lastMessageTime = System.currentTimeMillis() - 3600000, // 1 hour ago
            unreadCount = 0
        )
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        
        // Default mocks
        coEvery { mockRepository.getMatches(any()) } returns Result.success(sampleMatches)
        coEvery { mockRepository.observeMatches(any()) } returns flowOf(sampleMatches)
        
        viewModel = MatchesViewModel(mockRepository)
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
        val freshViewModel = MatchesViewModel(mockRepository)
        
        // When - checking initial state
        val initialState = freshViewModel.state.first()
        
        // Then - should be idle
        assertEquals(MatchesState.Idle, initialState)
    }
    
    @Test
    fun `loadMatches should fetch and update matches`() = runTest {
        // Given - mocked successful response
        coEvery { mockRepository.getMatches(testUserId) } returns Result.success(sampleMatches)
        
        // When - loading matches
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update matches
        val matches = viewModel.matches.value
        assertEquals(2, matches.size)
        assertEquals("Ana Silva", matches[0].matchedUserName)
        assertEquals("Bruno Costa", matches[1].matchedUserName)
        
        // And - state should be MatchesLoaded
        assertTrue(viewModel.state.value is MatchesState.MatchesLoaded)
    }
    
    @Test
    fun `loadMatches with refresh should clear cache and reload`() = runTest {
        // Given - initial matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val updatedMatches = listOf(sampleMatches[0]) // Only one match now
        coEvery { mockRepository.getMatches(testUserId) } returns Result.success(updatedMatches)
        
        // When - loading with refresh
        viewModel.handleIntent(MatchesIntent.LoadMatches(refresh = true))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update with new data
        val matches = viewModel.matches.value
        assertEquals(1, matches.size)
        
        // Verify repository was called again
        coVerify(exactly = 2) { mockRepository.getMatches(testUserId) }
    }
    
    @Test
    fun `observeMatches should start real-time updates`() = runTest {
        // Given - observable matches with updates
        val newMatch = Match(
            id = "3",
            userId = testUserId,
            matchedUserId = "999",
            matchedUserName = "Clara Mendes",
            matchedUserPhoto = "https://example.com/clara.jpg",
            type = MatchType.MUTUAL_LIKE,
            timestamp = System.currentTimeMillis(),
            isNew = true
        )
        
        val updatedMatches = sampleMatches + newMatch
        coEvery { mockRepository.observeMatches(testUserId) } returns flowOf(updatedMatches)
        
        // When - starting to observe matches
        viewModel.handleIntent(MatchesIntent.ObserveMatches)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - matches should be updated via flow
        val matches = viewModel.matches.value
        assertEquals(3, matches.size)
        assertTrue(matches.any { it.matchedUserName == "Clara Mendes" })
        
        // And - state should be updated
        assertTrue(viewModel.state.value is MatchesState.MatchesUpdated)
    }
    
    @Test
    fun `markMatchAsRead should call repository and update local match`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val matchId = "1"
        coEvery { mockRepository.markMatchAsRead(matchId) } returns Result.success(Unit)
        
        // When - marking match as read
        viewModel.handleIntent(MatchesIntent.MarkAsRead(matchId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.markMatchAsRead(matchId) }
        
        // And - state should be MatchMarkedAsRead
        assertTrue(viewModel.state.value is MatchesState.MatchMarkedAsRead)
    }
    
    @Test
    fun `deleteMatch should call repository and remove from local list`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val matchToDelete = sampleMatches[0]
        coEvery { mockRepository.deleteMatch(matchToDelete.id) } returns Result.success(Unit)
        
        // When - deleting match
        viewModel.handleIntent(MatchesIntent.DeleteMatch(matchToDelete.id))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.deleteMatch(matchToDelete.id) }
        
        // And - state should be MatchDeleted
        val state = viewModel.state.value
        assertTrue(state is MatchesState.MatchDeleted)
        assertEquals(matchToDelete.id, (state as MatchesState.MatchDeleted).matchId)
    }
    
    @Test
    fun `blockUser should call repository and remove match`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val matchToBlock = sampleMatches[0]
        coEvery { mockRepository.blockUser(testUserId, matchToBlock.matchedUserId) } returns Result.success(Unit)
        
        // When - blocking user
        viewModel.handleIntent(MatchesIntent.BlockUser(matchToBlock.matchedUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.blockUser(testUserId, matchToBlock.matchedUserId) }
        
        // And - state should be UserBlocked
        val state = viewModel.state.value
        assertTrue(state is MatchesState.UserBlocked)
        assertEquals(matchToBlock.matchedUserId, (state as MatchesState.UserBlocked).userId)
    }
    
    @Test
    fun `reportUser should call repository`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val userToReport = sampleMatches[0].matchedUserId
        val reason = "inappropriate_behavior"
        coEvery { mockRepository.reportUser(testUserId, userToReport, reason) } returns Result.success(Unit)
        
        // When - reporting user
        viewModel.handleIntent(MatchesIntent.ReportUser(userToReport, reason))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.reportUser(testUserId, userToReport, reason) }
        
        // And - state should be UserReported
        val state = viewModel.state.value
        assertTrue(state is MatchesState.UserReported)
        assertEquals(userToReport, (state as MatchesState.UserReported).userId)
    }
    
    @Test
    fun `filterByType should update filtered matches`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - filtering by SUPER_LIKE
        viewModel.handleIntent(MatchesIntent.FilterByType(MatchType.SUPER_LIKE))
        
        // Then - should update filtered matches
        val filteredMatches = viewModel.filteredMatches.value
        assertEquals(1, filteredMatches.size) // Only Bruno has SUPER_LIKE
        assertEquals("Bruno Costa", filteredMatches[0].matchedUserName)
        
        // And - state should be FilterApplied
        val state = viewModel.state.value
        assertTrue(state is MatchesState.FilterApplied)
        assertEquals(MatchType.SUPER_LIKE, (state as MatchesState.FilterApplied).filterType)
    }
    
    @Test
    fun `clearFilter should show all matches`() = runTest {
        // Given - matches loaded and filtered
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.handleIntent(MatchesIntent.FilterByType(MatchType.SUPER_LIKE))
        assertEquals(1, viewModel.filteredMatches.value.size)
        
        // When - clearing filter
        viewModel.handleIntent(MatchesIntent.ClearFilter)
        
        // Then - should show all matches again
        val filteredMatches = viewModel.filteredMatches.value
        assertEquals(2, filteredMatches.size)
        
        // And - state should be FilterCleared
        assertTrue(viewModel.state.value is MatchesState.FilterCleared)
    }
    
    @Test
    fun `getNewMatchesCount should return correct count`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting new matches count
        val newCount = viewModel.getNewMatchesCount()
        
        // Then - should return count of new matches (1 in sample)
        assertEquals(1, newCount)
    }
    
    @Test
    fun `getTotalUnreadCount should return correct count`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting total unread count
        val unreadCount = viewModel.getTotalUnreadCount()
        
        // Then - should return sum of all unread messages (2 in sample)
        assertEquals(2, unreadCount)
    }
    
    @Test
    fun `getMutualLikesCount should return correct count`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting mutual likes count
        val mutualCount = viewModel.getMutualLikesCount()
        
        // Then - should return count of MUTUAL_LIKE matches (1 in sample)
        assertEquals(1, mutualCount)
    }
    
    @Test
    fun `getSuperLikesCount should return correct count`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting super likes count
        val superCount = viewModel.getSuperLikesCount()
        
        // Then - should return count of SUPER_LIKE matches (1 in sample)
        assertEquals(1, superCount)
    }
    
    @Test
    fun `findMatchByUserId should return correct match`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - finding match by user ID
        val match = viewModel.findMatchByUserId("456")
        
        // Then - should return correct match
        assertNotNull(match)
        assertEquals("Ana Silva", match?.matchedUserName)
        assertEquals(MatchType.MUTUAL_LIKE, match?.type)
    }
    
    @Test
    fun `error in repository should update state to error`() = runTest {
        // Given - repository that returns error
        val errorMessage = "Failed to load matches"
        coEvery { mockRepository.getMatches(testUserId) } returns Result.failure(Exception(errorMessage))
        
        // When - loading matches
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - state should be Error
        val state = viewModel.state.value
        assertTrue(state is MatchesState.Error)
        assertEquals(errorMessage, (state as MatchesState.Error).message)
    }
    
    @Test
    fun `clearError should reset state to idle`() = runTest {
        // Given - error state
        coEvery { mockRepository.getMatches(testUserId) } returns Result.failure(Exception("Error"))
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value is MatchesState.Error)
        
        // When - clearing error
        viewModel.handleIntent(MatchesIntent.ClearError)
        
        // Then - state should be Idle
        assertEquals(MatchesState.Idle, viewModel.state.value)
    }
    
    @Test
    fun `hasMatches should return true when matches exist`() = runTest {
        // Given - matches loaded
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if has matches
        val hasMatches = viewModel.hasMatches()
        
        // Then - should return true
        assertTrue(hasMatches)
    }
    
    @Test
    fun `hasMatches should return false when no matches`() = runTest {
        // Given - no matches
        coEvery { mockRepository.getMatches(testUserId) } returns Result.success(emptyList())
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if has matches
        val hasMatches = viewModel.hasMatches()
        
        // Then - should return false
        assertFalse(hasMatches)
    }
    
    @Test
    fun `isLoading should reflect loading state`() = runTest {
        // Given - slow repository response
        coEvery { mockRepository.getMatches(testUserId) } coAnswers {
            kotlinx.coroutines.delay(1000)
            Result.success(sampleMatches)
        }
        
        // When - starting to load
        viewModel.handleIntent(MatchesIntent.LoadMatches())
        
        // Then - should be loading
        assertTrue(viewModel.isLoading.value)
        
        // And after completion - should not be loading
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }
}