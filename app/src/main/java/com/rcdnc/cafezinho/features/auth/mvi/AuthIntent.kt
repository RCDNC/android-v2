package com.rcdnc.cafezinho.features.auth.mvi

sealed class AuthIntent {
    data class Login(val phone: String, val otp: String) : AuthIntent()
    data class Signup(val phone: String) : AuthIntent()
    data class VerifyOtp(val phone: String, val otp: String) : AuthIntent()
    object Logout : AuthIntent()
}