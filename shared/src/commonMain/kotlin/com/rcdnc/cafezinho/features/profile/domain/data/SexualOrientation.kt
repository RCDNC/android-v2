package com.rcdnc.cafezinho.features.profile.domain.data

import com.rcdnc.cafezinho.core.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SexualOrientation(

    val id: String,

    val title: String,

    val description: String,

    @Transient
    var isCheck: Boolean = false

) : Parcelable