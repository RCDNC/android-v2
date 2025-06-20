package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetRecommendedUsersUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var getRecommendedUsersUseCase: GetRecommendedUsersUseCase

    private val validUser = UserProfile(
        id = "1",
        name = "Ana",
        age = 25,
        photos = listOf("photo1.jpg", "photo2.jpg"),
        bio = "Oi, sou a Ana!",
        interests = listOf("música", "viagens")
    )

    private val userWithoutPhotos = UserProfile(
        id = "2",
        name = "Bruno",
        age = 30,
        photos = emptyList(),
        bio = "Olá!"
    )

    private val underageUser = UserProfile(
        id = "3",
        name = "Carlos",
        age = 17,
        photos = listOf("photo.jpg"),
        bio = "Menor de idade"
    )

    private val userWithBlankPhoto = UserProfile(
        id = "4",
        name = "Diana",
        age = 28,
        photos = listOf("", "  "),
        bio = "Fotos em branco"
    )

    private val oldUser = UserProfile(
        id = "5",
        name = "Eduardo",
        age = 100,
        photos = listOf("photo.jpg"),
        bio = "Muito velho"
    )

    @Before
    fun setup() {
        userRepository = mockk()
        getRecommendedUsersUseCase = GetRecommendedUsersUseCase(userRepository)
    }

    @Test
    fun `invoke should return filtered valid users`() = runTest {
        // Arrange
        val allUsers = listOf(validUser, userWithoutPhotos, underageUser, userWithBlankPhoto, oldUser)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(allUsers)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertEquals(1, filteredUsers.size)
        assertEquals(validUser, filteredUsers[0])
    }

    @Test
    fun `invoke should filter out users without valid photos`() = runTest {
        // Arrange
        val usersWithInvalidPhotos = listOf(userWithoutPhotos, userWithBlankPhoto)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(usersWithInvalidPhotos)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertTrue(filteredUsers.isEmpty())
    }

    @Test
    fun `invoke should filter out underage users`() = runTest {
        // Arrange
        val users = listOf(underageUser)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(users)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertTrue(filteredUsers.isEmpty())
    }

    @Test
    fun `invoke should filter out users over 99 years old`() = runTest {
        // Arrange
        val users = listOf(oldUser)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(users)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertTrue(filteredUsers.isEmpty())
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { userRepository.getRecommendedUsers() } returns Result.failure(exception)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val exception = Exception("Database error")
        coEvery { userRepository.getRecommendedUsers() } throws exception

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return empty list when all users are filtered out`() = runTest {
        // Arrange
        val invalidUsers = listOf(userWithoutPhotos, underageUser, oldUser, userWithBlankPhoto)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(invalidUsers)

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertTrue(filteredUsers.isEmpty())
    }

    @Test
    fun `business rules should accept user with exactly 18 years`() = runTest {
        // Arrange
        val user18 = validUser.copy(age = 18)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(listOf(user18))

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertEquals(1, filteredUsers.size)
        assertEquals(user18, filteredUsers[0])
    }

    @Test
    fun `business rules should accept user with exactly 99 years`() = runTest {
        // Arrange
        val user99 = validUser.copy(age = 99)
        coEvery { userRepository.getRecommendedUsers() } returns Result.success(listOf(user99))

        // Act
        val result = getRecommendedUsersUseCase()

        // Assert
        assertTrue(result.isSuccess)
        val filteredUsers = result.getOrThrow()
        assertEquals(1, filteredUsers.size)
        assertEquals(user99, filteredUsers[0])
    }
}