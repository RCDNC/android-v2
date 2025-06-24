package com.rcdnc.cafezinho.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.model.ProfilePhoto
import com.rcdnc.cafezinho.features.profile.domain.model.Interest
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel para Profile usando MVI pattern
 * Baseado no padrão implementado no Chat e Matches
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    private val _profile = MutableStateFlow<ProfileData?>(null)
    val profile: StateFlow<ProfileData?> = _profile.asStateFlow()
    
    private val _availableInterests = MutableStateFlow<List<Interest>>(emptyList())
    val availableInterests: StateFlow<List<Interest>> = _availableInterests.asStateFlow()
    
    private val _profileStats = MutableStateFlow<ProfileStats?>(null)
    val profileStats: StateFlow<ProfileStats?> = _profileStats.asStateFlow()
    
    private var currentUserId: String = "1" // Default demo user ID
    
    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile(intent.userId)
            is ProfileIntent.UpdateProfile -> updateProfile(intent.profileData)
            is ProfileIntent.UploadPhoto -> uploadPhoto(intent.photoFile, intent.orderSequence)
            is ProfileIntent.DeletePhoto -> deletePhoto(intent.photoId)
            is ProfileIntent.ReorderPhotos -> reorderPhotos(intent.photoOrders)
            is ProfileIntent.LoadAvailableInterests -> loadAvailableInterests()
            is ProfileIntent.UpdateInterests -> updateInterests(intent.interestIds)
            is ProfileIntent.LoadProfileStats -> loadProfileStats(intent.userId)
            is ProfileIntent.UpdatePrivacySettings -> updatePrivacySettings(
                intent.showAge, intent.showDistance, intent.showOnlineStatus, intent.profileVisibility
            )
            is ProfileIntent.StartEditMode -> startEditMode()
            is ProfileIntent.CancelEditMode -> cancelEditMode()
            ProfileIntent.ClearError -> clearError()
        }
    }
    
    private fun loadProfile(userId: String? = null) {
        val userIdToUse = userId ?: currentUserId
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.getProfile(userIdToUse)
                .fold(
                    onSuccess = { profileData ->
                        _profile.value = profileData
                        _state.value = ProfileState.ProfileLoaded(profileData)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao carregar perfil")
                    }
                )
        }
    }
    
    private fun updateProfile(profileData: ProfileData) {
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.updateProfile(currentUserId, profileData)
                .fold(
                    onSuccess = { updatedProfile ->
                        _profile.value = updatedProfile
                        _state.value = ProfileState.ProfileUpdated(updatedProfile)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao atualizar perfil")
                    }
                )
        }
    }
    
    private fun uploadPhoto(photoFile: File, orderSequence: Int) {
        _state.value = ProfileState.UploadingPhoto
        
        viewModelScope.launch {
            profileRepository.uploadProfilePhoto(currentUserId, photoFile, orderSequence)
                .fold(
                    onSuccess = { uploadedPhoto ->
                        // Atualiza perfil localmente
                        val currentProfile = _profile.value
                        if (currentProfile != null) {
                            val updatedPhotos = currentProfile.photos.toMutableList()
                            updatedPhotos.add(uploadedPhoto)
                            val updatedProfile = currentProfile.copy(photos = updatedPhotos.sortedBy { it.orderSequence })
                            _profile.value = updatedProfile
                        }
                        
                        _state.value = ProfileState.PhotoUploaded(uploadedPhoto)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao fazer upload da foto")
                    }
                )
        }
    }
    
    private fun deletePhoto(photoId: String) {
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.deleteProfilePhoto(currentUserId, photoId)
                .fold(
                    onSuccess = {
                        // Remove foto localmente
                        val currentProfile = _profile.value
                        if (currentProfile != null) {
                            val updatedPhotos = currentProfile.photos.filter { it.id != photoId }
                            val updatedProfile = currentProfile.copy(photos = updatedPhotos)
                            _profile.value = updatedProfile
                        }
                        
                        _state.value = ProfileState.PhotoDeleted(photoId)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao deletar foto")
                    }
                )
        }
    }
    
    private fun reorderPhotos(photoOrders: List<Pair<String, Int>>) {
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.reorderPhotos(currentUserId, photoOrders)
                .fold(
                    onSuccess = {
                        // Atualiza ordem localmente
                        val currentProfile = _profile.value
                        if (currentProfile != null) {
                            val reorderedPhotos = currentProfile.photos.map { photo ->
                                val newOrder = photoOrders.find { it.first == photo.id }?.second
                                if (newOrder != null) {
                                    photo.copy(orderSequence = newOrder)
                                } else {
                                    photo
                                }
                            }.sortedBy { it.orderSequence }
                            
                            val updatedProfile = currentProfile.copy(photos = reorderedPhotos)
                            _profile.value = updatedProfile
                        }
                        
                        _state.value = ProfileState.PhotosReordered
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao reordenar fotos")
                    }
                )
        }
    }
    
    private fun loadAvailableInterests() {
        viewModelScope.launch {
            profileRepository.getAvailableInterests()
                .fold(
                    onSuccess = { interests ->
                        _availableInterests.value = interests
                        _state.value = ProfileState.InterestsLoaded(interests)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao carregar interesses")
                    }
                )
        }
    }
    
    private fun updateInterests(interestIds: List<String>) {
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.updateUserInterests(currentUserId, interestIds)
                .fold(
                    onSuccess = {
                        // Atualiza interesses localmente
                        val currentProfile = _profile.value
                        if (currentProfile != null) {
                            val updatedInterests = _availableInterests.value.filter { interest ->
                                interestIds.contains(interest.id)
                            }.map { it.copy(isSelected = true) }
                            
                            val updatedProfile = currentProfile.copy(interests = updatedInterests)
                            _profile.value = updatedProfile
                        }
                        
                        _state.value = ProfileState.InterestsUpdated
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao atualizar interesses")
                    }
                )
        }
    }
    
    private fun loadProfileStats(userId: String? = null) {
        val userIdToUse = userId ?: currentUserId
        
        viewModelScope.launch {
            profileRepository.getProfileStats(userIdToUse)
                .fold(
                    onSuccess = { stats ->
                        _profileStats.value = stats
                        _state.value = ProfileState.StatsLoaded(stats)
                    },
                    onFailure = { exception ->
                        _state.value = ProfileState.Error(exception.message ?: "Erro ao carregar estatísticas")
                    }
                )
        }
    }
    
    private fun updatePrivacySettings(
        showAge: Boolean,
        showDistance: Boolean,
        showOnlineStatus: Boolean,
        profileVisibility: String
    ) {
        _state.value = ProfileState.Loading
        
        viewModelScope.launch {
            profileRepository.updatePrivacySettings(
                currentUserId, showAge, showDistance, showOnlineStatus, profileVisibility
            ).fold(
                onSuccess = {
                    // Atualiza configurações localmente
                    val currentProfile = _profile.value
                    if (currentProfile != null) {
                        val updatedProfile = currentProfile.copy(
                            showAge = showAge,
                            showDistance = showDistance,
                            showOnlineStatus = showOnlineStatus
                        )
                        _profile.value = updatedProfile
                    }
                    
                    _state.value = ProfileState.PrivacyUpdated
                },
                onFailure = { exception ->
                    _state.value = ProfileState.Error(exception.message ?: "Erro ao atualizar privacidade")
                }
            )
        }
    }
    
    private fun startEditMode() {
        _state.value = ProfileState.EditMode
    }
    
    private fun cancelEditMode() {
        _state.value = ProfileState.ViewMode
    }
    
    private fun clearError() {
        _state.value = ProfileState.Idle
    }
    
    // Public helpers for UI
    fun getCurrentUserId(): String = currentUserId
    
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
    
    fun getProfileCompletion(): Int {
        return _profile.value?.profileCompletion ?: 0
    }
    
    fun canUploadMorePhotos(): Boolean {
        return (_profile.value?.photos?.size ?: 0) < 6
    }
    
    fun getMainPhoto(): ProfilePhoto? {
        return _profile.value?.photos?.find { it.isMainPhoto } 
            ?: _profile.value?.photos?.firstOrNull()
    }
    
    fun getSelectedInterestsCount(): Int {
        return _profile.value?.interests?.count { it.isSelected } ?: 0
    }
    
    fun isProfileComplete(): Boolean {
        val profile = _profile.value ?: return false
        return profile.photos.isNotEmpty() && 
               !profile.bio.isNullOrBlank() && 
               profile.interests.size >= 3
    }
    
    // Auto-load profile on init
    init {
        loadProfile()
        loadAvailableInterests()
        loadProfileStats()
    }
}

/**
 * Estados do Profile (MVI pattern)
 */
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object UploadingPhoto : ProfileState()
    object EditMode : ProfileState()
    object ViewMode : ProfileState()
    
    data class ProfileLoaded(val profile: ProfileData) : ProfileState()
    data class ProfileUpdated(val profile: ProfileData) : ProfileState()
    data class PhotoUploaded(val photo: ProfilePhoto) : ProfileState()
    data class PhotoDeleted(val photoId: String) : ProfileState()
    object PhotosReordered : ProfileState()
    data class InterestsLoaded(val interests: List<Interest>) : ProfileState()
    object InterestsUpdated : ProfileState()
    data class StatsLoaded(val stats: ProfileStats) : ProfileState()
    object PrivacyUpdated : ProfileState()
    
    data class Error(val message: String) : ProfileState()
}

/**
 * Intenções do usuário (MVI pattern)
 */
sealed class ProfileIntent {
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data class UpdateProfile(val profileData: ProfileData) : ProfileIntent()
    data class UploadPhoto(val photoFile: File, val orderSequence: Int = 0) : ProfileIntent()
    data class DeletePhoto(val photoId: String) : ProfileIntent()
    data class ReorderPhotos(val photoOrders: List<Pair<String, Int>>) : ProfileIntent()
    object LoadAvailableInterests : ProfileIntent()
    data class UpdateInterests(val interestIds: List<String>) : ProfileIntent()
    data class LoadProfileStats(val userId: String? = null) : ProfileIntent()
    data class UpdatePrivacySettings(
        val showAge: Boolean,
        val showDistance: Boolean,
        val showOnlineStatus: Boolean,
        val profileVisibility: String
    ) : ProfileIntent()
    object StartEditMode : ProfileIntent()
    object CancelEditMode : ProfileIntent()
    object ClearError : ProfileIntent()
}