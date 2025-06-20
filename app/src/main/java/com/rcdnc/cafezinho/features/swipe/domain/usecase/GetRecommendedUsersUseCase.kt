package com.rcdnc.cafezinho.features.swipe.domain.usecase

import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRecommendedUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<UserProfile>> {
        return try {
            val result = userRepository.getRecommendedUsers()
            if (result.isSuccess) {
                val users = result.getOrThrow()
                val filteredUsers = applyBusinessRules(users)
                Result.success(filteredUsers)
            } else {
                result
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun applyBusinessRules(users: List<UserProfile>): List<UserProfile> {
        return users.filter { user ->
            isValidUser(user) && hasValidPhotos(user) && meetsAgeRequirements(user)
        }
    }

    private fun isValidUser(user: UserProfile): Boolean {
        return user.id.isNotBlank() && 
               user.name.isNotBlank() && 
               user.age > 0
    }

    private fun hasValidPhotos(user: UserProfile): Boolean {
        return user.photos.isNotEmpty() && 
               user.photos.any { it.isNotBlank() }
    }

    private fun meetsAgeRequirements(user: UserProfile): Boolean {
        return user.age in 18..99
    }
}