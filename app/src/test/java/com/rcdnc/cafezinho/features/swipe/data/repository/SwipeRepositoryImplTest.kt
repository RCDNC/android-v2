package com.rcdnc.cafezinho.features.swipe.data.repository

import com.rcdnc.cafezinho.features.swipe.data.remote.SwipeApiService
import com.rcdnc.cafezinho.features.swipe.data.remote.dto.*
import com.rcdnc.cafezinho.features.swipe.domain.model.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

/**
 * Testes unitários para SwipeRepositoryImpl
 * Testa integração com API Laravel e mapeamento de DTOs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwipeRepositoryImplTest {
    
    // Test dependencies
    private lateinit var mockApiService: SwipeApiService
    private lateinit var repository: SwipeRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val testUserId = "123"
    private val targetUserId = "456"
    
    private val sampleUserDtos = listOf(
        SwipeUserDto(
            id = 1,
            firstName = "Ana",
            lastName = "Silva",
            age = 25,
            bio = "Adoro viajar!",
            location = "São Paulo, SP",
            distance = "2 km",
            photos = listOf(
                SwipePhotoDto("1", "https://example.com/photo1.jpg", 0, true),
                SwipePhotoDto("2", "https://example.com/photo2.jpg", 1, false)
            ),
            interests = listOf("Viajar", "Música"),
            jobTitle = "Designer",
            isVerified = true,
            isPremium = false,
            isOnline = true,
            rating = 4.5,
            profileCompletion = 85
        )
    )
    
    private val sampleConsumables = UserConsumablesDto(
        dailyLikesUsed = 5,
        dailyLikesLimit = 100,
        superLikesUsed = 1,
        superLikesLimit = 5,
        rewindsUsed = 0,
        rewindsLimit = 3,
        isPremium = true,
        canUseRewind = true,
        canUseSuperLike = true
    )
    
    private val sampleFilters = SwipeFilters(
        minAge = 20,
        maxAge = 35,
        maxDistance = 50,
        genderPreference = "female"
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApiService = mockk()
        repository = SwipeRepositoryImpl(mockApiService)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
    
    @Test
    fun `getNearbyUsers success should return mapped users`() = runTest {
        // Given - successful API response
        val response = SwipeUsersResponse(
            success = true,
            message = "Success",
            data = sampleUserDtos
        )
        
        coEvery { mockApiService.getNearbyUsers(123, any(), any(), any(), any(), any(), any()) } returns 
            Response.success(response)
        
        // When - getting nearby users
        val result = repository.getNearbyUsers(testUserId, sampleFilters)
        
        // Then - should return success with mapped users
        assertTrue(result.isSuccess)
        val users = result.getOrNull()!!
        assertEquals(1, users.size)
        
        val user = users[0]
        assertEquals("1", user.id)
        assertEquals("Ana", user.firstName)
        assertEquals("Silva", user.lastName)
        assertEquals(25, user.age)
        assertEquals("Adoro viajar!", user.bio)
        assertEquals(2, user.photos.size)
        assertEquals(true, user.isVerified)
        assertEquals(false, user.isPremium)
        
        // Verify API was called with correct parameters
        coVerify { 
            mockApiService.getNearbyUsers(
                userId = 123,
                minAge = 20,
                maxAge = 35,
                radius = 50,
                gender = "female"
            ) 
        }
    }
    
    @Test
    fun `getNearbyUsers failure should return error`() = runTest {
        // Given - failed API response
        val response = SwipeUsersResponse(
            success = false,
            message = "No users found",
            data = emptyList()
        )
        
        coEvery { mockApiService.getNearbyUsers(123, any(), any(), any(), any(), any(), any()) } returns 
            Response.success(response)
        
        // When - getting nearby users
        val result = repository.getNearbyUsers(testUserId)
        
        // Then - should return failure
        assertTrue(result.isFailure)
        assertEquals("No users found", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `getTopUsers success should return mapped users`() = runTest {
        // Given - successful API response
        val response = SwipeUsersResponse(
            success = true,
            message = "Success",
            data = sampleUserDtos
        )
        
        coEvery { mockApiService.getTopUsers(123) } returns Response.success(response)
        
        // When - getting top users
        val result = repository.getTopUsers(testUserId)
        
        // Then - should return success with mapped users
        assertTrue(result.isSuccess)
        val users = result.getOrNull()!!
        assertEquals(1, users.size)
        assertEquals("Ana", users[0].firstName)
        
        // Verify API was called
        coVerify { mockApiService.getTopUsers(123) }
    }
    
    @Test
    fun `performSwipeAction with LIKE should call API and return result`() = runTest {
        // Given - successful like response
        val likeResponse = LikeActionResponse(
            success = true,
            message = "Like sent",
            data = LikeActionData(
                isMatch = false,
                matchId = null,
                matchMessage = null,
                likesRemaining = 95,
                superLikesRemaining = 5
            )
        )
        
        coEvery { mockApiService.performLikeAction(any()) } returns Response.success(likeResponse)
        
        // When - performing like action
        val result = repository.performSwipeAction(testUserId, targetUserId, SwipeAction.LIKE)
        
        // Then - should return success with swipe result
        assertTrue(result.isSuccess)
        val swipeResult = result.getOrNull()!!
        assertEquals(SwipeAction.LIKE, swipeResult.action)
        assertEquals(false, swipeResult.isMatch)
        assertNull(swipeResult.matchData)
        
        // Verify API was called with correct parameters
        coVerify { 
            mockApiService.performLikeAction(
                match {
                    it.userId == 123 && 
                    it.targetUserId == 456 && 
                    it.action == "like"
                }
            ) 
        }
    }
    
    @Test
    fun `performSwipeAction with SUPER_LIKE should call API with correct action`() = runTest {
        // Given - successful super like response
        val likeResponse = LikeActionResponse(
            success = true,
            message = "Super like sent",
            data = LikeActionData(
                isMatch = true,
                matchId = "match123",
                matchMessage = "É um match!",
                likesRemaining = 100,
                superLikesRemaining = 4
            )
        )
        
        coEvery { mockApiService.performLikeAction(any()) } returns Response.success(likeResponse)
        
        // When - performing super like action
        val result = repository.performSwipeAction(testUserId, targetUserId, SwipeAction.SUPER_LIKE)
        
        // Then - should return success with match
        assertTrue(result.isSuccess)
        val swipeResult = result.getOrNull()!!
        assertEquals(SwipeAction.SUPER_LIKE, swipeResult.action)
        assertEquals(true, swipeResult.isMatch)
        assertNotNull(swipeResult.matchData)
        assertEquals("match123", swipeResult.matchData?.matchId)
        assertEquals("É um match!", swipeResult.matchData?.message)
        
        // Verify API was called with super_like action
        coVerify { 
            mockApiService.performLikeAction(
                match { it.action == "super_like" }
            ) 
        }
    }
    
    @Test
    fun `performSwipeAction with DISLIKE should not call API`() = runTest {
        // When - performing dislike action (should be handled locally)
        val result = repository.performSwipeAction(testUserId, targetUserId, SwipeAction.DISLIKE)
        
        // Then - should return success without API call
        assertTrue(result.isSuccess)
        val swipeResult = result.getOrNull()!!
        assertEquals(SwipeAction.DISLIKE, swipeResult.action)
        assertEquals(false, swipeResult.isMatch)
        
        // Verify API was NOT called for dislike
        coVerify(exactly = 0) { mockApiService.performLikeAction(any()) }
    }
    
    @Test
    fun `rewindLastAction success should return user`() = runTest {
        // Given - successful rewind response
        val rewindResponse = RewindResponse(
            success = true,
            message = "Action reverted",
            data = sampleUserDtos[0]
        )
        
        coEvery { mockApiService.rewindAction(123, 456) } returns Response.success(rewindResponse)
        
        // When - rewinding last action
        val result = repository.rewindLastAction(testUserId, targetUserId)
        
        // Then - should return success with user
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("1", user.id)
        assertEquals("Ana", user.firstName)
        
        // Verify API was called
        coVerify { mockApiService.rewindAction(123, 456) }
    }
    
    @Test
    fun `getUserMetrics success should return mapped metrics`() = runTest {
        // Given - successful metrics response
        val metricsResponse = UserConsumablesResponse(
            success = true,
            message = "Success",
            data = sampleConsumables
        )
        
        coEvery { mockApiService.getUserConsumables(123) } returns Response.success(metricsResponse)
        
        // When - getting user metrics
        val result = repository.getUserMetrics(testUserId)
        
        // Then - should return success with mapped metrics
        assertTrue(result.isSuccess)
        val metrics = result.getOrNull()!!
        assertEquals(5, metrics.dailyLikesUsed)
        assertEquals(100, metrics.dailyLikesLimit)
        assertEquals(1, metrics.superLikesUsed)
        assertEquals(5, metrics.superLikesLimit)
        assertEquals(true, metrics.isPremium)
        assertEquals(true, metrics.canUseRewind)
        
        // Verify API was called
        coVerify { mockApiService.getUserConsumables(123) }
    }
    
    @Test
    fun `updateDiscoveryFilters success should call API with mapped DTO`() = runTest {
        // Given - successful update response
        val updateResponse = ApiResponse<Unit>(
            success = true,
            message = "Preferences updated",
            data = Unit
        )
        
        coEvery { mockApiService.updateUserPreferences(123, any()) } returns Response.success(updateResponse)
        
        // When - updating filters
        val result = repository.updateDiscoveryFilters(testUserId, sampleFilters)
        
        // Then - should return success
        assertTrue(result.isSuccess)
        
        // Verify API was called with correct DTO
        coVerify { 
            mockApiService.updateUserPreferences(
                userId = 123,
                preferences = match {
                    it.minAge == 20 &&
                    it.maxAge == 35 &&
                    it.maxDistance == 50 &&
                    it.genderPreference == "female"
                }
            ) 
        }
    }
    
    @Test
    fun `getUserFilters success should return mapped filters`() = runTest {
        // Given - successful preferences response
        val preferencesDto = UserPreferencesDto(
            minAge = 22,
            maxAge = 40,
            maxDistance = 75,
            genderPreference = "all",
            showOnlineOnly = true,
            showVerifiedOnly = false,
            requiredInterests = listOf("Viajar", "Música")
        )
        
        val preferencesResponse = UserPreferencesResponse(
            success = true,
            message = "Success",
            data = preferencesDto
        )
        
        coEvery { mockApiService.getUserPreferences(123) } returns Response.success(preferencesResponse)
        
        // When - getting user filters
        val result = repository.getUserFilters(testUserId)
        
        // Then - should return success with mapped filters
        assertTrue(result.isSuccess)
        val filters = result.getOrNull()!!
        assertEquals(22, filters.minAge)
        assertEquals(40, filters.maxAge)
        assertEquals(75, filters.maxDistance)
        assertEquals("all", filters.genderPreference)
        assertEquals(true, filters.showOnlineOnly)
        assertEquals(false, filters.showVerifiedOnly)
        assertEquals(2, filters.requiredInterests.size)
        
        // Verify API was called
        coVerify { mockApiService.getUserPreferences(123) }
    }
    
    @Test
    fun `markUserAsViewed success should call API`() = runTest {
        // Given - successful view response
        val viewResponse = ApiResponse<Unit>(
            success = true,
            message = "User marked as viewed",
            data = Unit
        )
        
        coEvery { mockApiService.markUserAsViewed(any()) } returns Response.success(viewResponse)
        
        // When - marking user as viewed
        val result = repository.markUserAsViewed(testUserId, targetUserId)
        
        // Then - should return success
        assertTrue(result.isSuccess)
        
        // Verify API was called with correct request
        coVerify { 
            mockApiService.markUserAsViewed(
                match {
                    it.viewerId == 123 && it.viewedUserId == 456
                }
            ) 
        }
    }
    
    @Test
    fun `reportUser success should call API`() = runTest {
        // Given - successful report response
        val reportResponse = ApiResponse<Unit>(
            success = true,
            message = "User reported",
            data = Unit
        )
        
        coEvery { mockApiService.reportUser(any()) } returns Response.success(reportResponse)
        
        // When - reporting user
        val result = repository.reportUser(testUserId, targetUserId, "inappropriate_content")
        
        // Then - should return success
        assertTrue(result.isSuccess)
        
        // Verify API was called with correct request
        coVerify { 
            mockApiService.reportUser(
                match {
                    it.reporterId == 123 && 
                    it.reportedUserId == 456 && 
                    it.reason == "inappropriate_content"
                }
            ) 
        }
    }
    
    @Test
    fun `API network error should return failure`() = runTest {
        // Given - network exception
        coEvery { mockApiService.getNearbyUsers(any(), any(), any(), any(), any(), any(), any()) } throws 
            Exception("Network error")
        
        // When - calling repository
        val result = repository.getNearbyUsers(testUserId)
        
        // Then - should return failure
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `HTTP error response should return failure`() = runTest {
        // Given - HTTP error response
        coEvery { mockApiService.getNearbyUsers(any(), any(), any(), any(), any(), any(), any()) } returns 
            Response.error(404, mockk(relaxed = true))
        
        // When - calling repository
        val result = repository.getNearbyUsers(testUserId)
        
        // Then - should return failure
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Erro na API") == true)
    }
}