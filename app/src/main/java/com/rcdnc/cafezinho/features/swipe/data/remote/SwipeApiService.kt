package com.rcdnc.cafezinho.features.swipe.data.remote

import com.rcdnc.cafezinho.features.swipe.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para funcionalidades de Swipe/Descobrir
 * Baseado nos endpoints reais da API Laravel do Cafezinho
 */
interface SwipeApiService {
    
    /**
     * Busca usuários próximos para descoberta
     * Endpoint real: GET /api/user/showNearByUsers/{id}
     */
    @GET("user/showNearByUsers/{userId}")
    suspend fun getNearbyUsers(
        @Path("userId") userId: Int,
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null,
        @Query("radius") radius: Int? = null,
        @Query("min_age") minAge: Int? = null,
        @Query("max_age") maxAge: Int? = null,
        @Query("gender") gender: String? = null
    ): Response<SwipeUsersResponse>
    
    /**
     * Busca usuários top/recomendados
     * Endpoint real: GET /api/user/getTopUsers/{id}
     */
    @GET("user/getTopUsers/{userId}")
    suspend fun getTopUsers(
        @Path("userId") userId: Int
    ): Response<SwipeUsersResponse>
    
    /**
     * Executa ação de like/dislike/super like
     * Endpoint real: POST /api/consumable/like
     */
    @POST("consumable/like")
    suspend fun performLikeAction(
        @Body request: LikeActionRequest
    ): Response<LikeActionResponse>
    
    /**
     * Desfaz última ação (rewind)
     * Endpoint real: DELETE /api/consumable/like/{uid}/{ouid}
     */
    @DELETE("consumable/like/{userId}/{targetUserId}")
    suspend fun rewindAction(
        @Path("userId") userId: Int,
        @Path("targetUserId") targetUserId: Int
    ): Response<RewindResponse>
    
    /**
     * Busca métricas/consumíveis do usuário
     * Endpoint: GET /api/consumable/user/{userId}
     */
    @GET("consumable/user/{userId}")
    suspend fun getUserConsumables(
        @Path("userId") userId: Int
    ): Response<UserConsumablesResponse>
    
    /**
     * Atualiza preferências de descoberta
     * Endpoint: PUT /api/user/preferences/{userId}
     */
    @PUT("user/preferences/{userId}")
    suspend fun updateUserPreferences(
        @Path("userId") userId: Int,
        @Body preferences: UserPreferencesDto
    ): Response<ApiResponse<Unit>>
    
    /**
     * Busca preferências do usuário
     * Endpoint: GET /api/user/preferences/{userId}
     */
    @GET("user/preferences/{userId}")
    suspend fun getUserPreferences(
        @Path("userId") userId: Int
    ): Response<UserPreferencesResponse>
    
    /**
     * Marca usuário como visualizado
     * Endpoint: POST /api/user/view
     */
    @POST("user/view")
    suspend fun markUserAsViewed(
        @Body request: ViewUserRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Reporta usuário
     * Endpoint: POST /api/user/report
     */
    @POST("user/report")
    suspend fun reportUser(
        @Body request: ReportUserRequest
    ): Response<ApiResponse<Unit>>
    
    /**
     * Busca detalhes de um usuário específico
     * Endpoint: GET /api/user/{userId}
     */
    @GET("user/{userId}")
    suspend fun getUserDetails(
        @Path("userId") userId: Int
    ): Response<UserDetailsResponse>
}