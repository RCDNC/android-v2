package com.rcdnc.cafezinho.features.swipe.domain.repository

import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile

interface UserRepository {
    suspend fun getRecommendedUsers(): Result<List<UserProfile>>
    suspend fun likeUser(userId: String): Result<Match?>
    suspend fun dislikeUser(userId: String): Result<Unit>
    suspend fun superLikeUser(userId: String): Result<Match?>
    suspend fun rewindLastAction(): Result<UserProfile?>
}