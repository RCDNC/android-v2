package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Match?> {
        return try {
            when {
                userId.isBlank() -> {
                    Result.failure(Exception("User ID cannot be empty"))
                }
                else -> {
                    val result = userRepository.likeUser(userId)
                    if (result.isSuccess) {
                        val match = result.getOrThrow()
                        if (match != null) {
                            // Log match event for analytics
                            logMatchEvent(userId, match)
                        }
                        result
                    } else {
                        result
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun logMatchEvent(userId: String, match: Match) {
        // Business logic for match logging/analytics
        // Could trigger push notifications, update match count, etc.
    }
}