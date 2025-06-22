package com.rcdnc.cafezinho.features.profile.presentation.viewmodel

import com.rcdnc.cafezinho.features.profile.domain.model.*
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileStats
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Testes unitários para ProfileViewModel
 * Testa MVI pattern, profile management e integração com repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    
    // Test dependencies
    private lateinit var mockRepository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val testUserId = "123"
    
    private val sampleProfile = ProfileData(
        id = testUserId,
        firstName = "Ana",
        lastName = "Silva",
        age = 25,
        bio = "Adoro viajar e conhecer pessoas novas!",
        photos = listOf(
            ProfilePhoto("1", "https://example.com/photo1.jpg", 0, isMainPhoto = true),
            ProfilePhoto("2", "https://example.com/photo2.jpg", 1)
        ),
        interests = listOf(
            Interest("1", "Viajar", isSelected = true),
            Interest("2", "Música", isSelected = true),
            Interest("3", "Esportes", isSelected = false)
        ),
        profileCompletion = 85,
        isVerified = true,
        isPremium = false,
        jobTitle = "Designer",
        company = "Tech Company"
    )
    
    private val sampleStats = ProfileStats(
        profileCompletion = 85,
        totalPhotos = 2,
        totalInterests = 2,
        profileViews = 150,
        likes = 45,
        superLikes = 3,
        matches = 12,
        boostRemaining = 1
    )
    
    private val sampleInterests = listOf(
        Interest("1", "Viajar", category = "Hobbies"),
        Interest("2", "Música", category = "Arte"),
        Interest("3", "Esportes", category = "Atividades"),
        Interest("4", "Culinária", category = "Hobbies")
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        
        // Default mocks
        coEvery { mockRepository.getProfile(any()) } returns Result.success(sampleProfile)
        coEvery { mockRepository.getProfileStats(any()) } returns Result.success(sampleStats)
        coEvery { mockRepository.getAvailableInterests() } returns Result.success(sampleInterests)
        coEvery { mockRepository.observeProfile(any()) } returns flowOf(sampleProfile)
        
        viewModel = ProfileViewModel(mockRepository)
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
        val freshViewModel = ProfileViewModel(mockRepository)
        
        // When - checking initial state
        val initialState = freshViewModel.state.first()
        
        // Then - should be idle
        assertEquals(ProfileState.Idle, initialState)
    }
    
    @Test
    fun `loadProfile should fetch and update profile`() = runTest {
        // Given - mocked successful response
        coEvery { mockRepository.getProfile(testUserId) } returns Result.success(sampleProfile)
        
        // When - loading profile
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update profile
        val profile = viewModel.profile.value
        assertNotNull(profile)
        assertEquals("Ana", profile?.firstName)
        assertEquals(25, profile?.age)
        assertEquals(2, profile?.photos?.size)
        
        // And - state should be ProfileLoaded
        assertTrue(viewModel.state.value is ProfileState.ProfileLoaded)
    }
    
    @Test
    fun `updateProfile should call repository and update local profile`() = runTest {
        // Given - current profile loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val updatedProfile = sampleProfile.copy(
            firstName = "Ana Clara",
            bio = "Nova biografia atualizada"
        )
        
        coEvery { mockRepository.updateProfile(testUserId, updatedProfile) } returns Result.success(updatedProfile)
        
        // When - updating profile
        viewModel.handleIntent(ProfileIntent.UpdateProfile(updatedProfile))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.updateProfile(testUserId, updatedProfile) }
        
        // And - local profile should be updated
        val profile = viewModel.profile.value
        assertEquals("Ana Clara", profile?.firstName)
        assertEquals("Nova biografia atualizada", profile?.bio)
        
        // And - state should be ProfileUpdated
        assertTrue(viewModel.state.value is ProfileState.ProfileUpdated)
    }
    
    @Test
    fun `uploadPhoto should call repository and update photos list`() = runTest {
        // Given - current profile loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val mockFile = mockk<File>()
        val newPhoto = ProfilePhoto("3", "https://example.com/photo3.jpg", 2)
        
        coEvery { mockRepository.uploadProfilePhoto(testUserId, mockFile, 2) } returns Result.success(newPhoto)
        
        // When - uploading photo
        viewModel.handleIntent(ProfileIntent.UploadPhoto(mockFile, 2))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.uploadProfilePhoto(testUserId, mockFile, 2) }
        
        // And - state should be PhotoUploaded
        assertTrue(viewModel.state.value is ProfileState.PhotoUploaded)
    }
    
    @Test
    fun `deletePhoto should call repository and remove photo from list`() = runTest {
        // Given - current profile loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val photoToDelete = sampleProfile.photos[0]
        coEvery { mockRepository.deleteProfilePhoto(testUserId, photoToDelete.id) } returns Result.success(Unit)
        
        // When - deleting photo
        viewModel.handleIntent(ProfileIntent.DeletePhoto(photoToDelete.id))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.deleteProfilePhoto(testUserId, photoToDelete.id) }
        
        // And - state should be PhotoDeleted
        assertTrue(viewModel.state.value is ProfileState.PhotoDeleted)
    }
    
    @Test
    fun `reorderPhotos should call repository`() = runTest {
        // Given - current profile loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val photoOrders = listOf(
            Pair("1", 1),
            Pair("2", 0)
        )
        
        coEvery { mockRepository.reorderPhotos(testUserId, photoOrders) } returns Result.success(Unit)
        
        // When - reordering photos
        viewModel.handleIntent(ProfileIntent.ReorderPhotos(photoOrders))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.reorderPhotos(testUserId, photoOrders) }
        
        // And - state should be PhotosReordered
        assertTrue(viewModel.state.value is ProfileState.PhotosReordered)
    }
    
    @Test
    fun `loadAvailableInterests should fetch and update interests`() = runTest {
        // Given - mocked interests
        coEvery { mockRepository.getAvailableInterests() } returns Result.success(sampleInterests)
        
        // When - loading interests
        viewModel.handleIntent(ProfileIntent.LoadAvailableInterests)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update available interests
        val interests = viewModel.availableInterests.value
        assertEquals(4, interests.size)
        assertEquals("Viajar", interests[0].name)
        assertEquals("Hobbies", interests[0].category)
        
        // And - state should be InterestsLoaded
        assertTrue(viewModel.state.value is ProfileState.InterestsLoaded)
    }
    
    @Test
    fun `updateInterests should call repository and update local interests`() = runTest {
        // Given - profile and interests loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        viewModel.handleIntent(ProfileIntent.LoadAvailableInterests)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val selectedInterestIds = listOf("1", "2", "4")
        coEvery { mockRepository.updateUserInterests(testUserId, selectedInterestIds) } returns Result.success(Unit)
        
        // When - updating interests
        viewModel.handleIntent(ProfileIntent.UpdateInterests(selectedInterestIds))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.updateUserInterests(testUserId, selectedInterestIds) }
        
        // And - state should be InterestsUpdated
        assertTrue(viewModel.state.value is ProfileState.InterestsUpdated)
    }
    
    @Test
    fun `loadProfileStats should fetch and update stats`() = runTest {
        // Given - mocked stats
        coEvery { mockRepository.getProfileStats(testUserId) } returns Result.success(sampleStats)
        
        // When - loading stats
        viewModel.handleIntent(ProfileIntent.LoadProfileStats(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should update stats
        val stats = viewModel.profileStats.value
        assertNotNull(stats)
        assertEquals(85, stats?.profileCompletion)
        assertEquals(150, stats?.profileViews)
        assertEquals(45, stats?.likes)
        assertEquals(12, stats?.matches)
        
        // And - state should be StatsLoaded
        assertTrue(viewModel.state.value is ProfileState.StatsLoaded)
    }
    
    @Test
    fun `updatePrivacySettings should call repository`() = runTest {
        // Given - current profile loaded
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        coEvery { 
            mockRepository.updatePrivacySettings(testUserId, true, false, true, "public") 
        } returns Result.success(Unit)
        
        // When - updating privacy settings
        viewModel.handleIntent(
            ProfileIntent.UpdatePrivacySettings(
                showAge = true,
                showDistance = false,
                showOnlineStatus = true,
                profileVisibility = "public"
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository should be called
        coVerify { mockRepository.updatePrivacySettings(testUserId, true, false, true, "public") }
        
        // And - state should be PrivacyUpdated
        assertTrue(viewModel.state.value is ProfileState.PrivacyUpdated)
    }
    
    @Test
    fun `canUploadMorePhotos should return false when limit reached`() = runTest {
        // Given - profile with 6 photos (max limit)
        val profileWithMaxPhotos = sampleProfile.copy(
            photos = (1..6).map { 
                ProfilePhoto(it.toString(), "url$it", it - 1) 
            }
        )
        
        coEvery { mockRepository.getProfile(testUserId) } returns Result.success(profileWithMaxPhotos)
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if can upload more photos
        val canUpload = viewModel.canUploadMorePhotos()
        
        // Then - should return false
        assertFalse(canUpload)
    }
    
    @Test
    fun `canUploadMorePhotos should return true when under limit`() = runTest {
        // Given - profile with 2 photos (under limit)
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if can upload more photos
        val canUpload = viewModel.canUploadMorePhotos()
        
        // Then - should return true (2 < 6)
        assertTrue(canUpload)
    }
    
    @Test
    fun `isProfileComplete should return true for complete profile`() = runTest {
        // Given - complete profile with photos, bio and interests
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if profile is complete
        val isComplete = viewModel.isProfileComplete()
        
        // Then - should return true
        assertTrue(isComplete) // Has photos, bio, and 2 interests (>= 3 required in test)
    }
    
    @Test
    fun `isProfileComplete should return false for incomplete profile`() = runTest {
        // Given - incomplete profile
        val incompleteProfile = sampleProfile.copy(
            photos = emptyList(),
            bio = null,
            interests = emptyList()
        )
        
        coEvery { mockRepository.getProfile(testUserId) } returns Result.success(incompleteProfile)
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - checking if profile is complete
        val isComplete = viewModel.isProfileComplete()
        
        // Then - should return false
        assertFalse(isComplete)
    }
    
    @Test
    fun `getMainPhoto should return main photo`() = runTest {
        // Given - profile with main photo
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting main photo
        val mainPhoto = viewModel.getMainPhoto()
        
        // Then - should return the main photo
        assertNotNull(mainPhoto)
        assertEquals("1", mainPhoto?.id)
        assertTrue(mainPhoto?.isMainPhoto == true)
    }
    
    @Test
    fun `getSelectedInterestsCount should return correct count`() = runTest {
        // Given - profile with selected interests
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - getting selected interests count
        val count = viewModel.getSelectedInterestsCount()
        
        // Then - should return correct count (2 selected in sample)
        assertEquals(2, count)
    }
    
    @Test
    fun `error in repository should update state to error`() = runTest {
        // Given - repository that returns error
        val errorMessage = "Failed to load profile"
        coEvery { mockRepository.getProfile(testUserId) } returns Result.failure(Exception(errorMessage))
        
        // When - loading profile
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - state should be Error
        val state = viewModel.state.value
        assertTrue(state is ProfileState.Error)
        assertEquals(errorMessage, (state as ProfileState.Error).message)
    }
    
    @Test
    fun `startEditMode should update state to EditMode`() {
        // When - starting edit mode
        viewModel.handleIntent(ProfileIntent.StartEditMode)
        
        // Then - state should be EditMode
        assertEquals(ProfileState.EditMode, viewModel.state.value)
    }
    
    @Test
    fun `cancelEditMode should update state to ViewMode`() {
        // Given - in edit mode
        viewModel.handleIntent(ProfileIntent.StartEditMode)
        assertEquals(ProfileState.EditMode, viewModel.state.value)
        
        // When - canceling edit mode
        viewModel.handleIntent(ProfileIntent.CancelEditMode)
        
        // Then - state should be ViewMode
        assertEquals(ProfileState.ViewMode, viewModel.state.value)
    }
    
    @Test
    fun `clearError should reset state to idle`() = runTest {
        // Given - error state
        coEvery { mockRepository.getProfile(testUserId) } returns Result.failure(Exception("Error"))
        viewModel.handleIntent(ProfileIntent.LoadProfile(testUserId))
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value is ProfileState.Error)
        
        // When - clearing error
        viewModel.handleIntent(ProfileIntent.ClearError)
        
        // Then - state should be Idle
        assertEquals(ProfileState.Idle, viewModel.state.value)
    }
}