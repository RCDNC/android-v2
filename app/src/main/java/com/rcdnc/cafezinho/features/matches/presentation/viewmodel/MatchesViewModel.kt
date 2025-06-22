package com.rcdnc.cafezinho.features.matches.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.matches.domain.model.Match
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para Matches usando MVI pattern
 * Baseado no padrão implementado no Chat
 */
@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<MatchesState>(MatchesState.Idle)
    val state: StateFlow<MatchesState> = _state.asStateFlow()
    
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()
    
    private var currentUserId: String = "me" // TODO: Get from auth service
    
    fun handleIntent(intent: MatchesIntent) {
        when (intent) {
            is MatchesIntent.LoadMatches -> loadMatches(intent.userId)
            is MatchesIntent.DeleteMatch -> deleteMatch(intent.matchId, intent.otherUserId)
            is MatchesIntent.RefreshMatches -> refreshMatches()
            is MatchesIntent.OpenChat -> openChat(intent.otherUserId)
            MatchesIntent.ClearError -> clearError()
        }
    }
    
    private fun loadMatches(userId: String? = null) {
        val userIdToUse = userId ?: currentUserId
        _state.value = MatchesState.Loading
        
        viewModelScope.launch {
            matchRepository.getUserMatches(userIdToUse)
                .catch { exception ->
                    _state.value = MatchesState.Error(exception.message ?: "Erro ao carregar matches")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { matchesList ->
                            _matches.value = matchesList
                            _state.value = if (matchesList.isEmpty()) {
                                MatchesState.Empty
                            } else {
                                MatchesState.Success(matchesList)
                            }
                        },
                        onFailure = { exception ->
                            _state.value = MatchesState.Error(exception.message ?: "Erro ao carregar matches")
                        }
                    )
                }
        }
    }
    
    private fun deleteMatch(matchId: String, otherUserId: String) {
        _state.value = MatchesState.Loading
        
        viewModelScope.launch {
            matchRepository.deleteMatch(currentUserId, otherUserId)
                .fold(
                    onSuccess = {
                        // Remove match da lista local
                        val updatedMatches = _matches.value.filter { it.id != matchId }
                        _matches.value = updatedMatches
                        
                        _state.value = MatchesState.MatchDeleted(matchId)
                        
                        // Atualiza estado baseado na nova lista
                        if (updatedMatches.isEmpty()) {
                            _state.value = MatchesState.Empty
                        } else {
                            _state.value = MatchesState.Success(updatedMatches)
                        }
                    },
                    onFailure = { exception ->
                        _state.value = MatchesState.Error(exception.message ?: "Erro ao deletar match")
                    }
                )
        }
    }
    
    private fun refreshMatches() {
        loadMatches()
    }
    
    private fun openChat(otherUserId: String) {
        _state.value = MatchesState.NavigateToChat(otherUserId)
    }
    
    private fun clearError() {
        _state.value = MatchesState.Idle
    }
    
    // Public helpers for UI
    fun getCurrentUserId(): String = currentUserId
    
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
    
    fun getNewMatchesCount(): Int {
        return _matches.value.count { it.isNewMatch }
    }
    
    fun getSuperLikesCount(): Int {
        return _matches.value.count { it.isSuperLike }
    }
    
    fun getActiveConversationsCount(): Int {
        return _matches.value.count { it.userMessagesCount > 0 || it.otherUserMessagesCount > 0 }
    }
    
    // Auto-load matches on init
    init {
        loadMatches()
    }
}

/**
 * Estados do Matches (MVI pattern)
 */
sealed class MatchesState {
    object Idle : MatchesState()
    object Loading : MatchesState()
    data class Success(val matches: List<Match>) : MatchesState()
    object Empty : MatchesState()
    data class Error(val message: String) : MatchesState()
    data class MatchDeleted(val matchId: String) : MatchesState()
    data class NavigateToChat(val otherUserId: String) : MatchesState()
}

/**
 * Intenções do usuário (MVI pattern)
 */
sealed class MatchesIntent {
    data class LoadMatches(val userId: String? = null) : MatchesIntent()
    data class DeleteMatch(val matchId: String, val otherUserId: String) : MatchesIntent()
    object RefreshMatches : MatchesIntent()
    data class OpenChat(val otherUserId: String) : MatchesIntent()
    object ClearError : MatchesIntent()
}