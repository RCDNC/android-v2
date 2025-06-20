package com.rcdnc.cafezinho.domain.repository

import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): Result<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun updatePreferences(preferences: UserPreferences): Result<UserPreferences>
    suspend fun getNearbyUsers(limit: Int = 10): Result<List<User>>
    suspend fun getUserById(userId: String): Result<User?>
    fun observeCurrentUser(): Flow<User?>
    suspend fun uploadPhoto(photoData: ByteArray): Result<String>
    suspend fun deletePhoto(photoUrl: String): Result<Unit>
}