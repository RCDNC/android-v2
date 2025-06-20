package com.rcdnc.cafezinho.features.swipe.domain.model

data class Match(
    val matchId: String,
    val otherUserId: String,
    val timestamp: Long = System.currentTimeMillis()
)