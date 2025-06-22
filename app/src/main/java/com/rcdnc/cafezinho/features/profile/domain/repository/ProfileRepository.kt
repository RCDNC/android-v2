package com.rcdnc.cafezinho.features.profile.domain.repository

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.model.ProfilePhoto
import com.rcdnc.cafezinho.features.profile.domain.model.Interest
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Repository interface para Profile
 * Baseado na API Laravel e funcionalidades legacy
 */
interface ProfileRepository {
    
    /**
     * Obter perfil do usuário
     */
    suspend fun getProfile(userId: String): Result<ProfileData>
    
    /**
     * Atualizar dados do perfil
     */
    suspend fun updateProfile(userId: String, profile: ProfileData): Result<ProfileData>
    
    /**
     * Upload de foto do perfil
     */
    suspend fun uploadProfilePhoto(
        userId: String, 
        photoFile: File, 
        orderSequence: Int = 0
    ): Result<ProfilePhoto>
    
    /**
     * Deletar foto do perfil
     */
    suspend fun deleteProfilePhoto(userId: String, photoId: String): Result<Unit>
    
    /**
     * Reordenar fotos do perfil
     */
    suspend fun reorderPhotos(userId: String, photoOrders: List<Pair<String, Int>>): Result<Unit>
    
    /**
     * Obter lista de interesses disponíveis
     */
    suspend fun getAvailableInterests(): Result<List<Interest>>
    
    /**
     * Atualizar interesses do usuário
     */
    suspend fun updateUserInterests(userId: String, interestIds: List<String>): Result<Unit>
    
    /**
     * Obter estatísticas do perfil
     */
    suspend fun getProfileStats(userId: String): Result<ProfileStats>
    
    /**
     * Atualizar configurações de privacidade
     */
    suspend fun updatePrivacySettings(
        userId: String,
        showAge: Boolean,
        showDistance: Boolean,
        showOnlineStatus: Boolean,
        profileVisibility: String
    ): Result<Unit>
    
    /**
     * Observar mudanças no perfil (para cache local)
     */
    fun observeProfile(userId: String): Flow<ProfileData?>
}

/**
 * Estatísticas do perfil
 */
data class ProfileStats(
    val profileCompletion: Int,
    val totalPhotos: Int,
    val totalInterests: Int,
    val profileViews: Int,
    val likes: Int,
    val superLikes: Int,
    val matches: Int,
    val boostRemaining: Int = 0
)