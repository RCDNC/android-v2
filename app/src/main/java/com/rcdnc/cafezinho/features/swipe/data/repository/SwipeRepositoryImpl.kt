package com.rcdnc.cafezinho.features.swipe.data.repository

import com.rcdnc.cafezinho.features.swipe.data.remote.SwipeApiService
import com.rcdnc.cafezinho.features.swipe.data.remote.dto.*
import com.rcdnc.cafezinho.features.swipe.domain.model.*
import com.rcdnc.cafezinho.features.swipe.domain.repository.SwipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do SwipeRepository integrado com API Laravel
 * Baseado na API real do Cafezinho analisada
 */
@Singleton
class SwipeRepositoryImpl @Inject constructor(
    private val swipeApiService: SwipeApiService
) : SwipeRepository {
    
    // Cache local para métricas do usuário
    private val _userMetricsCache = MutableStateFlow<SwipeMetrics?>(null)
    
    override suspend fun getNearbyUsers(
        userId: String,
        filters: SwipeFilters?
    ): Result<List<SwipeUser>> {
        return try {
            val response = swipeApiService.getNearbyUsers(
                userId = userId.toInt(),
                minAge = filters?.minAge,
                maxAge = filters?.maxAge,
                radius = filters?.maxDistance,
                gender = filters?.genderPreference?.takeIf { it != "all" }
            )
            
            if (response.isSuccessful) {
                val usersResponse = response.body()
                
                if (usersResponse?.success == true) {
                    val users = usersResponse.data.map { it.toDomainModel() }
                    Result.success(users)
                } else {
                    Result.failure(Exception(usersResponse?.message ?: "Erro ao buscar usuários"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTopUsers(userId: String): Result<List<SwipeUser>> {
        return try {
            val response = swipeApiService.getTopUsers(userId.toInt())
            
            if (response.isSuccessful) {
                val usersResponse = response.body()
                
                if (usersResponse?.success == true) {
                    val users = usersResponse.data.map { it.toDomainModel() }
                    Result.success(users)
                } else {
                    Result.failure(Exception(usersResponse?.message ?: "Erro ao buscar usuários top"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performSwipeAction(
        userId: String,
        targetUserId: String,
        action: SwipeAction
    ): Result<SwipeResult> {
        return try {
            val actionString = when (action) {
                SwipeAction.LIKE -> "like"
                SwipeAction.DISLIKE -> "dislike"
                SwipeAction.SUPER_LIKE -> "super_like"
                SwipeAction.REWIND -> return Result.failure(Exception("Use rewindLastAction for rewind"))
            }
            
            val request = LikeActionRequest(
                userId = userId.toInt(),
                targetUserId = targetUserId.toInt(),
                action = actionString
            )
            
            val response = swipeApiService.performLikeAction(request)
            
            if (response.isSuccessful) {
                val likeResponse = response.body()
                
                if (likeResponse?.success == true) {
                    val result = likeResponse.data?.toDomainModel(action, targetUserId) 
                        ?: SwipeResult(action, createDummyUser(targetUserId))
                    
                    // Atualiza métricas localmente
                    updateLocalMetrics(likeResponse.data)
                    
                    Result.success(result)
                } else {
                    Result.failure(Exception(likeResponse?.message ?: "Erro ao executar ação"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rewindLastAction(
        userId: String,
        targetUserId: String
    ): Result<SwipeUser> {
        return try {
            val response = swipeApiService.rewindAction(
                userId = userId.toInt(),
                targetUserId = targetUserId.toInt()
            )
            
            if (response.isSuccessful) {
                val rewindResponse = response.body()
                
                if (rewindResponse?.success == true && rewindResponse.data != null) {
                    val user = rewindResponse.data.toDomainModel()
                    Result.success(user)
                } else {
                    Result.failure(Exception(rewindResponse?.message ?: "Erro ao desfazer ação"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserMetrics(userId: String): Result<SwipeMetrics> {
        return try {
            val response = swipeApiService.getUserConsumables(userId.toInt())
            
            if (response.isSuccessful) {
                val metricsResponse = response.body()
                
                if (metricsResponse?.success == true && metricsResponse.data != null) {
                    val metrics = metricsResponse.data.toDomainModel()
                    _userMetricsCache.value = metrics
                    Result.success(metrics)
                } else {
                    Result.failure(Exception(metricsResponse?.message ?: "Erro ao buscar métricas"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDiscoveryFilters(
        userId: String,
        filters: SwipeFilters
    ): Result<Unit> {
        return try {
            val preferencesDto = UserPreferencesDto(
                minAge = filters.minAge,
                maxAge = filters.maxAge,
                maxDistance = filters.maxDistance,
                genderPreference = filters.genderPreference,
                showOnlineOnly = filters.showOnlineOnly,
                showVerifiedOnly = filters.showVerifiedOnly,
                requiredInterests = filters.requiredInterests
            )
            
            val response = swipeApiService.updateUserPreferences(userId.toInt(), preferencesDto)
            
            if (response.isSuccessful) {
                val updateResponse = response.body()
                if (updateResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(updateResponse?.message ?: "Erro ao atualizar filtros"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserFilters(userId: String): Result<SwipeFilters> {
        return try {
            val response = swipeApiService.getUserPreferences(userId.toInt())
            
            if (response.isSuccessful) {
                val preferencesResponse = response.body()
                
                if (preferencesResponse?.success == true && preferencesResponse.data != null) {
                    val filters = preferencesResponse.data.toDomainModel()
                    Result.success(filters)
                } else {
                    // Retorna filtros padrão se não encontrar
                    Result.success(SwipeFilters())
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeUserMetrics(userId: String): Flow<SwipeMetrics> {
        return _userMetricsCache.asStateFlow()
            .let { flow ->
                // Se não há dados em cache, busca na API
                kotlinx.coroutines.GlobalScope.launch {
                    if (_userMetricsCache.value == null) {
                        getUserMetrics(userId)
                    }
                }
                flow
            }
            .let { flow ->
                kotlinx.coroutines.flow.map(flow) { metrics ->
                    metrics ?: SwipeMetrics() // Retorna métricas padrão se null
                }
            }
    }
    
    override suspend fun markUserAsViewed(
        userId: String,
        viewedUserId: String
    ): Result<Unit> {
        return try {
            val request = ViewUserRequest(
                viewerId = userId.toInt(),
                viewedUserId = viewedUserId.toInt()
            )
            
            val response = swipeApiService.markUserAsViewed(request)
            
            if (response.isSuccessful) {
                val viewResponse = response.body()
                if (viewResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(viewResponse?.message ?: "Erro ao marcar visualização"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reportUser(
        userId: String,
        reportedUserId: String,
        reason: String
    ): Result<Unit> {
        return try {
            val request = ReportUserRequest(
                reporterId = userId.toInt(),
                reportedUserId = reportedUserId.toInt(),
                reason = reason
            )
            
            val response = swipeApiService.reportUser(request)
            
            if (response.isSuccessful) {
                val reportResponse = response.body()
                if (reportResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(reportResponse?.message ?: "Erro ao reportar usuário"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun updateLocalMetrics(data: LikeActionData?) {
        if (data != null) {
            val currentMetrics = _userMetricsCache.value ?: SwipeMetrics()
            _userMetricsCache.value = currentMetrics.copy(
                dailyLikesUsed = currentMetrics.dailyLikesLimit - (data.likesRemaining ?: 0),
                superLikesUsed = currentMetrics.superLikesLimit - (data.superLikesRemaining ?: 0)
            )
        }
    }
    
    private fun createDummyUser(userId: String): SwipeUser {
        return SwipeUser(
            id = userId,
            firstName = "Usuário",
            age = 25
        )
    }
}

// Extension functions para converter DTOs em domain models
private fun SwipeUserDto.toDomainModel(): SwipeUser {
    return SwipeUser(
        id = this.id.toString(),
        firstName = this.firstName,
        lastName = this.lastName,
        age = this.age ?: 0,
        bio = this.bio,
        location = this.location,
        distance = this.distance,
        photos = this.photos?.map { it.toDomainModel() } ?: emptyList(),
        interests = this.interests ?: emptyList(),
        jobTitle = this.jobTitle,
        company = this.company,
        school = this.school,
        isVerified = this.isVerified ?: false,
        isPremium = this.isPremium ?: false,
        isOnline = this.isOnline ?: false,
        lastSeen = parseTimestamp(this.lastSeen),
        rating = this.rating ?: 0.0,
        profileCompletion = this.profileCompletion ?: 0,
        mutualConnections = this.mutualConnections ?: 0,
        mutualInterests = this.mutualInterests ?: emptyList()
    )
}

private fun SwipePhotoDto.toDomainModel(): SwipeUserPhoto {
    return SwipeUserPhoto(
        id = this.id,
        url = this.url,
        orderSequence = this.orderSequence ?: 0,
        isMainPhoto = this.isMainPhoto ?: false
    )
}

private fun LikeActionData.toDomainModel(action: SwipeAction, targetUserId: String): SwipeResult {
    val matchData = if (this.isMatch && this.matchId != null) {
        MatchData(
            matchId = this.matchId,
            timestamp = System.currentTimeMillis(),
            message = this.matchMessage
        )
    } else null
    
    return SwipeResult(
        action = action,
        user = SwipeUser(id = targetUserId, firstName = "Usuário", age = 25),
        isMatch = this.isMatch,
        matchData = matchData
    )
}

private fun UserConsumablesDto.toDomainModel(): SwipeMetrics {
    return SwipeMetrics(
        dailyLikesUsed = this.dailyLikesUsed,
        dailyLikesLimit = this.dailyLikesLimit,
        superLikesUsed = this.superLikesUsed,
        superLikesLimit = this.superLikesLimit,
        rewindsUsed = this.rewindsUsed,
        rewindsLimit = this.rewindsLimit,
        isPremium = this.isPremium,
        canUseRewind = this.canUseRewind,
        canUseSuperLike = this.canUseSuperLike
    )
}

private fun UserPreferencesDto.toDomainModel(): SwipeFilters {
    return SwipeFilters(
        minAge = this.minAge,
        maxAge = this.maxAge,
        maxDistance = this.maxDistance,
        genderPreference = this.genderPreference,
        showOnlineOnly = this.showOnlineOnly,
        showVerifiedOnly = this.showVerifiedOnly,
        requiredInterests = this.requiredInterests ?: emptyList()
    )
}

private fun parseTimestamp(timestamp: String?): Long? {
    return try {
        if (timestamp != null) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(timestamp)?.time
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}