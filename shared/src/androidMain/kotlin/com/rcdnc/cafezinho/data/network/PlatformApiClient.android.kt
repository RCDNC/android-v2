package com.rcdnc.cafezinho.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*

actual class PlatformApiClient actual constructor() {
    actual fun createHttpClient(): HttpClient {
        return HttpClient(Android) {
            engine {
                connectTimeout = 30_000
                socketTimeout = 30_000
            }
        }
    }
}