package com.rcdnc.cafezinho.features.profile.domain.usecase

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetUserProfileUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private val validProfile = ProfileData(
        id = "user123",
        firstName = "Ana",
        lastName = "Silva",
        age = 25,
        bio = "Gosto de viajar e conhecer pessoas novas",
        website = "https://ana.com",
        jobTitle = "Designer",
        company = "Tech Corp",
        school = "UFMG",
        photos = listOf("photo1.jpg", "photo2.jpg"),
        interests = listOf("viagens", "música")
    )

    @Before
    fun setup() {
        profileRepository = mockk()
        getUserProfileUseCase = GetUserProfileUseCase(profileRepository)
    }

    @Test
    fun `invoke with valid userId should return success`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { profileRepository.getProfile(userId) } returns Result.success(validProfile)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(validProfile, result.getOrNull())
    }

    @Test
    fun `invoke with empty userId should return failure`() = runTest {
        // Arrange
        val userId = ""

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with blank userId should return failure`() = runTest {
        // Arrange
        val userId = "   "

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should validate profile has valid ID`() = runTest {
        // Arrange
        val userId = "user123"
        val profileWithoutId = validProfile.copy(id = "")
        coEvery { profileRepository.getProfile(userId) } returns Result.success(profileWithoutId)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid profile: missing ID", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should validate profile has valid first name`() = runTest {
        // Arrange
        val userId = "user123"
        val profileWithoutName = validProfile.copy(firstName = "")
        coEvery { profileRepository.getProfile(userId) } returns Result.success(profileWithoutName)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid profile: missing first name", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should validate profile has valid age`() = runTest {
        // Arrange
        val userId = "user123"
        val profileWithInvalidAge = validProfile.copy(age = -1)
        coEvery { profileRepository.getProfile(userId) } returns Result.success(profileWithInvalidAge)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid profile: invalid age", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should accept profile with age zero`() = runTest {
        // Arrange - age 0 might be valid for profiles without age data
        val userId = "user123"
        val profileWithZeroAge = validProfile.copy(age = 0)
        coEvery { profileRepository.getProfile(userId) } returns Result.success(profileWithZeroAge)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(profileWithZeroAge, result.getOrNull())
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = RuntimeException("Network error")
        coEvery { profileRepository.getProfile(userId) } returns Result.failure(exception)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Database error")
        coEvery { profileRepository.getProfile(userId) } throws exception

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should preserve original repository result when validation passes`() = runTest {
        // Arrange
        val userId = "user123"
        val profileWithOptionalFields = validProfile.copy(
            lastName = null,
            bio = null,
            website = null
        )
        coEvery { profileRepository.getProfile(userId) } returns Result.success(profileWithOptionalFields)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        val returnedProfile = result.getOrNull()!!
        assertEquals(profileWithOptionalFields.id, returnedProfile.id)
        assertEquals(profileWithOptionalFields.firstName, returnedProfile.firstName)
        assertNull(returnedProfile.lastName)
        assertNull(returnedProfile.bio)
        assertNull(returnedProfile.website)
    }

    @Test
    fun `invoke should handle profile not found scenario`() = runTest {
        // Arrange
        val userId = "nonexistent"
        val notFoundException = NoSuchElementException("Profile not found")
        coEvery { profileRepository.getProfile(userId) } returns Result.failure(notFoundException)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(notFoundException, result.exceptionOrNull())
    }

    @Test
    fun `invoke should validate profile from repository regardless of input userId`() = runTest {
        // Arrange - Repository returns profile with different ID than requested
        val requestedUserId = "user123"
        val returnedProfile = validProfile.copy(id = "different456")
        coEvery { profileRepository.getProfile(requestedUserId) } returns Result.success(returnedProfile)

        // Act
        val result = getUserProfileUseCase(requestedUserId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("different456", result.getOrNull()?.id)
    }

    @Test
    fun `invoke should pass through all validation checks for minimal valid profile`() = runTest {
        // Arrange
        val userId = "user123"
        val minimalProfile = ProfileData(
            id = "user123",
            firstName = "Ana",
            lastName = null,
            age = 0,
            bio = null,
            photos = emptyList(),
            interests = emptyList()
        )
        coEvery { profileRepository.getProfile(userId) } returns Result.success(minimalProfile)

        // Act
        val result = getUserProfileUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(minimalProfile, result.getOrNull())
    }
}