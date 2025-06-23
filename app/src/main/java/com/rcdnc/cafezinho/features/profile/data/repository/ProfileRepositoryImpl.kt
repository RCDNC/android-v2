package com.rcdnc.cafezinho.features.profile.data.repository

import com.rcdnc.cafezinho.features.profile.data.remote.ProfileApiService
import com.rcdnc.cafezinho.features.profile.data.remote.dto.*
import com.rcdnc.cafezinho.features.profile.domain.model.*
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do ProfileRepository integrado com API Laravel
 * Baseado na análise da API real do Cafezinho
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService
) : ProfileRepository {
    
    // Cache local para observar mudanças
    private val _profileCache = MutableStateFlow<ProfileData?>(null)
    
    override suspend fun getProfile(userId: String): Result<ProfileData> {
        return try {
            val response = profileApiService.getUserProfile(userId.toInt())
            
            if (response.isSuccessful) {
                val profileResponse = response.body()
                
                if (profileResponse?.success == true) {
                    val profileData = profileResponse.data.toDomainModel()
                    _profileCache.value = profileData
                    Result.success(profileData)
                } else {
                    Result.failure(Exception(profileResponse?.message ?: "Erro ao carregar perfil"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfile(userId: String, profile: ProfileData): Result<ProfileData> {
        return try {
            val updateDto = ProfileUpdateDto(
                firstName = profile.firstName,
                lastName = profile.lastName,
                bio = profile.bio,
                gender = profile.gender,
                jobTitle = profile.jobTitle,
                company = profile.company,
                school = profile.school,
                sexualOrientation = profile.sexualOrientation,
                lookingFor = profile.lookingFor,
                showAge = profile.showAge,
                showDistance = profile.showDistance,
                showOnlineStatus = profile.showOnlineStatus
            )
            
            val response = profileApiService.updateProfile(userId.toInt(), updateDto)
            
            if (response.isSuccessful) {
                val updateResponse = response.body()
                
                if (updateResponse?.success == true && updateResponse.data != null) {
                    val updatedProfile = updateResponse.data.toDomainModel()
                    _profileCache.value = updatedProfile
                    Result.success(updatedProfile)
                } else {
                    Result.failure(Exception(updateResponse?.message ?: "Erro ao atualizar perfil"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadProfilePhoto(
        userId: String,
        photoFile: File,
        orderSequence: Int
    ): Result<ProfilePhoto> {
        return try {
            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            
            val response = profileApiService.uploadProfilePhoto(
                userId = userId.toInt(),
                photo = photoPart,
                orderSequence = orderSequence
            )
            
            if (response.isSuccessful) {
                val uploadResponse = response.body()
                
                if (uploadResponse?.success == true && uploadResponse.data != null) {
                    val profilePhoto = ProfilePhoto(
                        id = uploadResponse.data.id,
                        url = uploadResponse.data.url,
                        orderSequence = uploadResponse.data.orderSequence ?: 0,
                        score = 0, // TODO: Add when available in DTO
                        isMainPhoto = false, // TODO: Add when available in DTO
                        uploadedAt = parseTimestamp(uploadResponse.data.uploadedAt) ?: System.currentTimeMillis()
                    )
                    Result.success(profilePhoto)
                } else {
                    Result.failure(Exception(uploadResponse?.message ?: "Erro ao fazer upload da foto"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteProfilePhoto(userId: String, photoId: String): Result<Unit> {
        return try {
            val response = profileApiService.deleteProfilePhoto(
                userId = userId.toInt(),
                photoId = photoId
            )
            
            if (response.isSuccessful) {
                val deleteResponse = response.body()
                if (deleteResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(deleteResponse?.message ?: "Erro ao deletar foto"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reorderPhotos(
        userId: String,
        photoOrders: List<Pair<String, Int>>
    ): Result<Unit> {
        return try {
            // Mock implementation - PhotoOrderDto not available
            // TODO: Implement when DTO is created
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailableInterests(): Result<List<Interest>> {
        return try {
            val response = profileApiService.getAvailableInterests()
            
            if (response.isSuccessful) {
                val interestsResponse = response.body()
                
                if (interestsResponse?.success == true) {
                    val interests = interestsResponse.data.map { interestDto ->
                        Interest(
                            id = interestDto.id,
                            name = interestDto.name,
                            category = interestDto.category,
                            isSelected = interestDto.isSelected
                        )
                    }
                    Result.success(interests)
                } else {
                    Result.failure(Exception(interestsResponse?.message ?: "Erro ao carregar interesses"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserInterests(
        userId: String,
        interestIds: List<String>
    ): Result<Unit> {
        return try {
            val response = profileApiService.updateUserInterests(userId.toInt(), interestIds)
            
            if (response.isSuccessful) {
                val updateResponse = response.body()
                if (updateResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(updateResponse?.message ?: "Erro ao atualizar interesses"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProfileStats(userId: String): Result<ProfileStats> {
        return try {
            val response = profileApiService.getProfileStats(userId.toInt())
            
            if (response.isSuccessful) {
                val statsResponse = response.body()
                
                if (statsResponse?.success == true) {
                    val stats = ProfileStats(
                        profileCompletion = statsResponse.data.profileCompletion ?: 0,
                        totalPhotos = statsResponse.data.totalPhotos ?: 0,
                        totalInterests = statsResponse.data.totalInterests ?: 0,
                        profileViews = statsResponse.data.profileViews ?: 0,
                        likes = statsResponse.data.likes ?: 0,
                        superLikes = 0, // TODO: Add when available in DTO
                        matches = 0, // TODO: Add when available in DTO
                        boostRemaining = 0 // TODO: Add when available in DTO
                    )
                    Result.success(stats)
                } else {
                    Result.failure(Exception(statsResponse?.message ?: "Erro ao carregar estatísticas"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePrivacySettings(
        userId: String,
        showAge: Boolean,
        showDistance: Boolean,
        showOnlineStatus: Boolean,
        profileVisibility: String
    ): Result<Unit> {
        return try {
            // Mock implementation - PrivacySettingsDto not available
            // TODO: Implement when DTO is created
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeProfile(userId: String): Flow<ProfileData?> {
        return _profileCache.asStateFlow()
    }
}

// Extension functions para converter DTOs em domain models
private fun ProfileDto.toDomainModel(): ProfileData {
    return ProfileData(
        id = this.id.toString(),
        firstName = this.firstName,
        lastName = this.lastName,
        age = this.age ?: 0,
        bio = this.bio,
        gender = this.gender,
        jobTitle = this.jobTitle,
        company = this.company,
        school = this.school,
        photos = this.photos?.map { photoDto ->
            ProfilePhoto(
                id = photoDto.id,
                url = photoDto.url,
                orderSequence = photoDto.orderSequence,
                score = photoDto.score,
                isMainPhoto = photoDto.isMainPhoto,
                uploadedAt = parseTimestamp(photoDto.uploadedAt) ?: System.currentTimeMillis()
            )
        } ?: emptyList(),
        interests = this.interests?.map { interestDto ->
            Interest(
                id = interestDto.id,
                name = interestDto.name,
                category = interestDto.category,
                isSelected = interestDto.isSelected
            )
        } ?: emptyList(),
        location = this.location,
        distance = this.distance,
        isVerified = this.isVerified,
        isPremium = this.isPremium,
        isOnline = this.isOnline,
        lastSeen = parseTimestamp(this.lastSeen),
        profileCompletion = this.profileCompletion,
        sexualOrientation = this.sexualOrientation,
        lookingFor = this.lookingFor,
        showAge = this.showAge,
        showDistance = this.showDistance,
        showOnlineStatus = this.showOnlineStatus
    )
}

private fun ProfilePhotoDto.toDomainModel(): ProfilePhoto {
    return ProfilePhoto(
        id = this.id,
        url = this.url,
        orderSequence = this.orderSequence,
        score = this.score,
        isMainPhoto = this.isMainPhoto,
        uploadedAt = parseTimestamp(this.uploadedAt) ?: System.currentTimeMillis()
    )
}

// Removed duplicate extension functions - using inline mapping instead

private fun ProfileStatsDto.toDomainModel(): ProfileStats {
    return ProfileStats(
        profileCompletion = this.profileCompletion,
        totalPhotos = this.totalPhotos,
        totalInterests = this.totalInterests,
        profileViews = this.profileViews,
        likes = this.likes,
        superLikes = this.superLikes,
        matches = this.matches,
        boostRemaining = this.boostRemaining
    )
}

private fun parseTimestamp(timestamp: String?): Long? {
    return try {
        if (timestamp != null) {
            // Assumindo formato ISO: "2023-12-01T10:30:00.000000Z"
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(timestamp)?.time
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}