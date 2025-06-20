package com.rcdnc.cafezinho.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect class PlatformApiClient {
    fun createHttpClient(): HttpClient
}

object ApiClient {
    private val platformClient = PlatformApiClient()
    
    val httpClient: HttpClient by lazy {
        platformClient.createHttpClient().config {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
}

object ApiEndpoints {
    const val BASE_URL = "https://api.cafezinho.com"
    const val USERS = "$BASE_URL/users"
    const val MATCHES = "$BASE_URL/matches"
    const val MESSAGES = "$BASE_URL/messages"
    const val SWIPES = "$BASE_URL/swipes"
    const val AUTH = "$BASE_URL/auth"
}