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