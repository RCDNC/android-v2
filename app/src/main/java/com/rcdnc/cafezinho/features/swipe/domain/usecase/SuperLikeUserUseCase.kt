package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import javax.inject.Inject

class SuperLikeUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Match?> {
        return try {
            userRepository.superLikeUser(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}