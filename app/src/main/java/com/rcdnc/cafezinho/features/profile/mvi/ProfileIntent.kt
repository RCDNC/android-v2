package com.rcdnc.cafezinho.features.profile.mvi

import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData

sealed class ProfileIntent {
    object LoadProfile : ProfileIntent()
    data class UpdateProfile(val profile: ProfileData) : ProfileIntent()
    data class UploadPhoto(val photoUri: String) : ProfileIntent()
    data class DeletePhoto(val photoUrl: String) : ProfileIntent()
}