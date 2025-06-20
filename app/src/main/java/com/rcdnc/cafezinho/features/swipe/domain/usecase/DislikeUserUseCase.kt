package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import javax.inject.Inject

class DislikeUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return try {
            userRepository.dislikeUser(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}