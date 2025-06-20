package com.rcdnc.cafezinho.features.swipe.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.swipe.domain.usecase.DislikeUserUseCase
import com.rcdnc.cafezinho.features.swipe.domain.usecase.GetRecommendedUsersUseCase
import com.rcdnc.cafezinho.features.swipe.domain.usecase.LikeUserUseCase
import com.rcdnc.cafezinho.features.swipe.domain.usecase.SuperLikeUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val getRecommendedUsersUseCase: GetRecommendedUsersUseCase,
    private val likeUserUseCase: LikeUserUseCase,
    private val dislikeUserUseCase: DislikeUserUseCase,
    private val superLikeUserUseCase: SuperLikeUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SwipeState>(SwipeState.Loading)
    val state: StateFlow<SwipeState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun handleIntent(intent: SwipeIntent) {
        when (intent) {
            is SwipeIntent.LoadUsers -> loadUsers()
            is SwipeIntent.RefreshUsers -> refreshUsers()
            is SwipeIntent.LikeUser -> likeUser(intent.user.id)
            is SwipeIntent.DislikeUser -> dislikeUser(intent.user.id)
            is SwipeIntent.SuperLike -> superLikeUser(intent.user.id)
            is SwipeIntent.LikeCurrentUser -> likeCurrentUser()
            is SwipeIntent.DislikeCurrentUser -> dislikeCurrentUser()
            is SwipeIntent.SuperLikeCurrentUser -> superLikeCurrentUser()
            is SwipeIntent.RewindLastAction -> rewindLastAction()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = SwipeState.Loading
            getRecommendedUsersUseCase()
                .onSuccess { users ->
                    _state.value = if (users.isEmpty()) {
                        SwipeState.Empty
                    } else {
                        SwipeState.Success(users = users)
                    }
                }
                .onFailure { error ->
                    _state.value = SwipeState.Error(error.message ?: "Failed to load users")
                }
        }
    }

    private fun refreshUsers() {
        loadUsers()
    }

    private fun likeUser(userId: String) {
        viewModelScope.launch {
            likeUserUseCase(userId)
                .onSuccess { match ->
                    val currentState = _state.value
                    if (currentState is SwipeState.Success) {
                        val newIndex = currentState.currentIndex + 1
                        _state.value = currentState.copy(
                            currentIndex = newIndex,
                            match = match
                        )
                        checkIfNeedMoreUsers(currentState.users, newIndex)
                    }
                }
                .onFailure { error ->
                    _state.value = SwipeState.Error(error.message ?: "Failed to like user")
                }
        }
    }

    private fun dislikeUser(userId: String) {
        viewModelScope.launch {
            dislikeUserUseCase(userId)
                .onSuccess {
                    val currentState = _state.value
                    if (currentState is SwipeState.Success) {
                        val newIndex = currentState.currentIndex + 1
                        _state.value = currentState.copy(currentIndex = newIndex)
                        checkIfNeedMoreUsers(currentState.users, newIndex)
                    }
                }
                .onFailure { error ->
                    _state.value = SwipeState.Error(error.message ?: "Failed to dislike user")
                }
        }
    }

    private fun superLikeUser(userId: String) {
        viewModelScope.launch {
            superLikeUserUseCase(userId)
                .onSuccess { match ->
                    val currentState = _state.value
                    if (currentState is SwipeState.Success) {
                        val newIndex = currentState.currentIndex + 1
                        _state.value = currentState.copy(
                            currentIndex = newIndex,
                            match = match
                        )
                        checkIfNeedMoreUsers(currentState.users, newIndex)
                    }
                }
                .onFailure { error ->
                    _state.value = SwipeState.Error(error.message ?: "Failed to super like user")
                }
        }
    }

    private fun likeCurrentUser() {
        val currentState = _state.value
        if (currentState is SwipeState.Success && currentState.currentIndex < currentState.users.size) {
            val currentUser = currentState.users[currentState.currentIndex]
            likeUser(currentUser.id)
        }
    }

    private fun dislikeCurrentUser() {
        val currentState = _state.value
        if (currentState is SwipeState.Success && currentState.currentIndex < currentState.users.size) {
            val currentUser = currentState.users[currentState.currentIndex]
            dislikeUser(currentUser.id)
        }
    }

    private fun superLikeCurrentUser() {
        val currentState = _state.value
        if (currentState is SwipeState.Success && currentState.currentIndex < currentState.users.size) {
            val currentUser = currentState.users[currentState.currentIndex]
            superLikeUser(currentUser.id)
        }
    }

    private fun rewindLastAction() {
        // TODO: Implement rewind logic
    }

    private fun checkIfNeedMoreUsers(users: List<com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile>, currentIndex: Int) {
        if (currentIndex >= users.size - 3) {
            loadUsers()
        }
    }
}