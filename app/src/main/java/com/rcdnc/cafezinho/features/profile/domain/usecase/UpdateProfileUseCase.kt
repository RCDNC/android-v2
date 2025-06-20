package com.rcdnc.cafezinho.features.profile.domain.usecase

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: ProfileData): Result<ProfileData> {
        return try {
            when {
                profile.id.isBlank() -> {
                    Result.failure(Exception("Profile ID cannot be empty"))
                }
                profile.firstName.isBlank() -> {
                    Result.failure(Exception("First name cannot be empty"))
                }
                profile.age < 18 -> {
                    Result.failure(Exception("Age must be at least 18"))
                }
                profile.age > 99 -> {
                    Result.failure(Exception("Age must be less than 100"))
                }
                profile.bio?.length ?: 0 > 500 -> {
                    Result.failure(Exception("Bio too long (max 500 characters)"))
                }
                else -> {
                    val sanitizedProfile = sanitizeProfile(profile)
                    profileRepository.updateProfile(sanitizedProfile)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sanitizeProfile(profile: ProfileData): ProfileData {
        return profile.copy(
            firstName = profile.firstName.trim(),
            lastName = profile.lastName?.trim(),
            bio = profile.bio?.trim()?.take(500),
            website = profile.website?.trim(),
            jobTitle = profile.jobTitle?.trim(),
            company = profile.company?.trim(),
            school = profile.school?.trim()
        )
    }
}