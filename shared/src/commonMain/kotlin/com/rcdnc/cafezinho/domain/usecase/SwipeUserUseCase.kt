package com.rcdnc.cafezinho.domain.usecase

import com.rcdnc.cafezinho.domain.model.Match
import com.rcdnc.cafezinho.domain.model.SwipeAction
import com.rcdnc.cafezinho.domain.model.SwipeType
import com.rcdnc.cafezinho.domain.repository.MatchRepository
import com.rcdnc.cafezinho.domain.repository.UserRepository
import kotlinx.datetime.Clock

class SwipeUserUseCase(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        targetUserId: String, 
        swipeType: SwipeType
    ): Result<SwipeResult> {
        return try {
            val currentUser = userRepository.getCurrentUser().getOrNull()
                ?: return Result.failure(Exception("User not logged in"))
            
            val swipeAction = SwipeAction(
                userId = currentUser.id,
                targetUserId = targetUserId,
                action = swipeType,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                isSuperLike = swipeType == SwipeType.SUPER_LIKE
            )
            
            val match = matchRepository.swipeUser(swipeAction).getOrThrow()
            
            val result = if (match != null) {
                SwipeResult.Match(match)
            } else {
                when (swipeType) {
                    SwipeType.LIKE, SwipeType.SUPER_LIKE -> SwipeResult.LikeSent
                    SwipeType.PASS -> SwipeResult.Passed
                }
            }
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

sealed class SwipeResult {
    data class Match(val match: com.rcdnc.cafezinho.domain.model.Match) : SwipeResult()
    data object LikeSent : SwipeResult()
    data object Passed : SwipeResult()
}