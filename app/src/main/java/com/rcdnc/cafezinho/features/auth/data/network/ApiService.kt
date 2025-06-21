package com.rcdnc.cafezinho.features.auth.data.network

import com.rcdnc.cafezinho.features.auth.data.model.AuthResponse
import com.rcdnc.cafezinho.features.auth.data.model.LoginRequest
import com.rcdnc.cafezinho.features.auth.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service for authentication endpoints
 * Connects to Laravel API with Sanctum authentication
 */
interface ApiService {
    
    @POST("auth/{provider}")
    suspend fun login(
        @Path("provider") provider: String,
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("register/{provider}")
    suspend fun register(
        @Path("provider") provider: String,
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @POST("logDebug")
    suspend fun debugLogin(
        @Body request: Map<String, String>
    ): Response<AuthResponse>
    
    @DELETE("user/revoking/{id}")
    suspend fun logout(
        @Path("id") userId: Int,
        @Header("Authorization") authToken: String
    ): Response<Unit>
}

/**
 * Base URLs for different environments
 */
object ApiConfig {
    const val DEV_BASE_URL = "https://dev-api.cafezinho.app/api/"
    const val STAGE_BASE_URL = "https://ds3rvbo91hhlz.cloudfront.net/api/"
    const val PROD_BASE_URL = "https://d2pwhpqxnn6p0k.cloudfront.net/api/"
    
    // Default to staging for demo
    const val BASE_URL = STAGE_BASE_URL
}