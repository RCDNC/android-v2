package com.rcdnc.cafezinho.features.matches.mvi

import com.rcdnc.cafezinho.features.matches.domain.model.MatchData

sealed class MatchesState {
    object Loading : MatchesState()
    data class Success(val matches: List<MatchData>) : MatchesState()
    data class Error(val message: String) : MatchesState()
    object Empty : MatchesState()
}