package com.rcdnc.cafezinho.features.matches.domain.repository

import com.rcdnc.cafezinho.features.matches.domain.model.MatchData
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getMatches(): Flow<Result<List<MatchData>>>
    suspend fun getMatchDetails(matchId: String): Result<MatchData>
    suspend fun deleteMatch(matchId: String): Result<Unit>
}