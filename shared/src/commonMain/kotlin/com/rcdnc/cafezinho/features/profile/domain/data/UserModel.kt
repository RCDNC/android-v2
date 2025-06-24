package com.rcdnc.cafezinho.features.profile.domain.data

import com.rcdnc.cafezinho.core.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    var socialType: String = "",
    var socialId: String = "",
    var authToken: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    var website: String = "",
    var username: String = "",
    var gender: String = "",
    var showMeGender: String = "",
    var showOrientation: Int = 0,
    var genderShow: Int = 0,
    var rewind: Int = 0,
    var likesToDay: Int = 0,
    var userPassion: MutableList<String> = mutableListOf(),
    var dateOfBirth: String = "",
    var mainInterest: Int = -1,
    var secondaryInterest: Int = -1,
    var mySchoolId: Int = 0,
    var isFromSkippingPhone: Boolean = false,
    var orientationList: MutableList<SexualOrientation> = mutableListOf()
) : Parcelable
