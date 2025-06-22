package com.rcdnc.cafezinho.features.profile.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para integração com API Laravel de Profile
 * Baseado na estrutura real da API e UserDetailsModel legacy
 */

/**
 * DTO principal do Profile vindo da API Laravel
 */
data class ProfileDto(
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String? = null,
    val email: String,
    val age: Int? = null,
    val bio: String? = null,
    val gender: String? = null,
    @SerializedName("job_title")
    val jobTitle: String? = null,
    val company: String? = null,
    val school: String? = null,
    val location: String? = null,
    val distance: String? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("is_premium")
    val isPremium: Boolean = false,
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    @SerializedName("last_seen")
    val lastSeen: String? = null,
    @SerializedName("profile_completion")
    val profileCompletion: Int = 0,
    @SerializedName("sexual_orientation")
    val sexualOrientation: String? = null,
    @SerializedName("looking_for")
    val lookingFor: String? = null,
    @SerializedName("show_age")
    val showAge: Boolean = true,
    @SerializedName("show_distance")
    val showDistance: Boolean = true,
    @SerializedName("show_online_status")
    val showOnlineStatus: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    
    // Relacionamentos
    val photos: List<ProfilePhotoDto>? = null,
    val interests: List<InterestDto>? = null
)

/**
 * DTO para fotos do perfil
 */
data class ProfilePhotoDto(
    val id: String,
    val url: String,
    @SerializedName("order_sequence")
    val orderSequence: Int = 0,
    val score: Int? = null,
    @SerializedName("is_main_photo")
    val isMainPhoto: Boolean = false,
    @SerializedName("uploaded_at")
    val uploadedAt: String,
    @SerializedName("image_flags")
    val imageFlags: Int? = null
)

/**
 * DTO para interesses/paixões
 */
data class InterestDto(
    val id: String,
    val name: String,
    val category: String? = null,
    @SerializedName("is_selected")
    val isSelected: Boolean = false,
    val icon: String? = null,
    val color: String? = null
)

/**
 * DTO para atualização de perfil
 */
data class ProfileUpdateDto(
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    val bio: String? = null,
    val gender: String? = null,
    @SerializedName("job_title")
    val jobTitle: String? = null,
    val company: String? = null,
    val school: String? = null,
    @SerializedName("sexual_orientation")
    val sexualOrientation: String? = null,
    @SerializedName("looking_for")
    val lookingFor: String? = null,
    @SerializedName("show_age")
    val showAge: Boolean? = null,
    @SerializedName("show_distance")
    val showDistance: Boolean? = null,
    @SerializedName("show_online_status")
    val showOnlineStatus: Boolean? = null
)

/**
 * DTO para resposta de upload de foto
 */
data class PhotoUploadDto(
    val id: String,
    val url: String,
    @SerializedName("order_sequence")
    val orderSequence: Int,
    @SerializedName("upload_status")
    val uploadStatus: String, // "processing", "completed", "failed"
    @SerializedName("validation_score")
    val validationScore: Int? = null
)

/**
 * DTO para configurações de privacidade
 */
data class PrivacyDto(
    @SerializedName("show_age")
    val showAge: Boolean,
    @SerializedName("show_distance")
    val showDistance: Boolean,
    @SerializedName("show_online_status")
    val showOnlineStatus: Boolean,
    @SerializedName("profile_visibility")
    val profileVisibility: String, // "public", "private", "matches_only"
    @SerializedName("show_recently_active")
    val showRecentlyActive: Boolean = true
)

/**
 * DTO para estatísticas do perfil
 */
data class ProfileStatsDto(
    @SerializedName("profile_completion")
    val profileCompletion: Int,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("total_interests")
    val totalInterests: Int,
    @SerializedName("profile_views")
    val profileViews: Int,
    val likes: Int,
    @SerializedName("super_likes")
    val superLikes: Int,
    val matches: Int,
    @SerializedName("boost_remaining")
    val boostRemaining: Int = 0
)

/**
 * DTO para validação de foto (AWS Rekognition)
 */
data class PhotoValidationDto(
    @SerializedName("is_valid")
    val isValid: Boolean,
    val score: Float,
    @SerializedName("detected_faces")
    val detectedFaces: Int,
    @SerializedName("validation_errors")
    val validationErrors: List<String>? = null,
    @SerializedName("content_moderation")
    val contentModeration: ContentModerationDto? = null
)

/**
 * DTO para moderação de conteúdo
 */
data class ContentModerationDto(
    @SerializedName("is_appropriate")
    val isAppropriate: Boolean,
    @SerializedName("confidence_score")
    val confidenceScore: Float,
    @SerializedName("detected_labels")
    val detectedLabels: List<String>? = null
)