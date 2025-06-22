package com.rcdnc.cafezinho.features.matches.data.remote

import com.rcdnc.cafezinho.features.matches.data.remote.dto.MatchDto
import com.rcdnc.cafezinho.features.matches.data.remote.dto.MatchUpdateDto
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para Matches
 * Baseado nos endpoints Laravel já implementados
 */
interface MatchApiService {
    
    companion object {
        private const val BASE_PATH = "api"
    }
    
    /**
     * Obter todos os matches de um usuário
     * GET /api/match/{userId}
     */
    @GET("$BASE_PATH/match/{userId}")
    suspend fun getUserMatches(
        @Path("userId") userId: Int
    ): Response<UserMatchesResponse>
    
    /**
     * Atualizar contador de mensagens no match
     * POST /api/match/
     */
    @POST("$BASE_PATH/match/")
    suspend fun updateMessageCount(
        @Body matchUpdate: MatchUpdateDto
    ): Response<MatchUpdateResponse>
    
    /**
     * Deletar match entre dois usuários
     * DELETE /api/match/{userId}/{otherUserId}
     */
    @DELETE("$BASE_PATH/match/{userId}/{otherUserId}")
    suspend fun deleteMatch(
        @Path("userId") userId: Int,
        @Path("otherUserId") otherUserId: Int
    ): Response<DeleteMatchResponse>
}

// Response data classes
data class UserMatchesResponse(
    val success: Boolean,
    val data: List<MatchDto>,
    val message: String? = null
)

data class MatchUpdateResponse(
    val success: Boolean,
    val message: String? = null
)

data class DeleteMatchResponse(
    val success: Boolean,
    val message: String
)