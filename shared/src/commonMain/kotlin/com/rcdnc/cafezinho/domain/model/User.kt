package com.rcdnc.cafezinho.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val age: Int,
    val bio: String? = null,
    val photos: List<String> = emptyList(),
    val location: Location? = null,
    val preferences: UserPreferences? = null,
    val isVerified: Boolean = false,
    val lastActive: Long? = null
)

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val country: String? = null
)

@Serializable
data class UserPreferences(
    val minAge: Int = 18,
    val maxAge: Int = 99,
    val maxDistance: Int = 50,
    val interestedInGender: Gender = Gender.ALL
)

@Serializable
enum class Gender {
    MALE, FEMALE, ALL
}