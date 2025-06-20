package com.rcdnc.cafezinho.features.matches.domain.usecase

import com.rcdnc.cafezinho.features.matches.domain.model.MatchData
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMatchesUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    operator fun invoke(): Flow<Result<List<MatchData>>> {
        return matchRepository.getMatches()
            .map { result ->
                if (result.isSuccess) {
                    val matches = result.getOrThrow()
                    val sortedMatches = applySortingRules(matches)
                    Result.success(sortedMatches)
                } else {
                    result
                }
            }
            .catch { exception ->
                emit(Result.failure(exception))
            }
    }

    private fun applySortingRules(matches: List<MatchData>): List<MatchData> {
        return matches
            .filter { it.matchId.isNotBlank() && it.userId.isNotBlank() }
            .sortedWith(compareByDescending<MatchData> { it.isNewMatch }
                .thenByDescending { it.timestamp })
    }
}