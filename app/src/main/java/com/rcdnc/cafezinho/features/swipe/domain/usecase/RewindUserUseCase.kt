package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import javax.inject.Inject

class RewindUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserProfile?> {
        return try {
            userRepository.rewindLastAction()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}