package com.rcdnc.cafezinho.features.auth.domain.model

data class User(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val firstName: String? = null,
    val isVerified: Boolean = false,
    val token: String? = null
)