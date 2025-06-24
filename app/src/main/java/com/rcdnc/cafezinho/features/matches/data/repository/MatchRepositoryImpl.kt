package com.rcdnc.cafezinho.features.matches.data.repository

import com.rcdnc.cafezinho.features.matches.data.remote.MatchApiService
import com.rcdnc.cafezinho.features.matches.data.remote.dto.MatchDto
import com.rcdnc.cafezinho.features.matches.data.remote.dto.MatchUpdateDto
import com.rcdnc.cafezinho.features.matches.domain.model.Match
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do MatchRepository integrado com API Laravel
 * Baseado na análise da API real do Cafezinho
 */
@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val matchApiService: MatchApiService
) : MatchRepository {
    
    override fun getUserMatches(userId: String): Flow<Result<List<Match>>> = flow {
        // Se for usuário demo, retorna dados mockados
        if (userId.startsWith("demo-user-") || userId == "1") {
            val demoMatches = getDemoMatches()
            android.util.Log.d("MatchRepository", "Returning ${demoMatches.size} demo matches for userId: $userId")
            emit(Result.success(demoMatches))
            return@flow
        }
        
        try {
            val response = matchApiService.getUserMatches(userId.toInt())
            
            if (response.isSuccessful) {
                val matchesResponse = response.body()
                
                if (matchesResponse?.success == true) {
                    val matches = matchesResponse.data.map { matchDto ->
                        matchDto.toDomainModel(userId)
                    }
                    emit(Result.success(matches))
                } else {
                    emit(Result.failure(Exception(matchesResponse?.message ?: "Erro ao carregar matches")))
                }
            } else {
                emit(Result.failure(Exception("Erro na API: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun deleteMatch(userId: String, otherUserId: String): Result<Unit> {
        return try {
            val response = matchApiService.deleteMatch(
                userId = userId.toInt(),
                otherUserId = otherUserId.toInt()
            )
            
            if (response.isSuccessful) {
                val deleteResponse = response.body()
                if (deleteResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(deleteResponse?.message ?: "Erro ao deletar match"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMessageCount(
        userId: String,
        otherUserId: String,
        userMessagesCount: Int,
        otherUserMessagesCount: Int
    ): Result<Unit> {
        return try {
            val matchUpdate = MatchUpdateDto(
                userId = userId.toInt(),
                otherUserId = otherUserId.toInt(),
                userMessagesCount = userMessagesCount,
                otherUserMessagesCount = otherUserMessagesCount
            )
            
            val response = matchApiService.updateMessageCount(matchUpdate)
            
            if (response.isSuccessful) {
                val updateResponse = response.body()
                if (updateResponse?.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(updateResponse?.message ?: "Erro ao atualizar contador"))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMatch(userId: String, otherUserId: String): Result<Match?> {
        return try {
            // A API não tem endpoint específico para um match individual
            // Implementação via getUserMatches e filtro local
            val allMatches = getUserMatches(userId)
            var result: Result<Match?> = Result.success(null)
            
            allMatches.collect { matchesResult ->
                result = matchesResult.fold(
                    onSuccess = { matches ->
                        val match = matches.find { it.otherUserId == otherUserId }
                        Result.success(match)
                    },
                    onFailure = { exception ->
                        Result.failure(exception)
                    }
                )
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun hasMatch(userId: String, otherUserId: String): Result<Boolean> {
        return try {
            getMatch(userId, otherUserId).fold(
                onSuccess = { match ->
                    Result.success(match != null)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Extensão para converter DTO em domain model
 */
private fun MatchDto.toDomainModel(currentUserId: String): Match {
    val isCurrentUserSender = this.userId.toString() == currentUserId
    val otherUser = this.otherUser
    
    return Match(
        id = this.id.toString(),
        userId = this.userId.toString(),
        otherUserId = this.otherUserId.toString(),
        otherUserName = otherUser?.name ?: "Usuário ${this.otherUserId}",
        otherUserAvatar = otherUser?.avatar,
        otherUserAge = otherUser?.age,
        otherUserDistance = otherUser?.location,
        isSuperLike = otherUser?.superLike != null,
        isBoostedLike = otherUser?.boostedLike != null,
        isPremiumUser = otherUser?.isPremium ?: false,
        matchedAt = parseTimestamp(this.createdAt),
        userMessagesCount = if (isCurrentUserSender) this.userMessagesCount else this.otherUserMessagesCount,
        otherUserMessagesCount = if (isCurrentUserSender) this.otherUserMessagesCount else this.userMessagesCount,
        hasUnreadMessages = false, // TODO: Implementar lógica baseada nos contadores
        lastMessageTimestamp = parseTimestamp(this.updatedAt),
        isNewMatch = isNewMatch(this.createdAt),
        isOnline = otherUser?.isOnline ?: false
    )
}

/**
 * Parse timestamp string para Long
 */
private fun parseTimestamp(timestamp: String): Long {
    return try {
        // Assumindo formato ISO: "2023-12-01T10:30:00.000000Z"
        // TODO: Implementar parser correto baseado no formato real da API
        System.currentTimeMillis() - (24 * 60 * 60 * 1000) // Mock: 1 dia atrás
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

/**
 * Verificar se é um match novo (últimas 24h)
 */
private fun isNewMatch(createdAt: String): Boolean {
    val matchTime = parseTimestamp(createdAt)
    val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
    return matchTime > oneDayAgo
}

/**
 * Retorna matches demo para usuário de teste
 */
private fun getDemoMatches(): List<Match> {
    return listOf(
        Match(
            id = "match-1",
            userId = "1",
            otherUserId = "2",
            otherUserName = "Maria Silva",
            otherUserAvatar = null,
            otherUserAge = 28,
            otherUserDistance = "2 km",
            isSuperLike = true,
            isPremiumUser = true,
            isNewMatch = true,
            isOnline = true,
            hasUnreadMessages = true,
            userMessagesCount = 3,
            otherUserMessagesCount = 5,
            matchedAt = System.currentTimeMillis() - 3600000, // 1 hora atrás
            lastMessageTimestamp = System.currentTimeMillis() - 1800000
        ),
        Match(
            id = "match-2",
            userId = "1",
            otherUserId = "3",
            otherUserName = "João Santos",
            otherUserAvatar = null,
            otherUserAge = 32,
            otherUserDistance = "5 km",
            isSuperLike = false,
            isPremiumUser = true,
            isNewMatch = false,
            isOnline = false,
            hasUnreadMessages = false,
            userMessagesCount = 10,
            otherUserMessagesCount = 12,
            matchedAt = System.currentTimeMillis() - 86400000, // 1 dia atrás
            lastMessageTimestamp = System.currentTimeMillis() - 7200000
        ),
        Match(
            id = "match-3",
            userId = "1",
            otherUserId = "4",
            otherUserName = "Ana Costa",
            otherUserAvatar = null,
            otherUserAge = 25,
            otherUserDistance = "3 km",
            isSuperLike = true,
            isPremiumUser = false,
            isNewMatch = true,
            isOnline = true,
            hasUnreadMessages = false,
            userMessagesCount = 0,
            otherUserMessagesCount = 0,
            matchedAt = System.currentTimeMillis() - 7200000, // 2 horas atrás
            lastMessageTimestamp = null
        ),
        Match(
            id = "match-4",
            userId = "1",
            otherUserId = "5",
            otherUserName = "Pedro Oliveira",
            otherUserAvatar = null,
            otherUserAge = 30,
            otherUserDistance = "8 km",
            isSuperLike = false,
            isPremiumUser = false,
            isNewMatch = false,
            isOnline = true,
            hasUnreadMessages = true,
            userMessagesCount = 1,
            otherUserMessagesCount = 2,
            matchedAt = System.currentTimeMillis() - 172800000, // 2 dias atrás
            lastMessageTimestamp = System.currentTimeMillis() - 3600000
        ),
        Match(
            id = "match-5",
            userId = "1",
            otherUserId = "6",
            otherUserName = "Julia Mendes",
            otherUserAvatar = null,
            otherUserAge = 26,
            otherUserDistance = "1 km",
            isSuperLike = false,
            isPremiumUser = true,
            isNewMatch = false,
            isOnline = false,
            hasUnreadMessages = false,
            userMessagesCount = 15,
            otherUserMessagesCount = 18,
            matchedAt = System.currentTimeMillis() - 604800000, // 1 semana atrás
            lastMessageTimestamp = System.currentTimeMillis() - 86400000
        ),
        Match(
            id = "match-6",
            userId = "1",
            otherUserId = "7",
            otherUserName = "Lucas Ferreira",
            otherUserAvatar = null,
            otherUserAge = 29,
            otherUserDistance = "4 km",
            isSuperLike = true,
            isPremiumUser = false,
            isNewMatch = true,
            isOnline = true,
            hasUnreadMessages = true,
            userMessagesCount = 0,
            otherUserMessagesCount = 1,
            matchedAt = System.currentTimeMillis() - 1800000, // 30 minutos atrás
            lastMessageTimestamp = System.currentTimeMillis() - 900000
        )
    )
}