package com.rcdnc.cafezinho.features.swipe.presentation.viewmodel

import com.rcdnc.cafezinho.features.swipe.domain.model.*
import com.rcdnc.cafezinho.features.swipe.domain.repository.SwipeRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Testes unitários para SwipeViewModel
 * Testa MVI pattern, state management e integração com repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwipeViewModelTest {
    
    // Test dependencies
    private lateinit var mockRepository: SwipeRepository
    private lateinit var viewModel: SwipeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val testUserId = "123"
    private val sampleUsers = listOf(
        SwipeUser(id = "1", firstName = "Ana", age = 25),
        SwipeUser(id = "2", firstName = "Bruno", age = 28),
        SwipeUser(id = "3", firstName = "Carlos", age = 30)
    )
    
    private val sampleMetrics = SwipeMetrics(
        dailyLikesUsed = 5,
        dailyLikesLimit = 100,
        superLikesUsed = 1,
        superLikesLimit = 5,
        isPremium = true,
        canUseRewind = true
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        
        // Default mocks
        coEvery { mockRepository.getNearbyUsers(any(), any()) } returns Result.success(sampleUsers)
        coEvery { mockRepository.getTopUsers(any()) } returns Result.success(emptyList())
        coEvery { mockRepository.getUserMetrics(any()) } returns Result.success(sampleMetrics)
        
        viewModel = SwipeViewModel(mockRepository)
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
        val freshViewModel = SwipeViewModel(mockRepository)
        
        // When - checking initial state
        val initialState = freshViewModel.state.first()
        
        // Then - should be idle
        assertEquals(SwipeState.Idle, initialState)
    }
    
    @Test
    fun `loadUsers should fetch users and update stack`() = runTest {
        // Given - mocked successful response
        coEvery { mockRepository.getTopUsers(testUserId) } returns Result.success(emptyList())
        coEvery { mockRepository.getNearbyUsers(testUserId, any()) } returns Result.success(sampleUsers)
        
        // When - loading users
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update stack with users
        val stack = viewModel.swipeStack.value
        assertEquals(3, stack.size)
        assertEquals("Ana", stack[0].firstName)
        assertEquals("Bruno", stack[1].firstName)
        assertEquals("Carlos", stack[2].firstName)
        
        // And - state should be UsersLoaded
        assertTrue(viewModel.state.value is SwipeState.UsersLoaded)
    }
    
    @Test
    fun `loadUsers with top users should prioritize them`() = runTest {
        // Given - mocked responses with top users
        val topUsers = listOf(SwipeUser(id = "top1", firstName = "Premium", age = 26))
        coEvery { mockRepository.getTopUsers(testUserId) } returns Result.success(topUsers)
        coEvery { mockRepository.getNearbyUsers(testUserId, any()) } returns Result.success(sampleUsers)
        
        // When - loading users
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - top users should be first in stack
        val stack = viewModel.swipeStack.value
        assertEquals("Premium", stack[0].firstName)
        assertTrue(stack.size > 1) // Should have nearby users too
        
        // Verify both APIs were called
        coVerify { mockRepository.getTopUsers(testUserId) }
        coVerify { mockRepository.getNearbyUsers(testUserId, any()) }
    }
    
    @Test
    fun `swipeUser with LIKE should call repository and remove user from stack`() = runTest {
        // Given - users loaded
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val userToSwipe = sampleUsers[0]
        val swipeResult = SwipeResult(SwipeAction.LIKE, userToSwipe, isMatch = false)
        
        coEvery { 
            mockRepository.performSwipeAction(testUserId, userToSwipe.id, SwipeAction.LIKE) 
        } returns Result.success(swipeResult)
        
        coEvery { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) } returns Result.success(Unit)
        
        // When - swiping user
        viewModel.handleIntent(SwipeIntent.SwipeUser(userToSwipe, SwipeAction.LIKE))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - user should be removed from stack
        val stack = viewModel.swipeStack.value
        assertFalse(stack.any { it.id == userToSwipe.id })
        
        // And - repository methods should be called
        coVerify { mockRepository.performSwipeAction(testUserId, userToSwipe.id, SwipeAction.LIKE) }
        coVerify { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) }
        
        // And - state should be UserSwiped
        assertTrue(viewModel.state.value is SwipeState.UserSwiped)
    }
    
    @Test
    fun `swipeUser with DISLIKE should not call like API but mark as viewed`() = runTest {
        // Given - users loaded
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val userToSwipe = sampleUsers[0]
        coEvery { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) } returns Result.success(Unit)
        
        // When - disliking user
        viewModel.handleIntent(SwipeIntent.SwipeUser(userToSwipe, SwipeAction.DISLIKE))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - user should be removed from stack
        val stack = viewModel.swipeStack.value
        assertFalse(stack.any { it.id == userToSwipe.id })
        
        // And - should mark as viewed but not call performSwipeAction
        coVerify { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) }
        coVerify(exactly = 0) { mockRepository.performSwipeAction(any(), any(), any()) }
        
        // And - state should be UserSwiped
        assertTrue(viewModel.state.value is SwipeState.UserSwiped)
    }
    
    @Test
    fun `swipeUser with match should show match state`() = runTest {
        // Given - users loaded
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val userToSwipe = sampleUsers[0]
        val matchData = MatchData("match123", System.currentTimeMillis(), "É um match!")
        val swipeResult = SwipeResult(SwipeAction.LIKE, userToSwipe, isMatch = true, matchData = matchData)
        
        coEvery { 
            mockRepository.performSwipeAction(testUserId, userToSwipe.id, SwipeAction.LIKE) 
        } returns Result.success(swipeResult)
        
        coEvery { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) } returns Result.success(Unit)
        
        // When - swiping user that results in match
        viewModel.handleIntent(SwipeIntent.SwipeUser(userToSwipe, SwipeAction.LIKE))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - state should be MatchFound
        val state = viewModel.state.value
        assertTrue(state is SwipeState.MatchFound)
        assertEquals(true, (state as SwipeState.MatchFound).result.isMatch)
    }
    
    @Test
    fun `rewindLastSwipe should add user back to stack`() = runTest {
        // Given - users loaded and one swiped
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        val userToSwipe = sampleUsers[0]
        coEvery { mockRepository.markUserAsViewed(testUserId, userToSwipe.id) } returns Result.success(Unit)
        
        viewModel.handleIntent(SwipeIntent.SwipeUser(userToSwipe, SwipeAction.DISLIKE))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Mock rewind success
        coEvery { mockRepository.rewindLastAction(testUserId, userToSwipe.id) } returns Result.success(userToSwipe)
        
        // When - rewinding last swipe
        viewModel.handleIntent(SwipeIntent.RewindLastSwipe)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - user should be back in stack
        val stack = viewModel.swipeStack.value
        assertTrue(stack.any { it.id == userToSwipe.id })
        assertEquals(userToSwipe.id, stack[0].id) // Should be at top
        
        // And - state should be RewindSuccess
        assertTrue(viewModel.state.value is SwipeState.RewindSuccess)
    }
    
    @Test
    fun `canLike should return false when daily limit reached`() = runTest {
        // Given - metrics with limit reached
        val limitReachedMetrics = sampleMetrics.copy(
            dailyLikesUsed = 100,
            dailyLikesLimit = 100
        )
        coEvery { mockRepository.getUserMetrics(testUserId) } returns Result.success(limitReachedMetrics)
        
        viewModel.handleIntent(SwipeIntent.LoadMetrics)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if can like
        val canLike = viewModel.canLike()
        
        // Then - should return false
        assertFalse(canLike)
    }
    
    @Test
    fun `canSuperLike should return false when limit reached`() = runTest {
        // Given - metrics with super like limit reached
        val limitReachedMetrics = sampleMetrics.copy(
            superLikesUsed = 5,
            superLikesLimit = 5,
            canUseSuperLike = false
        )
        coEvery { mockRepository.getUserMetrics(testUserId) } returns Result.success(limitReachedMetrics)
        
        viewModel.handleIntent(SwipeIntent.LoadMetrics)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if can super like
        val canSuperLike = viewModel.canSuperLike()
        
        // Then - should return false
        assertFalse(canSuperLike)
    }
    
    @Test
    fun `updateFilters should call repository and reload users`() = runTest {
        // Given - new filters
        val newFilters = SwipeFilters(
            minAge = 25,
            maxAge = 35,
            maxDistance = 50,
            genderPreference = "female"
        )
        
        coEvery { mockRepository.updateDiscoveryFilters(testUserId, newFilters) } returns Result.success(Unit)
        coEvery { mockRepository.getTopUsers(testUserId) } returns Result.success(emptyList())
        coEvery { mockRepository.getNearbyUsers(testUserId, newFilters) } returns Result.success(sampleUsers)
        
        // When - updating filters
        viewModel.handleIntent(SwipeIntent.UpdateFilters(newFilters))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should call repository to update filters
        coVerify { mockRepository.updateDiscoveryFilters(testUserId, newFilters) }
        
        // And - should reload users with new filters
        coVerify { mockRepository.getNearbyUsers(testUserId, newFilters) }
        
        // And - state should be FiltersUpdated
        assertTrue(viewModel.state.value is SwipeState.FiltersUpdated)
    }
    
    @Test
    fun `error in repository should update state to error`() = runTest {
        // Given - repository that returns error
        val errorMessage = "Network error"
        coEvery { mockRepository.getTopUsers(testUserId) } returns Result.failure(Exception(errorMessage))
        coEvery { mockRepository.getNearbyUsers(testUserId, any()) } returns Result.failure(Exception(errorMessage))
        
        // When - loading users
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - state should be Error
        val state = viewModel.state.value
        assertTrue(state is SwipeState.Error)
        assertEquals(errorMessage, (state as SwipeState.Error).message)
    }
    
    @Test
    fun `clearError should reset state to idle`() = runTest {
        // Given - error state
        coEvery { mockRepository.getNearbyUsers(testUserId, any()) } returns Result.failure(Exception("Error"))
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value is SwipeState.Error)
        
        // When - clearing error
        viewModel.handleIntent(SwipeIntent.ClearError)
        
        // Then - state should be Idle
        assertEquals(SwipeState.Idle, viewModel.state.value)
    }
    
    @Test
    fun `getCurrentUser should return first user in stack`() = runTest {
        // Given - users loaded
        viewModel.handleIntent(SwipeIntent.LoadUsers())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting current user
        val currentUser = viewModel.getCurrentUser()
        
        // Then - should return first user
        assertNotNull(currentUser)
        assertEquals(sampleUsers[0].id, currentUser?.id)
    }
    
    @Test
    fun `getLikesRemaining should calculate correctly`() = runTest {
        // Given - loaded metrics
        viewModel.handleIntent(SwipeIntent.LoadMetrics)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting likes remaining
        val likesRemaining = viewModel.getLikesRemaining()
        
        // Then - should calculate correctly (100 - 5 = 95)
        assertEquals(95, likesRemaining)
    }
}