package com.rcdnc.cafezinho.features.swipe.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para API de Swipe/Descobrir
 * Baseados na estrutura real da API Laravel do Cafezinho
 */

/**
 * Resposta genérica da API
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

/**
 * Resposta da busca de usuários
 */
data class SwipeUsersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<SwipeUserDto>
)

/**
 * DTO do usuário para swipe
 */
data class SwipeUserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("distance") val distance: String?,
    @SerializedName("photos") val photos: List<SwipePhotoDto>?,
    @SerializedName("interests") val interests: List<String>?,
    @SerializedName("job_title") val jobTitle: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("school") val school: String?,
    @SerializedName("is_verified") val isVerified: Boolean?,
    @SerializedName("is_premium") val isPremium: Boolean?,
    @SerializedName("is_online") val isOnline: Boolean?,
    @SerializedName("last_seen") val lastSeen: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("profile_completion") val profileCompletion: Int?,
    @SerializedName("mutual_connections") val mutualConnections: Int?,
    @SerializedName("mutual_interests") val mutualInterests: List<String>?
)

/**
 * DTO da foto do usuário
 */
data class SwipePhotoDto(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("order_sequence") val orderSequence: Int?,
    @SerializedName("is_main_photo") val isMainPhoto: Boolean?
)

/**
 * Request para ação de like/dislike/super like
 */
data class LikeActionRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("target_user_id") val targetUserId: Int,
    @SerializedName("action") val action: String, // "like", "dislike", "super_like"
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

/**
 * Resposta da ação de like
 */
data class LikeActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: LikeActionData?
)

/**
 * Dados da resposta de like action
 */
data class LikeActionData(
    @SerializedName("is_match") val isMatch: Boolean,
    @SerializedName("match_id") val matchId: String?,
    @SerializedName("match_message") val matchMessage: String?,
    @SerializedName("likes_remaining") val likesRemaining: Int?,
    @SerializedName("super_likes_remaining") val superLikesRemaining: Int?
)

/**
 * Resposta do rewind
 */
data class RewindResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: SwipeUserDto?
)

/**
 * Resposta dos consumíveis do usuário
 */
data class UserConsumablesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserConsumablesDto?
)

/**
 * DTO dos consumíveis/métricas do usuário
 */
data class UserConsumablesDto(
    @SerializedName("daily_likes_used") val dailyLikesUsed: Int,
    @SerializedName("daily_likes_limit") val dailyLikesLimit: Int,
    @SerializedName("super_likes_used") val superLikesUsed: Int,
    @SerializedName("super_likes_limit") val superLikesLimit: Int,
    @SerializedName("rewinds_used") val rewindsUsed: Int,
    @SerializedName("rewinds_limit") val rewindsLimit: Int,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("can_use_rewind") val canUseRewind: Boolean,
    @SerializedName("can_use_super_like") val canUseSuperLike: Boolean
)

/**
 * DTO para preferências do usuário
 */
data class UserPreferencesDto(
    @SerializedName("min_age") val minAge: Int,
    @SerializedName("max_age") val maxAge: Int,
    @SerializedName("max_distance") val maxDistance: Int,
    @SerializedName("gender_preference") val genderPreference: String,
    @SerializedName("show_online_only") val showOnlineOnly: Boolean,
    @SerializedName("show_verified_only") val showVerifiedOnly: Boolean,
    @SerializedName("required_interests") val requiredInterests: List<String>?
)

/**
 * Resposta das preferências do usuário
 */
data class UserPreferencesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserPreferencesDto?
)

/**
 * Request para marcar usuário como visualizado
 */
data class ViewUserRequest(
    @SerializedName("viewer_id") val viewerId: Int,
    @SerializedName("viewed_user_id") val viewedUserId: Int
)

/**
 * Request para reportar usuário
 */
data class ReportUserRequest(
    @SerializedName("reporter_id") val reporterId: Int,
    @SerializedName("reported_user_id") val reportedUserId: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("description") val description: String? = null
)

/**
 * Resposta de detalhes do usuário
 */
data class UserDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: SwipeUserDto?
)