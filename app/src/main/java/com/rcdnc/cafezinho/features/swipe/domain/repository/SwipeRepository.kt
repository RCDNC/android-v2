package com.rcdnc.cafezinho.features.swipe.domain.repository

import com.rcdnc.cafezinho.features.swipe.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface para funcionalidades de Swipe/Descobrir
 * Baseado na API Laravel existente do Cafezinho
 */
interface SwipeRepository {
    
    /**
     * Busca usuários próximos para swipe
     * Endpoint: GET /api/user/showNearByUsers/{id}
     */
    suspend fun getNearbyUsers(
        userId: String,
        filters: SwipeFilters? = null
    ): Result<List<SwipeUser>>
    
    /**
     * Busca usuários top/recomendados
     * Endpoint: GET /api/user/getTopUsers/{id}
     */
    suspend fun getTopUsers(userId: String): Result<List<SwipeUser>>
    
    /**
     * Executa ação de like/dislike/super like
     * Endpoint: POST /api/consumable/like
     */
    suspend fun performSwipeAction(
        userId: String,
        targetUserId: String,
        action: SwipeAction
    ): Result<SwipeResult>
    
    /**
     * Desfaz última ação (rewind)
     * Endpoint: DELETE /api/consumable/like/{uid}/{ouid}
     */
    suspend fun rewindLastAction(
        userId: String,
        targetUserId: String
    ): Result<SwipeUser>
    
    /**
     * Busca métricas e limites do usuário
     */
    suspend fun getUserMetrics(userId: String): Result<SwipeMetrics>
    
    /**
     * Atualiza filtros de descoberta
     */
    suspend fun updateDiscoveryFilters(
        userId: String,
        filters: SwipeFilters
    ): Result<Unit>
    
    /**
     * Busca filtros atuais do usuário
     */
    suspend fun getUserFilters(userId: String): Result<SwipeFilters>
    
    /**
     * Observa mudanças nas métricas do usuário
     */
    fun observeUserMetrics(userId: String): Flow<SwipeMetrics>
    
    /**
     * Marca usuário como visualizado
     */
    suspend fun markUserAsViewed(
        userId: String,
        viewedUserId: String
    ): Result<Unit>
    
    /**
     * Reporta usuário
     */
    suspend fun reportUser(
        userId: String,
        reportedUserId: String,
        reason: String
    ): Result<Unit>
}