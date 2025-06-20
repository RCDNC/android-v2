package com.rcdnc.cafezinho.features.profile.domain.repository

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData

interface ProfileRepository {
    suspend fun getProfile(userId: String): Result<ProfileData>
    suspend fun updateProfile(profile: ProfileData): Result<ProfileData>
    suspend fun uploadProfileImage(imageBytes: ByteArray): Result<String>
    suspend fun deleteProfileImage(imageUrl: String): Result<Unit>
}