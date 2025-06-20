package com.rcdnc.cafezinho.features.profile.domain.usecase

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<ProfileData> {
        return try {
            when {
                userId.isBlank() -> {
                    Result.failure(Exception("User ID cannot be empty"))
                }
                else -> {
                    val result = profileRepository.getProfile(userId)
                    if (result.isSuccess) {
                        val profile = result.getOrThrow()
                        validateProfile(profile)
                    } else {
                        result
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateProfile(profile: ProfileData): Result<ProfileData> {
        return when {
            profile.id.isBlank() -> {
                Result.failure(Exception("Invalid profile: missing ID"))
            }
            profile.firstName.isBlank() -> {
                Result.failure(Exception("Invalid profile: missing first name"))
            }
            profile.age < 0 -> {
                Result.failure(Exception("Invalid profile: invalid age"))
            }
            else -> {
                Result.success(profile)
            }
        }
    }
}