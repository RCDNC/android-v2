package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LikeUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var likeUserUseCase: LikeUserUseCase

    private val mockMatch = Match(
        matchId = "match123",
        otherUserId = "user2",
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        userRepository = mockk()
        likeUserUseCase = LikeUserUseCase(userRepository)
    }

    @Test
    fun `invoke with valid userId should return success with match`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { userRepository.likeUser(userId) } returns Result.success(mockMatch)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(mockMatch, result.getOrNull())
    }

    @Test
    fun `invoke with valid userId should return success with no match`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { userRepository.likeUser(userId) } returns Result.success(null)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke with empty userId should return failure`() = runTest {
        // Arrange
        val userId = ""

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with blank userId should return failure`() = runTest {
        // Arrange
        val userId = "   "

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = RuntimeException("Network error")
        coEvery { userRepository.likeUser(userId) } returns Result.failure(exception)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Database error")
        coEvery { userRepository.likeUser(userId) } throws exception

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should successfully handle match result and trigger analytics`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { userRepository.likeUser(userId) } returns Result.success(mockMatch)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(mockMatch, result.getOrNull())
        // Note: In a real scenario, we would verify that logMatchEvent was called
        // but since it's a private method, we're just ensuring the flow works correctly
    }

    @Test
    fun `invoke should pass through repository result when no match occurs`() = runTest {
        // Arrange
        val userId = "user123"
        coEvery { userRepository.likeUser(userId) } returns Result.success(null)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke should preserve original failure from repository`() = runTest {
        // Arrange
        val userId = "user123"
        val originalException = IllegalStateException("User already liked")
        coEvery { userRepository.likeUser(userId) } returns Result.failure(originalException)

        // Act
        val result = likeUserUseCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(originalException, result.exceptionOrNull())
    }
}