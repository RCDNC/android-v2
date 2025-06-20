package com.rcdnc.cafezinho.domain.repository

import com.rcdnc.cafezinho.domain.model.Match
import com.rcdnc.cafezinho.domain.model.Message
import com.rcdnc.cafezinho.domain.model.SwipeAction
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun swipeUser(action: SwipeAction): Result<Match?>
    suspend fun getMatches(): Result<List<Match>>
    suspend fun getMatchById(matchId: String): Result<Match?>
    suspend fun sendMessage(message: Message): Result<Message>
    suspend fun getMessages(matchId: String): Result<List<Message>>
    suspend fun markMessagesAsRead(matchId: String): Result<Unit>
    fun observeMatches(): Flow<List<Match>>
    fun observeMessages(matchId: String): Flow<List<Message>>
    suspend fun unmatch(matchId: String): Result<Unit>
}