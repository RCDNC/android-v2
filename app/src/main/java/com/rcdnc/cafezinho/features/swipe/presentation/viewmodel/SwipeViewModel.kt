package com.rcdnc.cafezinho.features.swipe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.swipe.domain.model.*
import com.rcdnc.cafezinho.features.swipe.domain.repository.SwipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para Swipe/Descobrir usando MVI pattern
 * Baseado no padrão implementado no Chat, Matches e Profile
 */
@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<SwipeState>(SwipeState.Idle)
    val state: StateFlow<SwipeState> = _state.asStateFlow()
    
    private val _swipeStack = MutableStateFlow<List<SwipeUser>>(emptyList())
    val swipeStack: StateFlow<List<SwipeUser>> = _swipeStack.asStateFlow()
    
    private val _userMetrics = MutableStateFlow<SwipeMetrics?>(null)
    val userMetrics: StateFlow<SwipeMetrics?> = _userMetrics.asStateFlow()
    
    private val _filters = MutableStateFlow<SwipeFilters>(SwipeFilters())
    val filters: StateFlow<SwipeFilters> = _filters.asStateFlow()
    
    private val _lastSwipedUser = MutableStateFlow<SwipeUser?>(null)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var currentUserId: String = "1" // Default demo user ID
    
    fun handleIntent(intent: SwipeIntent) {
        when (intent) {
            is SwipeIntent.LoadUsers -> loadUsers(intent.refresh)
            is SwipeIntent.SwipeUser -> swipeUser(intent.user, intent.action)
            is SwipeIntent.RewindLastSwipe -> rewindLastSwipe()
            is SwipeIntent.UpdateFilters -> updateFilters(intent.filters)
            is SwipeIntent.LoadMoreUsers -> loadMoreUsers()
            is SwipeIntent.ReportUser -> reportUser(intent.user, intent.reason)
            is SwipeIntent.ShowUserDetails -> showUserDetails(intent.user)
            SwipeIntent.ClearError -> clearError()
            SwipeIntent.LoadMetrics -> loadUserMetrics()
        }
    }
    
    private fun loadUsers(refresh: Boolean = false) {
        if (_isLoading.value) return
        
        android.util.Log.d("SwipeViewModel", "Loading users for userId: $currentUserId")
        
        _isLoading.value = true
        _state.value = SwipeState.Loading
        
        viewModelScope.launch {
            // Para usuários demo, pula getTopUsers e vai direto para getNearbyUsers
            if (currentUserId == "1" || currentUserId.startsWith("demo-user-")) {
                loadNearbyUsers(refresh, append = false)
            } else {
                // Primeiro carrega usuários top/prioritários
                swipeRepository.getTopUsers(currentUserId)
                    .fold(
                        onSuccess = { topUsers ->
                            if (topUsers.isNotEmpty()) {
                                _swipeStack.value = topUsers.take(5) // Limita a 5 usuários top
                                loadNearbyUsers(refresh, append = true)
                            } else {
                                loadNearbyUsers(refresh, append = false)
                            }
                        },
                        onFailure = {
                            // Se falhar, carrega apenas usuários próximos
                            loadNearbyUsers(refresh, append = false)
                        }
                    )
            }
        }
    }
    
    private fun loadNearbyUsers(refresh: Boolean, append: Boolean) {
        viewModelScope.launch {
            swipeRepository.getNearbyUsers(currentUserId, _filters.value)
                .fold(
                    onSuccess = { users ->
                        android.util.Log.d("SwipeViewModel", "Loaded ${users.size} nearby users")
                        
                        val filteredUsers = users.filter { user ->
                            // Remove usuários que já estão na stack
                            !_swipeStack.value.any { it.id == user.id }
                        }
                        
                        val newStack = if (append) {
                            _swipeStack.value + filteredUsers
                        } else {
                            filteredUsers
                        }
                        
                        _swipeStack.value = newStack.take(20) // Limita stack a 20 usuários
                        _state.value = if (newStack.isNotEmpty()) {
                            SwipeState.UsersLoaded(newStack)
                        } else {
                            SwipeState.NoMoreUsers
                        }
                        
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao carregar usuários")
                        _isLoading.value = false
                    }
                )
        }
    }
    
    private fun swipeUser(user: SwipeUser, action: SwipeAction) {
        if (_isLoading.value) return
        
        viewModelScope.launch {
            // Remove usuário da stack imediatamente para UX responsiva
            val currentStack = _swipeStack.value.toMutableList()
            currentStack.removeAll { it.id == user.id }
            _swipeStack.value = currentStack
            _lastSwipedUser.value = user
            
            // Marca como visualizado
            swipeRepository.markUserAsViewed(currentUserId, user.id)
            
            // Se for dislike, não faz call para API (economiza resources)
            if (action == SwipeAction.DISLIKE) {
                _state.value = SwipeState.UserSwiped(
                    SwipeResult(action, user, isMatch = false)
                )
                checkIfNeedMoreUsers()
                return@launch
            }
            
            // Para likes e super likes, chama API
            swipeRepository.performSwipeAction(currentUserId, user.id, action)
                .fold(
                    onSuccess = { result ->
                        _state.value = if (result.isMatch) {
                            SwipeState.MatchFound(result)
                        } else {
                            SwipeState.UserSwiped(result)
                        }
                        
                        // Atualiza métricas localmente
                        updateMetricsAfterSwipe(action)
                        checkIfNeedMoreUsers()
                    },
                    onFailure = { exception ->
                        // Em caso de erro, adiciona usuário de volta à stack
                        currentStack.add(0, user)
                        _swipeStack.value = currentStack
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao processar swipe")
                    }
                )
        }
    }
    
    private fun rewindLastSwipe() {
        val lastUser = _lastSwipedUser.value ?: return
        val currentMetrics = _userMetrics.value
        
        if (currentMetrics?.canUseRewind != true) {
            _state.value = SwipeState.Error("Você não pode desfazer agora")
            return
        }
        
        viewModelScope.launch {
            swipeRepository.rewindLastAction(currentUserId, lastUser.id)
                .fold(
                    onSuccess = { rewindedUser ->
                        // Adiciona usuário de volta ao topo da stack
                        val currentStack = _swipeStack.value.toMutableList()
                        currentStack.add(0, rewindedUser)
                        _swipeStack.value = currentStack
                        
                        _state.value = SwipeState.RewindSuccess(rewindedUser)
                        _lastSwipedUser.value = null
                        
                        // Atualiza métricas
                        loadUserMetrics()
                    },
                    onFailure = { exception ->
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao desfazer ação")
                    }
                )
        }
    }
    
    private fun updateFilters(newFilters: SwipeFilters) {
        _filters.value = newFilters
        
        viewModelScope.launch {
            swipeRepository.updateDiscoveryFilters(currentUserId, newFilters)
                .fold(
                    onSuccess = {
                        _state.value = SwipeState.FiltersUpdated(newFilters)
                        // Recarrega usuários com novos filtros
                        loadUsers(refresh = true)
                    },
                    onFailure = { exception ->
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao atualizar filtros")
                    }
                )
        }
    }
    
    private fun loadMoreUsers() {
        if (_isLoading.value || _swipeStack.value.size >= 15) return
        
        loadNearbyUsers(refresh = false, append = true)
    }
    
    private fun reportUser(user: SwipeUser, reason: String) {
        viewModelScope.launch {
            swipeRepository.reportUser(currentUserId, user.id, reason)
                .fold(
                    onSuccess = {
                        // Remove usuário da stack
                        val currentStack = _swipeStack.value.toMutableList()
                        currentStack.removeAll { it.id == user.id }
                        _swipeStack.value = currentStack
                        
                        _state.value = SwipeState.UserReported(user)
                    },
                    onFailure = { exception ->
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao reportar usuário")
                    }
                )
        }
    }
    
    private fun showUserDetails(user: SwipeUser) {
        _state.value = SwipeState.ShowingUserDetails(user)
    }
    
    private fun loadUserMetrics() {
        viewModelScope.launch {
            swipeRepository.getUserMetrics(currentUserId)
                .fold(
                    onSuccess = { metrics ->
                        _userMetrics.value = metrics
                        _state.value = SwipeState.MetricsLoaded(metrics)
                    },
                    onFailure = { exception ->
                        _state.value = SwipeState.Error(exception.message ?: "Erro ao carregar métricas")
                    }
                )
        }
    }
    
    private fun clearError() {
        _state.value = SwipeState.Idle
    }
    
    private fun updateMetricsAfterSwipe(action: SwipeAction) {
        val currentMetrics = _userMetrics.value ?: return
        
        val updatedMetrics = when (action) {
            SwipeAction.LIKE -> currentMetrics.copy(
                dailyLikesUsed = (currentMetrics.dailyLikesUsed + 1).coerceAtMost(currentMetrics.dailyLikesLimit)
            )
            SwipeAction.SUPER_LIKE -> currentMetrics.copy(
                superLikesUsed = (currentMetrics.superLikesUsed + 1).coerceAtMost(currentMetrics.superLikesLimit)
            )
            else -> currentMetrics
        }
        
        _userMetrics.value = updatedMetrics
    }
    
    private fun checkIfNeedMoreUsers() {
        if (_swipeStack.value.size <= 3) {
            loadMoreUsers()
        }
    }
    
    // Public helpers for UI
    fun getCurrentUserId(): String = currentUserId
    
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
    
    fun canLike(): Boolean {
        val metrics = _userMetrics.value ?: return true
        return metrics.dailyLikesUsed < metrics.dailyLikesLimit
    }
    
    fun canSuperLike(): Boolean {
        val metrics = _userMetrics.value ?: return true
        return metrics.canUseSuperLike && metrics.superLikesUsed < metrics.superLikesLimit
    }
    
    fun canRewind(): Boolean {
        val metrics = _userMetrics.value ?: return false
        return metrics.canUseRewind && _lastSwipedUser.value != null
    }
    
    fun getCurrentUser(): SwipeUser? {
        return _swipeStack.value.firstOrNull()
    }
    
    fun getStackSize(): Int {
        return _swipeStack.value.size
    }
    
    fun getLikesRemaining(): Int {
        val metrics = _userMetrics.value ?: return 100
        return (metrics.dailyLikesLimit - metrics.dailyLikesUsed).coerceAtLeast(0)
    }
    
    fun getSuperLikesRemaining(): Int {
        val metrics = _userMetrics.value ?: return 5
        return (metrics.superLikesLimit - metrics.superLikesUsed).coerceAtLeast(0)
    }
    
    // Auto-load on init
    init {
        loadUserMetrics()
        loadUsers()
    }
}

/**
 * Estados do Swipe (MVI pattern)
 */
sealed class SwipeState {
    object Idle : SwipeState()
    object Loading : SwipeState()
    object NoMoreUsers : SwipeState()
    
    data class UsersLoaded(val users: List<SwipeUser>) : SwipeState()
    data class UserSwiped(val result: SwipeResult) : SwipeState()
    data class MatchFound(val result: SwipeResult) : SwipeState()
    data class RewindSuccess(val user: SwipeUser) : SwipeState()
    data class UserReported(val user: SwipeUser) : SwipeState()
    data class ShowingUserDetails(val user: SwipeUser) : SwipeState()
    data class FiltersUpdated(val filters: SwipeFilters) : SwipeState()
    data class MetricsLoaded(val metrics: SwipeMetrics) : SwipeState()
    
    data class Error(val message: String) : SwipeState()
}

/**
 * Intenções do usuário (MVI pattern)
 */
sealed class SwipeIntent {
    data class LoadUsers(val refresh: Boolean = false) : SwipeIntent()
    data class SwipeUser(val user: com.rcdnc.cafezinho.features.swipe.domain.model.SwipeUser, val action: SwipeAction) : SwipeIntent()
    object RewindLastSwipe : SwipeIntent()
    data class UpdateFilters(val filters: SwipeFilters) : SwipeIntent()
    object LoadMoreUsers : SwipeIntent()
    data class ReportUser(val user: com.rcdnc.cafezinho.features.swipe.domain.model.SwipeUser, val reason: String) : SwipeIntent()
    data class ShowUserDetails(val user: com.rcdnc.cafezinho.features.swipe.domain.model.SwipeUser) : SwipeIntent()
    object LoadMetrics : SwipeIntent()
    object ClearError : SwipeIntent()
}