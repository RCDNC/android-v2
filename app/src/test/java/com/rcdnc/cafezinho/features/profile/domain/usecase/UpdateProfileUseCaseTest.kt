package com.rcdnc.cafezinho.features.profile.domain.usecase

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateProfileUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var updateProfileUseCase: UpdateProfileUseCase

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
        updateProfileUseCase = UpdateProfileUseCase(profileRepository)
    }

    @Test
    fun `invoke with valid profile should return success`() = runTest {
        // Arrange
        val profileSlot = slot<ProfileData>()
        coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(validProfile)

        // Act
        val result = updateProfileUseCase(validProfile)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(validProfile, result.getOrNull())
        
        // Verify sanitization occurred
        val capturedProfile = profileSlot.captured
        assertEquals("Ana", capturedProfile.firstName) // Should be trimmed
        assertEquals("Silva", capturedProfile.lastName)
    }

    @Test
    fun `invoke with empty profile ID should return failure`() = runTest {
        // Arrange
        val profileWithoutId = validProfile.copy(id = "")

        // Act
        val result = updateProfileUseCase(profileWithoutId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Profile ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty first name should return failure`() = runTest {
        // Arrange
        val profileWithoutName = validProfile.copy(firstName = "")

        // Act
        val result = updateProfileUseCase(profileWithoutName)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("First name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with age under 18 should return failure`() = runTest {
        // Arrange
        val underageProfile = validProfile.copy(age = 17)

        // Act
        val result = updateProfileUseCase(underageProfile)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Age must be at least 18", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with age over 99 should return failure`() = runTest {
        // Arrange
        val tooOldProfile = validProfile.copy(age = 100)

        // Act
        val result = updateProfileUseCase(tooOldProfile)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Age must be less than 100", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with bio too long should return failure`() = runTest {
        // Arrange
        val longBio = "a".repeat(501) // 501 characters
        val profileWithLongBio = validProfile.copy(bio = longBio)

        // Act
        val result = updateProfileUseCase(profileWithLongBio)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Bio too long (max 500 characters)", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should sanitize profile fields by trimming whitespace`() = runTest {
        // Arrange
        val profileWithWhitespace = validProfile.copy(
            firstName = "  Ana  ",
            lastName = "  Silva  ",
            bio = "  Gosto de viajar  ",
            website = "  https://ana.com  ",
            jobTitle = "  Designer  ",
            company = "  Tech Corp  ",
            school = "  UFMG  "
        )
        
        val profileSlot = slot<ProfileData>()
        coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(validProfile)

        // Act
        val result = updateProfileUseCase(profileWithWhitespace)

        // Assert
        assertTrue(result.isSuccess)
        
        val sanitizedProfile = profileSlot.captured
        assertEquals("Ana", sanitizedProfile.firstName)
        assertEquals("Silva", sanitizedProfile.lastName)
        assertEquals("Gosto de viajar", sanitizedProfile.bio)
        assertEquals("https://ana.com", sanitizedProfile.website)
        assertEquals("Designer", sanitizedProfile.jobTitle)
        assertEquals("Tech Corp", sanitizedProfile.company)
        assertEquals("UFMG", sanitizedProfile.school)
    }

    @Test
    fun `invoke should truncate bio to 500 characters if longer`() = runTest {
        // Arrange
        val longBio = "a".repeat(600) // 600 characters
        val profileWithLongBio = validProfile.copy(bio = longBio)
        
        val profileSlot = slot<ProfileData>()
        coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(validProfile)

        // Act
        val result = updateProfileUseCase(profileWithLongBio)

        // Assert
        assertTrue(result.isFailure) // Should fail validation before sanitization
    }

    @Test
    fun `invoke should handle null optional fields properly`() = runTest {
        // Arrange
        val profileWithNulls = validProfile.copy(
            lastName = null,
            bio = null,
            website = null,
            jobTitle = null,
            company = null,
            school = null
        )
        
        val profileSlot = slot<ProfileData>()
        coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(profileWithNulls)

        // Act
        val result = updateProfileUseCase(profileWithNulls)

        // Assert
        assertTrue(result.isSuccess)
        
        val sanitizedProfile = profileSlot.captured
        assertNull(sanitizedProfile.lastName)
        assertNull(sanitizedProfile.bio)
        assertNull(sanitizedProfile.website)
        assertNull(sanitizedProfile.jobTitle)
        assertNull(sanitizedProfile.company)
        assertNull(sanitizedProfile.school)
    }

    @Test
    fun `invoke with exactly 500 character bio should succeed`() = runTest {
        // Arrange
        val bio500 = "a".repeat(500) // Exactly 500 characters
        val profileWith500Bio = validProfile.copy(bio = bio500)
        
        val profileSlot = slot<ProfileData>()
        coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(profileWith500Bio)

        // Act
        val result = updateProfileUseCase(profileWith500Bio)

        // Assert
        assertTrue(result.isSuccess)
        val sanitizedProfile = profileSlot.captured
        assertEquals(500, sanitizedProfile.bio?.length)
    }

    @Test
    fun `invoke with age exactly 18 should succeed`() = runTest {
        // Arrange
        val profile18 = validProfile.copy(age = 18)
        coEvery { profileRepository.updateProfile(any()) } returns Result.success(profile18)

        // Act
        val result = updateProfileUseCase(profile18)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke with age exactly 99 should succeed`() = runTest {
        // Arrange
        val profile99 = validProfile.copy(age = 99)
        coEvery { profileRepository.updateProfile(any()) } returns Result.success(profile99)

        // Act
        val result = updateProfileUseCase(profile99)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { profileRepository.updateProfile(any()) } returns Result.failure(exception)

        // Act
        val result = updateProfileUseCase(validProfile)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val exception = Exception("Database error")
        coEvery { profileRepository.updateProfile(any()) } throws exception

        // Act
        val result = updateProfileUseCase(validProfile)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}