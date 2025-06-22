package com.rcdnc.cafezinho.features.profile.data.remote

import com.rcdnc.cafezinho.features.profile.data.remote.dto.ProfileDto
import com.rcdnc.cafezinho.features.profile.data.remote.dto.ProfileUpdateDto
import com.rcdnc.cafezinho.features.profile.data.remote.dto.InterestDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para Profile
 * Baseado na análise da API Laravel e funcionalidades legacy
 */
interface ProfileApiService {
    
    companion object {
        private const val BASE_PATH = "api"
    }
    
    /**
     * Obter perfil do usuário
     * GET /api/user/{userId}
     */
    @GET("$BASE_PATH/user/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: Int
    ): Response<ProfileResponse>
    
    /**
     * Atualizar dados do perfil
     * PATCH /api/user/{userId}
     */
    @PATCH("$BASE_PATH/user/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Int,
        @Body profileUpdate: ProfileUpdateDto
    ): Response<ProfileUpdateResponse>
    
    /**
     * Upload de foto do perfil
     * POST /api/user/{userId}/photo
     */
    @Multipart
    @POST("$BASE_PATH/user/{userId}/photo")
    suspend fun uploadProfilePhoto(
        @Path("userId") userId: Int,
        @Part photo: MultipartBody.Part,
        @Part("order_sequence") orderSequence: Int
    ): Response<PhotoUploadResponse>
    
    /**
     * Deletar foto do perfil
     * DELETE /api/user/{userId}/photo/{photoId}
     */
    @DELETE("$BASE_PATH/user/{userId}/photo/{photoId}")
    suspend fun deleteProfilePhoto(
        @Path("userId") userId: Int,
        @Path("photoId") photoId: String
    ): Response<PhotoDeleteResponse>
    
    /**
     * Reordenar fotos do perfil
     * PUT /api/user/{userId}/photos/reorder
     */
    @PUT("$BASE_PATH/user/{userId}/photos/reorder")
    suspend fun reorderPhotos(
        @Path("userId") userId: Int,
        @Body photoOrders: List<PhotoOrderDto>
    ): Response<PhotoReorderResponse>
    
    /**
     * Obter lista de interesses disponíveis
     * GET /api/interests
     */
    @GET("$BASE_PATH/interests")
    suspend fun getAvailableInterests(): Response<InterestsResponse>
    
    /**
     * Atualizar interesses do usuário
     * PUT /api/user/{userId}/interests
     */
    @PUT("$BASE_PATH/user/{userId}/interests")
    suspend fun updateUserInterests(
        @Path("userId") userId: Int,
        @Body interests: List<String>
    ): Response<InterestsUpdateResponse>
    
    /**
     * Obter estatísticas do perfil
     * GET /api/user/{userId}/stats
     */
    @GET("$BASE_PATH/user/{userId}/stats")
    suspend fun getProfileStats(
        @Path("userId") userId: Int
    ): Response<ProfileStatsResponse>
    
    /**
     * Atualizar configurações de privacidade
     * PUT /api/user/{userId}/privacy
     */
    @PUT("$BASE_PATH/user/{userId}/privacy")
    suspend fun updatePrivacySettings(
        @Path("userId") userId: Int,
        @Body privacySettings: PrivacySettingsDto
    ): Response<PrivacyUpdateResponse>
}

// Response data classes
data class ProfileResponse(
    val success: Boolean,
    val data: ProfileDto,
    val message: String? = null
)

data class ProfileUpdateResponse(
    val success: Boolean,
    val data: ProfileDto? = null,
    val message: String? = null
)

data class PhotoUploadResponse(
    val success: Boolean,
    val data: PhotoDto? = null,
    val message: String? = null
)

data class PhotoDeleteResponse(
    val success: Boolean,
    val message: String
)

data class PhotoReorderResponse(
    val success: Boolean,
    val message: String? = null
)

data class InterestsResponse(
    val success: Boolean,
    val data: List<InterestDto>,
    val message: String? = null
)

data class InterestsUpdateResponse(
    val success: Boolean,
    val message: String? = null
)

data class ProfileStatsResponse(
    val success: Boolean,
    val data: ProfileStatsDto,
    val message: String? = null
)

data class PrivacyUpdateResponse(
    val success: Boolean,
    val message: String? = null
)

// DTOs for requests
data class PhotoOrderDto(
    val photoId: String,
    val orderSequence: Int
)

data class PrivacySettingsDto(
    val showAge: Boolean,
    val showDistance: Boolean,
    val showOnlineStatus: Boolean,
    val profileVisibility: String // "public", "private", "matches_only"
)

data class PhotoDto(
    val id: String,
    val url: String,
    val orderSequence: Int,
    val uploadedAt: String
)

data class ProfileStatsDto(
    val profileCompletion: Int,
    val totalPhotos: Int,
    val totalInterests: Int,
    val profileViews: Int,
    val likes: Int
)