package com.rcdnc.cafezinho.domain.usecase

import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.repository.UserRepository

class GetNearbyUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<User>> {
        return try {
            val currentUser = userRepository.getCurrentUser().getOrNull()
                ?: return Result.failure(Exception("User not logged in"))
            
            val nearbyUsers = userRepository.getNearbyUsers(limit).getOrThrow()
            
            // Filter users based on preferences
            val filteredUsers = nearbyUsers.filter { user ->
                val preferences = currentUser.preferences
                if (preferences != null) {
                    user.age >= preferences.minAge && 
                    user.age <= preferences.maxAge &&
                    (preferences.interestedInGender == com.rcdnc.cafezinho.domain.model.Gender.ALL || 
                     user.id != currentUser.id) // Simple filter, can be enhanced
                } else {
                    true
                }
            }
            
            Result.success(filteredUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}