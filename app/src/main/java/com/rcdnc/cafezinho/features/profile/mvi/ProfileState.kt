package com.rcdnc.cafezinho.features.profile.mvi

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: ProfileData) : ProfileState()
    data class Error(val message: String) : ProfileState()
}