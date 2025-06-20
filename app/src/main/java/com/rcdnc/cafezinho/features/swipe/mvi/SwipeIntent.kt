package com.rcdnc.cafezinho.features.swipe.mvi

import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile

sealed class SwipeIntent {
    object LoadUsers : SwipeIntent()
    object RefreshUsers : SwipeIntent()
    data class LikeUser(val user: UserProfile) : SwipeIntent()
    data class DislikeUser(val user: UserProfile) : SwipeIntent()
    data class SuperLike(val user: UserProfile) : SwipeIntent()
    object LikeCurrentUser : SwipeIntent()
    object DislikeCurrentUser : SwipeIntent()
    object SuperLikeCurrentUser : SwipeIntent()
    object RewindLastAction : SwipeIntent()
}