package com.rcdnc.cafezinho.features.profile.domain.model

data class ProfileData(
    val id: String,
    val firstName: String,
    val lastName: String? = null,
    val age: Int,
    val bio: String? = null,
    val website: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val school: String? = null,
    val photos: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val location: String? = null
)