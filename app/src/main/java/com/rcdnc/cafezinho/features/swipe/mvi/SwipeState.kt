package com.rcdnc.cafezinho.features.swipe.mvi

import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile

sealed class SwipeState {
    object Loading : SwipeState()
    data class Success(
        val users: List<UserProfile>,
        val currentIndex: Int = 0,
        val match: Match? = null
    ) : SwipeState()
    data class Error(val message: String) : SwipeState()
    object Empty : SwipeState()
}