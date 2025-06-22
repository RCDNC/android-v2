package com.rcdnc.cafezinho.features.matches.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para integração com API Laravel de Matches
 * Baseado na estrutura real da API analisada
 */

/**
 * DTO principal do Match vindo da API Laravel
 */
data class MatchDto(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("other_user_id")
    val otherUserId: Int,
    @SerializedName("user_messages_count")
    val userMessagesCount: Int,
    @SerializedName("other_user_messages_count")
    val otherUserMessagesCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    
    // Dados do outro usuário (vem via join/relationship)
    @SerializedName("other_user")
    val otherUser: MatchUserDto? = null
)

/**
 * DTO para dados do usuário no match
 */
data class MatchUserDto(
    val id: Int,
    val name: String,
    val email: String? = null,
    val avatar: String? = null,
    val age: Int? = null,
    val bio: String? = null,
    val location: String? = null,
    @SerializedName("is_premium")
    val isPremium: Boolean = false,
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    @SerializedName("last_seen")
    val lastSeen: String? = null,
    
    // Campos específicos do match
    @SerializedName("super_like")
    val superLike: String? = null,
    @SerializedName("boosted_like")
    val boostedLike: String? = null,
    @SerializedName("match_type")
    val matchType: String? = null
)

/**
 * DTO para atualizar contador de mensagens
 */
data class MatchUpdateDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("other_user_id")
    val otherUserId: Int,
    @SerializedName("user_messages_count")
    val userMessagesCount: Int,
    @SerializedName("other_user_messages_count")
    val otherUserMessagesCount: Int
)

/**
 * DTO para estatísticas de matches
 */
data class MatchStatsDto(
    @SerializedName("total_matches")
    val totalMatches: Int,
    @SerializedName("new_matches")
    val newMatches: Int,
    @SerializedName("active_conversations")
    val activeConversations: Int,
    @SerializedName("super_likes_received")
    val superLikesReceived: Int
)