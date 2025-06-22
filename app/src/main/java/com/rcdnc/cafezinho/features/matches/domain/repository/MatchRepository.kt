package com.rcdnc.cafezinho.features.matches.domain.repository

import com.rcdnc.cafezinho.features.matches.domain.model.Match
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface para Matches
 * Baseado na API Laravel e funcionalidades legacy
 */
interface MatchRepository {
    
    /**
     * Obter todos os matches do usuário
     */
    fun getUserMatches(userId: String): Flow<Result<List<Match>>>
    
    /**
     * Deletar um match específico
     */
    suspend fun deleteMatch(userId: String, otherUserId: String): Result<Unit>
    
    /**
     * Atualizar contador de mensagens de um match
     */
    suspend fun updateMessageCount(
        userId: String,
        otherUserId: String,
        userMessagesCount: Int,
        otherUserMessagesCount: Int
    ): Result<Unit>
    
    /**
     * Obter match específico entre dois usuários
     */
    suspend fun getMatch(userId: String, otherUserId: String): Result<Match?>
    
    /**
     * Verificar se existe match entre dois usuários
     */
    suspend fun hasMatch(userId: String, otherUserId: String): Result<Boolean>
}