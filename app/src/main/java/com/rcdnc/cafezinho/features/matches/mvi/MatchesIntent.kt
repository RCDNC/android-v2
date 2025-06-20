package com.rcdnc.cafezinho.features.matches.mvi

sealed class MatchesIntent {
    object LoadMatches : MatchesIntent()
    object RefreshMatches : MatchesIntent()
    data class OpenChat(val matchId: String) : MatchesIntent()
}