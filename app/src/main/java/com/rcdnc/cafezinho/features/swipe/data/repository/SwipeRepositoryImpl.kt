package com.rcdnc.cafezinho.features.swipe.data.repository

import com.rcdnc.cafezinho.features.swipe.data.remote.SwipeApiService
import com.rcdnc.cafezinho.features.swipe.data.remote.dto.*
import com.rcdnc.cafezinho.features.swipe.domain.model.*
import com.rcdnc.cafezinho.features.swipe.domain.repository.SwipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
        // Se for usuário demo, retorna dados mockados
        if (userId == "1" || userId.startsWith("demo-user-")) {
            android.util.Log.d("SwipeRepository", "Returning demo users for swipe userId: $userId")
            return Result.success(getDemoSwipeUsers())
        }
        
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
        // Se for usuário demo, retorna resultado simulado
        if (userId == "1" || userId.startsWith("demo-user-")) {
            return Result.success(getDemoSwipeResult(action, targetUserId))
        }
        
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
        // Se for usuário demo, retorna métricas demo
        if (userId == "1" || userId.startsWith("demo-user-")) {
            val demoMetrics = SwipeMetrics(
                dailyLikesUsed = 15,
                dailyLikesLimit = 30,
                superLikesUsed = 1,
                superLikesLimit = 3,
                rewindsUsed = 0,
                rewindsLimit = 0,
                isPremium = false,
                canUseRewind = false,
                canUseSuperLike = true
            )
            _userMetricsCache.value = demoMetrics
            return Result.success(demoMetrics)
        }
        
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
        return _userMetricsCache.asStateFlow().map { metrics ->
            metrics ?: SwipeMetrics() // Retorna métricas padrão se null
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

/**
 * Retorna usuários demo para o sistema de swipe
 */
private fun getDemoSwipeUsers(): List<SwipeUser> {
    return listOf(
        SwipeUser(
            id = "10",
            firstName = "Sophia",
            lastName = "Martins",
            age = 24,
            bio = "Amo café ☕, livros 📚 e viagens ✈️. Procurando alguém para compartilhar aventuras e conversas profundas.",
            location = "São Paulo, SP",
            distance = "1.2 km",
            photos = listOf(
                SwipeUserPhoto("1", "https://example.com/sophia1.jpg", 0, true),
                SwipeUserPhoto("2", "https://example.com/sophia2.jpg", 1, false),
                SwipeUserPhoto("3", "https://example.com/sophia3.jpg", 2, false)
            ),
            interests = listOf("Café", "Literatura", "Viagens", "Fotografia", "Yoga"),
            jobTitle = "Designer UX/UI",
            company = "Tech Startup",
            school = "USP",
            isVerified = true,
            isPremium = true,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            rating = 4.8,
            profileCompletion = 95,
            mutualConnections = 3,
            mutualInterests = listOf("Café", "Viagens")
        ),
        SwipeUser(
            id = "11",
            firstName = "Gabriel",
            lastName = "Santos",
            age = 28,
            bio = "Desenvolvedor por profissão, cozinheiro por paixão 👨‍💻🍳 Sempre em busca do café perfeito!",
            location = "São Paulo, SP",
            distance = "3.5 km",
            photos = listOf(
                SwipeUserPhoto("4", "https://example.com/gabriel1.jpg", 0, true),
                SwipeUserPhoto("5", "https://example.com/gabriel2.jpg", 1, false)
            ),
            interests = listOf("Tecnologia", "Gastronomia", "Café", "Games", "Música"),
            jobTitle = "Software Engineer",
            company = "Fintech",
            school = "Unicamp",
            isVerified = false,
            isPremium = false,
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 3600000, // 1 hora atrás
            rating = 4.5,
            profileCompletion = 80,
            mutualConnections = 1,
            mutualInterests = listOf("Café", "Tecnologia")
        ),
        SwipeUser(
            id = "12",
            firstName = "Isabella",
            lastName = "Lima",
            age = 26,
            bio = "Médica veterinária 🐾 Apaixonada por animais e natureza 🌿 Café é essencial na minha vida!",
            location = "São Paulo, SP",
            distance = "2.8 km",
            photos = listOf(
                SwipeUserPhoto("6", "https://example.com/isabella1.jpg", 0, true),
                SwipeUserPhoto("7", "https://example.com/isabella2.jpg", 1, false),
                SwipeUserPhoto("8", "https://example.com/isabella3.jpg", 2, false),
                SwipeUserPhoto("9", "https://example.com/isabella4.jpg", 3, false)
            ),
            interests = listOf("Animais", "Natureza", "Café", "Trilhas", "Fotografia"),
            jobTitle = "Veterinária",
            company = "Clínica Pet Love",
            school = "UNESP",
            isVerified = true,
            isPremium = false,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            rating = 4.9,
            profileCompletion = 100,
            mutualConnections = 2,
            mutualInterests = listOf("Café", "Fotografia", "Natureza")
        ),
        SwipeUser(
            id = "13",
            firstName = "Rafael",
            lastName = "Oliveira",
            age = 30,
            bio = "Advogado, músico nas horas vagas 🎸 Sempre pronto para um bom papo e um café especial",
            location = "São Paulo, SP",
            distance = "5.2 km",
            photos = listOf(
                SwipeUserPhoto("10", "https://example.com/rafael1.jpg", 0, true),
                SwipeUserPhoto("11", "https://example.com/rafael2.jpg", 1, false),
                SwipeUserPhoto("12", "https://example.com/rafael3.jpg", 2, false)
            ),
            interests = listOf("Música", "Direito", "Café", "Cinema", "Literatura"),
            jobTitle = "Advogado",
            company = "Oliveira & Associados",
            school = "PUC-SP",
            isVerified = true,
            isPremium = true,
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 7200000, // 2 horas atrás
            rating = 4.7,
            profileCompletion = 90,
            mutualConnections = 0,
            mutualInterests = listOf("Café", "Cinema")
        ),
        SwipeUser(
            id = "14",
            firstName = "Camila",
            lastName = "Ferreira",
            age = 23,
            bio = "Estudante de psicologia 🧠 Amo arte, música e conversas que fazem pensar ✨",
            location = "São Paulo, SP",
            distance = "1.8 km",
            photos = listOf(
                SwipeUserPhoto("13", "https://example.com/camila1.jpg", 0, true),
                SwipeUserPhoto("14", "https://example.com/camila2.jpg", 1, false)
            ),
            interests = listOf("Psicologia", "Arte", "Música", "Café", "Meditação"),
            jobTitle = "Estudante",
            company = null,
            school = "PUC-SP",
            isVerified = false,
            isPremium = false,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            rating = 4.6,
            profileCompletion = 75,
            mutualConnections = 1,
            mutualInterests = listOf("Café", "Arte")
        ),
        SwipeUser(
            id = "15",
            firstName = "Bruno",
            lastName = "Costa",
            age = 32,
            bio = "Empreendedor, fitness enthusiast 💪 Café antes do treino é sagrado! Busco alguém para compartilhar momentos especiais",
            location = "São Paulo, SP",
            distance = "4.0 km",
            photos = listOf(
                SwipeUserPhoto("15", "https://example.com/bruno1.jpg", 0, true),
                SwipeUserPhoto("16", "https://example.com/bruno2.jpg", 1, false),
                SwipeUserPhoto("17", "https://example.com/bruno3.jpg", 2, false)
            ),
            interests = listOf("Fitness", "Empreendedorismo", "Café", "Viagens", "Investimentos"),
            jobTitle = "CEO",
            company = "Costa Ventures",
            school = "FGV",
            isVerified = true,
            isPremium = true,
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 1800000, // 30 minutos atrás
            rating = 4.7,
            profileCompletion = 100,
            mutualConnections = 2,
            mutualInterests = listOf("Café", "Viagens", "Empreendedorismo")
        ),
        SwipeUser(
            id = "16",
            firstName = "Larissa",
            lastName = "Almeida",
            age = 27,
            bio = "Professora de inglês 📚 Apaixonada por culturas e idiomas 🌍 Coffee lover ☕",
            location = "São Paulo, SP",
            distance = "2.5 km",
            photos = listOf(
                SwipeUserPhoto("18", "https://example.com/larissa1.jpg", 0, true),
                SwipeUserPhoto("19", "https://example.com/larissa2.jpg", 1, false),
                SwipeUserPhoto("20", "https://example.com/larissa3.jpg", 2, false),
                SwipeUserPhoto("21", "https://example.com/larissa4.jpg", 3, false)
            ),
            interests = listOf("Idiomas", "Viagens", "Café", "Literatura", "Culinária"),
            jobTitle = "English Teacher",
            company = "Language School",
            school = "Letras - USP",
            isVerified = false,
            isPremium = false,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            rating = 4.8,
            profileCompletion = 85,
            mutualConnections = 1,
            mutualInterests = listOf("Café", "Literatura", "Viagens")
        ),
        SwipeUser(
            id = "17",
            firstName = "Thiago",
            lastName = "Mendes",
            age = 29,
            bio = "Arquiteto apaixonado por design e café ☕ Sempre explorando novos cafés pela cidade",
            location = "São Paulo, SP",
            distance = "3.2 km",
            photos = listOf(
                SwipeUserPhoto("22", "https://example.com/thiago1.jpg", 0, true),
                SwipeUserPhoto("23", "https://example.com/thiago2.jpg", 1, false)
            ),
            interests = listOf("Arquitetura", "Design", "Café", "Arte", "Fotografia"),
            jobTitle = "Arquiteto",
            company = "Studio Design",
            school = "FAU-USP",
            isVerified = true,
            isPremium = false,
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 10800000, // 3 horas atrás
            rating = 4.6,
            profileCompletion = 88,
            mutualConnections = 0,
            mutualInterests = listOf("Café", "Arte", "Design")
        ),
        SwipeUser(
            id = "18",
            firstName = "Amanda",
            lastName = "Rodrigues",
            age = 25,
            bio = "Nutricionista e food lover 🥗 Café é minha paixão (com moderação!) 😄",
            location = "São Paulo, SP",
            distance = "1.5 km",
            photos = listOf(
                SwipeUserPhoto("24", "https://example.com/amanda1.jpg", 0, true),
                SwipeUserPhoto("25", "https://example.com/amanda2.jpg", 1, false),
                SwipeUserPhoto("26", "https://example.com/amanda3.jpg", 2, false)
            ),
            interests = listOf("Nutrição", "Gastronomia", "Café", "Yoga", "Bem-estar"),
            jobTitle = "Nutricionista",
            company = "Clínica Saúde & Vida",
            school = "FSP-USP",
            isVerified = true,
            isPremium = true,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            rating = 4.9,
            profileCompletion = 98,
            mutualConnections = 2,
            mutualInterests = listOf("Café", "Gastronomia", "Yoga")
        ),
        SwipeUser(
            id = "19",
            firstName = "Lucas",
            lastName = "Pereira",
            age = 31,
            bio = "Jornalista e escritor 📝 Sempre com um café na mão e uma história para contar",
            location = "São Paulo, SP",
            distance = "4.5 km",
            photos = listOf(
                SwipeUserPhoto("27", "https://example.com/lucas1.jpg", 0, true),
                SwipeUserPhoto("28", "https://example.com/lucas2.jpg", 1, false),
                SwipeUserPhoto("29", "https://example.com/lucas3.jpg", 2, false)
            ),
            interests = listOf("Jornalismo", "Literatura", "Café", "Cinema", "Política"),
            jobTitle = "Jornalista",
            company = "Revista Digital",
            school = "ECA-USP",
            isVerified = false,
            isPremium = false,
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 14400000, // 4 horas atrás
            rating = 4.5,
            profileCompletion = 82,
            mutualConnections = 1,
            mutualInterests = listOf("Café", "Literatura", "Cinema")
        )
    )
}

/**
 * Retorna resultado demo para ação de swipe
 */
private fun getDemoSwipeResult(action: SwipeAction, targetUserId: String): SwipeResult {
    // Simula matches com alguns usuários específicos
    val isMatch = when {
        action == SwipeAction.DISLIKE -> false
        action == SwipeAction.SUPER_LIKE -> true // Super like sempre dá match em demo
        action == SwipeAction.LIKE && targetUserId in listOf("10", "12", "14", "16", "18") -> true
        else -> false
    }
    
    val matchData = if (isMatch) {
        MatchData(
            matchId = "match-demo-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            message = when (targetUserId) {
                "10" -> "Vocês têm Café e Viagens em comum!"
                "12" -> "Vocês adoram Café e Fotografia!"
                "14" -> "Arte e Café conectam vocês!"
                "16" -> "Literatura e Viagens em comum!"
                "18" -> "Yoga e Gastronomia unem vocês!"
                else -> "É um match! Iniciem uma conversa!"
            }
        )
    } else null
    
    // Encontra o usuário da lista demo
    val user = getDemoSwipeUsers().find { it.id == targetUserId } 
        ?: SwipeUser(id = targetUserId, firstName = "Usuário", age = 25)
    
    android.util.Log.d("SwipeRepository", "Demo swipe action: $action on user $targetUserId - Match: $isMatch")
    
    return SwipeResult(
        action = action,
        user = user,
        isMatch = isMatch,
        matchData = matchData
    )
}