package com.rcdnc.cafezinho.features.profile.domain.model

/**
 * Modelo de dados do perfil do usuário
 * Baseado em UserDetailsModel.kt do legacy e análise da API
 */
data class ProfileData(
    val id: String,
    val firstName: String,
    val lastName: String? = null,
    val age: Int,
    val bio: String? = null,
    val gender: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val school: String? = null,
    val photos: List<ProfilePhoto> = emptyList(),
    val interests: List<Interest> = emptyList(),
    val location: String? = null,
    val distance: String? = null,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val isOnline: Boolean = false,
    val lastSeen: Long? = null,
    val profileCompletion: Int = 0, // Porcentagem 0-100
    val sexualOrientation: String? = null,
    val lookingFor: String? = null,
    val showAge: Boolean = true,
    val showDistance: Boolean = true,
    val showOnlineStatus: Boolean = true
)

/**
 * Modelo para fotos do perfil
 * Baseado em UserImageModel.kt
 */
data class ProfilePhoto(
    val id: String,
    val url: String,
    val orderSequence: Int = 0,
    val score: Int? = null,
    val isMainPhoto: Boolean = false,
    val uploadedAt: Long = System.currentTimeMillis()
)

/**
 * Modelo para interesses/paixões
 * Baseado em PassionsModel.kt
 */
data class Interest(
    val id: String,
    val name: String,
    val category: String? = null,
    val isSelected: Boolean = false
)

/**
 * Enum para tipos de mídia de perfil
 */
enum class MediaType {
    PHOTO,
    VIDEO,
    INSTAGRAM
}

/**
 * Enum para privacidade de campos
 */
enum class PrivacySetting {
    PUBLIC,
    PRIVATE,
    MATCHES_ONLY
}