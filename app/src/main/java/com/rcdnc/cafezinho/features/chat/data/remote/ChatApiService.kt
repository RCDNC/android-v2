package com.rcdnc.cafezinho.features.chat.data.remote

import com.rcdnc.cafezinho.features.chat.data.remote.dto.ChatPairDto
import com.rcdnc.cafezinho.features.chat.data.remote.dto.InboxMessageDto
import com.rcdnc.cafezinho.features.chat.data.remote.dto.MatchUpdateDto
import com.rcdnc.cafezinho.features.chat.data.remote.dto.RandomQuestionDto
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para Chat
 * Baseado na análise da API Laravel do Cafezinho
 */
interface ChatApiService {
    
    companion object {
        private const val BASE_PATH = "api"
    }
    
    // INBOX ENDPOINTS
    
    /**
     * Verificar se dois usuários já conversaram antes
     * GET /api/inbox/twoUsersHaveChattedBefore
     */
    @GET("$BASE_PATH/inbox/twoUsersHaveChattedBefore")
    suspend fun twoUsersHaveChattedBefore(
        @Query("sender_id") senderId: Int,
        @Query("receiver_id") receiverId: Int
    ): Response<HaveChatted>
    
    /**
     * Obter todas as conversas da cafeteria para um usuário
     * GET /api/inbox/allPairsFromCafeteria/{userId}
     */
    @GET("$BASE_PATH/inbox/allPairsFromCafeteria/{userId}")
    suspend fun getAllPairsFromCafeteria(
        @Path("userId") userId: Int
    ): Response<ChatPairsResponse>
    
    /**
     * Deletar conversa entre dois usuários
     * DELETE /api/inbox/{userId}/{otherUserId}
     */
    @DELETE("$BASE_PATH/inbox/{userId}/{otherUserId}")
    suspend fun deleteConversation(
        @Path("userId") userId: Int,
        @Path("otherUserId") otherUserId: Int
    ): Response<DeleteResponse>
    
    // MATCH ENDPOINTS (relacionados ao contador de mensagens)
    
    /**
     * Atualizar contador de mensagens no match
     * POST /api/match/
     */
    @POST("$BASE_PATH/match/")
    suspend fun updateMessageCount(
        @Body matchUpdate: MatchUpdateDto
    ): Response<MatchResponse>
    
    /**
     * Obter matches do usuário
     * GET /api/match/{userId}
     */
    @GET("$BASE_PATH/match/{userId}")
    suspend fun getUserMatches(
        @Path("userId") userId: Int
    ): Response<UserMatchesResponse>
    
    /**
     * Deletar match entre dois usuários
     * DELETE /api/match/{userId}/{otherUserId}
     */
    @DELETE("$BASE_PATH/match/{userId}/{otherUserId}")
    suspend fun deleteMatch(
        @Path("userId") userId: Int,
        @Path("otherUserId") otherUserId: Int
    ): Response<DeleteResponse>
    
    // NOTIFICATION ENDPOINTS
    
    /**
     * Enviar notificação de mensagem
     * GET /api/notification/{senderId}/{receiverId}
     */
    @GET("$BASE_PATH/notification/{senderId}/{receiverId}")
    suspend fun sendMessageNotification(
        @Path("senderId") senderId: Int,
        @Path("receiverId") receiverId: Int
    ): Response<NotificationResponse>
    
    /**
     * Enviar notificação pré-match
     * GET /api/notification/preMatch/{senderId}/{receiverId}
     */
    @GET("$BASE_PATH/notification/preMatch/{senderId}/{receiverId}")
    suspend fun sendPreMatchNotification(
        @Path("senderId") senderId: Int,
        @Path("receiverId") receiverId: Int
    ): Response<NotificationResponse>
    
    // CAFETERIA ENDPOINTS
    
    /**
     * Obter pergunta aleatória da cafeteria
     * GET /api/cafeteria/randomQuestion/
     */
    @GET("$BASE_PATH/cafeteria/randomQuestion/")
    suspend fun getRandomQuestion(): Response<RandomQuestionResponse>
}

// RESPONSE DATA CLASSES

data class HaveChatted(
    val haveChatted: Boolean
)

data class ChatPairsResponse(
    val pairs: List<ChatPairDto>
)

data class UserMatchesResponse(
    val matches: List<MatchDto>
)

data class DeleteResponse(
    val message: String
)

data class MatchResponse(
    val success: Boolean,
    val message: String? = null
)

data class NotificationResponse(
    val success: Boolean,
    val message: String? = null
)

data class RandomQuestionResponse(
    val question: RandomQuestionDto
)

data class MatchDto(
    val id: Int,
    val user_id: Int,
    val other_user_id: Int,
    val user_messages_count: Int,
    val other_user_messages_count: Int,
    val created_at: String,
    val updated_at: String
)