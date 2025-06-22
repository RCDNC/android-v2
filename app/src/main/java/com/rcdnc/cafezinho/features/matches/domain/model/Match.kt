package com.rcdnc.cafezinho.features.matches.domain.model

/**
 * Modelo de Match baseado no MatchModel.kt legacy
 * Representa um match entre usuários
 */
data class Match(
    val id: String,
    val userId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserAvatar: String?,
    val otherUserAge: Int? = null,
    val otherUserDistance: String? = null,
    val isSuperLike: Boolean = false,
    val isBoostedLike: Boolean = false,
    val isPremiumUser: Boolean = false,
    val matchedAt: Long = System.currentTimeMillis(),
    val userMessagesCount: Int = 0,
    val otherUserMessagesCount: Int = 0,
    val hasUnreadMessages: Boolean = false,
    val lastMessageTimestamp: Long? = null,
    val isNewMatch: Boolean = false,
    val isOnline: Boolean = false
)

/**
 * Tipo de match para diferentes visualizações
 */
enum class MatchType {
    REGULAR,      // Match normal
    SUPER_LIKE,   // Super like recebido
    BOOSTED,      // Like com boost
    PREMIUM       // Match com usuário premium
}

/**
 * Estado do match para UI
 */
enum class MatchStatus {
    NEW,          // Match novo (destacado)
    ACTIVE,       // Match ativo com conversas
    INACTIVE      // Match sem atividade recente
}