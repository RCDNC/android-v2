package com.rcdnc.cafezinho.features.auth.domain.model

import java.util.*

/**
 * User domain model representing authenticated user data
 * Used across authentication and profile features
 */
data class User(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val isVerified: Boolean = false,
    val isProfileComplete: Boolean = false,
    val token: String? = null,
    val refreshToken: String? = null,
    
    // Profile fields for completion wizard
    val dateOfBirth: Date? = null,
    val gender: String? = null,
    val genderPreference: String? = null,
    val bio: String? = null,
    val school: String? = null,
    val profilePhotoUrl: String? = null,
    val profilePhotos: List<String> = emptyList(),
    
    // Social auth data
    val socialId: String? = null,
    val socialType: SocialAuthType? = null,
    
    // App settings
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = false,
    val themeMode: String = "system",
    
    // Timestamps
    val createdAt: Date? = null,
    val lastLoginAt: Date? = null
)

/**
 * Supported social authentication providers
 */
enum class SocialAuthType {
    GOOGLE,
    FACEBOOK
}

/**
 * Profile completion steps for registration wizard
 */
enum class ProfileStep {
    FIRST_NAME,
    LAST_NAME,
    DATE_OF_BIRTH,
    GENDER,
    GENDER_PREFERENCE,
    BIO,
    SCHOOL,
    PHOTOS,
    COMPLETION;
    
    fun next(): ProfileStep? {
        return when (this) {
            FIRST_NAME -> LAST_NAME
            LAST_NAME -> DATE_OF_BIRTH
            DATE_OF_BIRTH -> GENDER
            GENDER -> GENDER_PREFERENCE
            GENDER_PREFERENCE -> BIO
            BIO -> SCHOOL
            SCHOOL -> PHOTOS
            PHOTOS -> COMPLETION
            COMPLETION -> null
        }
    }
    
    fun previous(): ProfileStep? {
        return when (this) {
            FIRST_NAME -> null
            LAST_NAME -> FIRST_NAME
            DATE_OF_BIRTH -> LAST_NAME
            GENDER -> DATE_OF_BIRTH
            GENDER_PREFERENCE -> GENDER
            BIO -> GENDER_PREFERENCE
            SCHOOL -> BIO
            PHOTOS -> SCHOOL
            COMPLETION -> PHOTOS
        }
    }
}

/**
 * Registration data transfer object for profile completion
 */
data class RegistrationData(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: Date? = null,
    val gender: String? = null,
    val genderPreference: String? = null,
    val bio: String = "",
    val school: String = "",
    val profilePhotos: List<String> = emptyList()
) {
    fun isStepValid(step: ProfileStep): Boolean {
        return when (step) {
            ProfileStep.FIRST_NAME -> firstName.isNotBlank()
            ProfileStep.LAST_NAME -> lastName.isNotBlank()
            ProfileStep.DATE_OF_BIRTH -> dateOfBirth != null
            ProfileStep.GENDER -> !gender.isNullOrBlank()
            ProfileStep.GENDER_PREFERENCE -> !genderPreference.isNullOrBlank()
            ProfileStep.BIO -> bio.isNotBlank()
            ProfileStep.SCHOOL -> school.isNotBlank()
            ProfileStep.PHOTOS -> profilePhotos.isNotEmpty()
            ProfileStep.COMPLETION -> isComplete()
        }
    }
    
    fun isComplete(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                dateOfBirth != null &&
                !gender.isNullOrBlank() &&
                !genderPreference.isNullOrBlank() &&
                bio.isNotBlank() &&
                school.isNotBlank() &&
                profilePhotos.isNotEmpty()
    }
}