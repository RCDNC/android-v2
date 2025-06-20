package com.rcdnc.cafezinho.features.swipe.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val age: Int,
    val photos: List<String> = emptyList(),
    val bio: String? = null,
    val distance: Float? = null,
    val interests: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val src: String? = null
)