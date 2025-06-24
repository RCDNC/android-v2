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
        // Se for usuário demo, retorna dados mockados
        if (userId == "1" || userId.startsWith("demo-user-")) {
            val demoProfile = getDemoProfile()
            _profileCache.value = demoProfile
            android.util.Log.d("ProfileRepository", "Returning demo profile for userId: $userId")
            return Result.success(demoProfile)
        }
        
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
        // Sempre retorna interesses demo para simplificar
        return Result.success(getDemoInterests())
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
        // Se for usuário demo, retorna estatísticas demo
        if (userId == "1" || userId.startsWith("demo-user-")) {
            return Result.success(getDemoProfileStats())
        }
        
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

/**
 * Retorna perfil demo para usuário de teste
 */
private fun getDemoProfile(): ProfileData {
    return ProfileData(
        id = "1",
        firstName = "João",
        lastName = "Demo",
        age = 28,
        bio = "Desenvolvedor apaixonado por café ☕ e tecnologia 💻\n\nProcurando alguém para compartilhar momentos especiais e um bom café!",
        gender = "Masculino",
        jobTitle = "Desenvolvedor Mobile",
        company = "Tech Startup",
        school = "USP - Ciência da Computação",
        photos = listOf(
            ProfilePhoto(
                id = "photo1",
                url = "https://example.com/demo1.jpg",
                orderSequence = 0,
                score = 95,
                isMainPhoto = true,
                uploadedAt = System.currentTimeMillis() - 86400000 // 1 dia atrás
            ),
            ProfilePhoto(
                id = "photo2", 
                url = "https://example.com/demo2.jpg",
                orderSequence = 1,
                score = 88,
                isMainPhoto = false,
                uploadedAt = System.currentTimeMillis() - 172800000 // 2 dias atrás
            ),
            ProfilePhoto(
                id = "photo3",
                url = "https://example.com/demo3.jpg", 
                orderSequence = 2,
                score = 82,
                isMainPhoto = false,
                uploadedAt = System.currentTimeMillis() - 259200000 // 3 dias atrás
            )
        ),
        interests = listOf(
            Interest("1", "Café", "Bebidas", true),
            Interest("2", "Tecnologia", "Hobbies", true),
            Interest("3", "Games", "Entretenimento", true),
            Interest("4", "Viagens", "Lifestyle", true),
            Interest("5", "Música", "Arte", true),
            Interest("6", "Fotografia", "Arte", true)
        ),
        location = "São Paulo, SP",
        distance = "0 km",
        isVerified = true,
        isPremium = false,
        isOnline = true,
        lastSeen = System.currentTimeMillis(),
        profileCompletion = 85,
        sexualOrientation = "Heterossexual",
        lookingFor = "Relacionamento sério",
        showAge = true,
        showDistance = true,
        showOnlineStatus = true
    )
}

/**
 * Retorna lista de interesses disponíveis
 */
private fun getDemoInterests(): List<Interest> {
    return listOf(
        // Bebidas
        Interest("1", "Café", "Bebidas", false),
        Interest("2", "Chá", "Bebidas", false),
        Interest("3", "Vinho", "Bebidas", false),
        Interest("4", "Cerveja Artesanal", "Bebidas", false),
        Interest("5", "Drinks", "Bebidas", false),
        
        // Hobbies
        Interest("6", "Tecnologia", "Hobbies", false),
        Interest("7", "Leitura", "Hobbies", false),
        Interest("8", "Cozinhar", "Hobbies", false),
        Interest("9", "Jardinagem", "Hobbies", false),
        Interest("10", "DIY", "Hobbies", false),
        
        // Entretenimento
        Interest("11", "Games", "Entretenimento", false),
        Interest("12", "Cinema", "Entretenimento", false),
        Interest("13", "Séries", "Entretenimento", false),
        Interest("14", "Stand-up", "Entretenimento", false),
        Interest("15", "Teatro", "Entretenimento", false),
        
        // Lifestyle
        Interest("16", "Viagens", "Lifestyle", false),
        Interest("17", "Fitness", "Lifestyle", false),
        Interest("18", "Yoga", "Lifestyle", false),
        Interest("19", "Meditação", "Lifestyle", false),
        Interest("20", "Vida Saudável", "Lifestyle", false),
        
        // Arte
        Interest("21", "Música", "Arte", false),
        Interest("22", "Fotografia", "Arte", false),
        Interest("23", "Pintura", "Arte", false),
        Interest("24", "Dança", "Arte", false),
        Interest("25", "Poesia", "Arte", false),
        
        // Esportes
        Interest("26", "Futebol", "Esportes", false),
        Interest("27", "Corrida", "Esportes", false),
        Interest("28", "Natação", "Esportes", false),
        Interest("29", "Ciclismo", "Esportes", false),
        Interest("30", "Trilhas", "Esportes", false),
        
        // Animais
        Interest("31", "Cachorros", "Animais", false),
        Interest("32", "Gatos", "Animais", false),
        Interest("33", "Pássaros", "Animais", false),
        Interest("34", "Aquarismo", "Animais", false),
        Interest("35", "Vida Selvagem", "Animais", false),
        
        // Cultura
        Interest("36", "Idiomas", "Cultura", false),
        Interest("37", "História", "Cultura", false),
        Interest("38", "Filosofia", "Cultura", false),
        Interest("39", "Política", "Cultura", false),
        Interest("40", "Voluntariado", "Cultura", false)
    )
}

/**
 * Retorna estatísticas demo do perfil
 */
private fun getDemoProfileStats(): ProfileStats {
    return ProfileStats(
        profileCompletion = 85,
        totalPhotos = 3,
        totalInterests = 6,
        profileViews = 342,
        likes = 127,
        superLikes = 8,
        matches = 6,
        boostRemaining = 0
    )
}