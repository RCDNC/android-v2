package com.rcdnc.cafezinho.features.matches.domain.usecase

import com.rcdnc.cafezinho.features.matches.domain.model.MatchData
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetMatchesUseCaseTest {

    private lateinit var matchRepository: MatchRepository
    private lateinit var getMatchesUseCase: GetMatchesUseCase

    private val oldMatch = MatchData(
        matchId = "match1",
        userId = "user1",
        userName = "Ana",
        userPhoto = "photo1.jpg",
        timestamp = 1000000L,
        lastMessage = "Oi!",
        isNewMatch = false
    )

    private val newMatch = MatchData(
        matchId = "match2",
        userId = "user2",
        userName = "Bruno",
        userPhoto = "photo2.jpg",
        timestamp = 2000000L,
        lastMessage = "Olá!",
        isNewMatch = true
    )

    private val recentOldMatch = MatchData(
        matchId = "match3",
        userId = "user3",
        userName = "Carlos",
        userPhoto = "photo3.jpg",
        timestamp = 3000000L,
        lastMessage = "Tudo bem?",
        isNewMatch = false
    )

    private val invalidMatch = MatchData(
        matchId = "",
        userId = "",
        userName = "Invalid",
        userPhoto = null,
        timestamp = 1500000L,
        lastMessage = null,
        isNewMatch = false
    )

    @Before
    fun setup() {
        matchRepository = mockk()
        getMatchesUseCase = GetMatchesUseCase(matchRepository)
    }

    @Test
    fun `invoke should return sorted matches with new matches first`() = runTest {
        // Arrange
        val matches = listOf(oldMatch, newMatch, recentOldMatch)
        every { matchRepository.getMatches() } returns flowOf(Result.success(matches))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isSuccess)
        val sortedMatches = result.getOrThrow()
        assertEquals(3, sortedMatches.size)
        
        // New match should be first
        assertEquals(newMatch, sortedMatches[0])
        // Then old matches sorted by timestamp (most recent first)
        assertEquals(recentOldMatch, sortedMatches[1])
        assertEquals(oldMatch, sortedMatches[2])
    }

    @Test
    fun `invoke should filter out invalid matches`() = runTest {
        // Arrange
        val matches = listOf(oldMatch, invalidMatch, newMatch)
        every { matchRepository.getMatches() } returns flowOf(Result.success(matches))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isSuccess)
        val filteredMatches = result.getOrThrow()
        assertEquals(2, filteredMatches.size)
        assertFalse(filteredMatches.contains(invalidMatch))
    }

    @Test
    fun `invoke should handle empty matches list`() = runTest {
        // Arrange
        every { matchRepository.getMatches() } returns flowOf(Result.success(emptyList()))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isSuccess)
        val matches = result.getOrThrow()
        assertTrue(matches.isEmpty())
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        every { matchRepository.getMatches() } returns flowOf(Result.failure(exception))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle repository exception in flow`() = runTest {
        // Arrange
        val exception = Exception("Database error")
        every { matchRepository.getMatches() } returns flow {
            throw exception
        }

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `sorting should prioritize new matches regardless of timestamp`() = runTest {
        // Arrange - new match with older timestamp should still come first
        val oldNewMatch = newMatch.copy(timestamp = 500000L) // Older than oldMatch
        val matches = listOf(oldMatch, recentOldMatch, oldNewMatch)
        every { matchRepository.getMatches() } returns flowOf(Result.success(matches))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isSuccess)
        val sortedMatches = result.getOrThrow()
        
        // New match should be first despite older timestamp
        assertEquals(oldNewMatch, sortedMatches[0])
        assertEquals(recentOldMatch, sortedMatches[1])
        assertEquals(oldMatch, sortedMatches[2])
    }

    @Test
    fun `filtering should require both matchId and userId to be non-blank`() = runTest {
        // Arrange
        val matchWithEmptyId = oldMatch.copy(matchId = "")
        val matchWithEmptyUserId = oldMatch.copy(userId = "")
        val matchWithBlankId = oldMatch.copy(matchId = "   ")
        val matchWithBlankUserId = oldMatch.copy(userId = "   ")
        val validMatch = newMatch
        
        val matches = listOf(matchWithEmptyId, matchWithEmptyUserId, matchWithBlankId, matchWithBlankUserId, validMatch)
        every { matchRepository.getMatches() } returns flowOf(Result.success(matches))

        // Act
        val result = getMatchesUseCase().single()

        // Assert
        assertTrue(result.isSuccess)
        val filteredMatches = result.getOrThrow()
        assertEquals(1, filteredMatches.size)
        assertEquals(validMatch, filteredMatches[0])
    }
}