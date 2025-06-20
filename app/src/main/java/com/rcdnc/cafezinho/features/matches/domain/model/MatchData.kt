package com.rcdnc.cafezinho.features.matches.domain.model

data class MatchData(
    val matchId: String,
    val userId: String,
    val userName: String,
    val userPhoto: String?,
    val timestamp: Long,
    val lastMessage: String? = null,
    val isNewMatch: Boolean = false
)