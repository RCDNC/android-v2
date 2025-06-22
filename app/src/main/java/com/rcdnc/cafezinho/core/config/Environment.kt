package com.rcdnc.cafezinho.core.config

/**
 * Configuração de ambiente para URLs de produção
 * Resolve Blocker Crítico #2
 */
object Environment {
    
    /**
     * Ambiente atual da aplicação
     */
    enum class Type {
        DEVELOPMENT,
        STAGING,
        PRODUCTION
    }
    
    // Configuração baseada em BuildConfig
    val CURRENT: Type = when {
        // TODO: Configurar via BuildConfig.BUILD_TYPE quando compilar
        true -> Type.DEVELOPMENT  // Forçado para desenvolvimento
        else -> Type.PRODUCTION
    }
    
    /**
     * URLs base por ambiente
     */
    val BASE_URL: String = when (CURRENT) {
        Type.DEVELOPMENT -> "http://10.0.2.2:8000/api/" // Android emulator localhost
        Type.STAGING -> "https://staging-api.cafezinho.app/api/"
        Type.PRODUCTION -> "https://api.cafezinho.app/api/"
    }
    
    /**
     * URLs específicas por ambiente
     */
    val WEB_URL: String = when (CURRENT) {
        Type.DEVELOPMENT -> "http://10.0.2.2:3000/"
        Type.STAGING -> "https://staging.cafezinho.app/"
        Type.PRODUCTION -> "https://cafezinho.app/"
    }
    
    /**
     * WebSocket URLs
     */
    val WEBSOCKET_URL: String = when (CURRENT) {
        Type.DEVELOPMENT -> "ws://10.0.2.2:6001"
        Type.STAGING -> "wss://staging-ws.cafezinho.app"
        Type.PRODUCTION -> "wss://ws.cafezinho.app"
    }
    
    /**
     * CDN URLs para imagens
     */
    val CDN_URL: String = when (CURRENT) {
        Type.DEVELOPMENT -> "http://10.0.2.2:8000/storage/"
        Type.STAGING -> "https://staging-cdn.cafezinho.app/"
        Type.PRODUCTION -> "https://cdn.cafezinho.app/"
    }
    
    /**
     * Configurações de segurança
     */
    val ENABLE_SSL_PINNING: Boolean = CURRENT == Type.PRODUCTION
    val ENABLE_NETWORK_SECURITY: Boolean = CURRENT != Type.DEVELOPMENT
    val ENABLE_LOGGING: Boolean = CURRENT != Type.PRODUCTION
    
    /**
     * Timeouts de rede (em segundos)
     */
    val NETWORK_TIMEOUT: Long = when (CURRENT) {
        Type.DEVELOPMENT -> 60L // Debug mais longo
        Type.STAGING -> 30L
        Type.PRODUCTION -> 15L // Produção mais rápido
    }
    
    /**
     * Configurações de cache
     */
    val CACHE_SIZE_MB: Long = when (CURRENT) {
        Type.DEVELOPMENT -> 50L
        Type.STAGING -> 100L
        Type.PRODUCTION -> 200L
    }
    
    /**
     * URLs para termos e políticas
     */
    val TERMS_URL: String = "$WEB_URL/terms"
    val PRIVACY_URL: String = "$WEB_URL/privacy"
    val SUPPORT_URL: String = "$WEB_URL/support"
    
    /**
     * Configurações de push notifications
     */
    val FCM_SENDER_ID: String = when (CURRENT) {
        Type.DEVELOPMENT -> "123456789" // Dev FCM project
        Type.STAGING -> "987654321" // Staging FCM project  
        Type.PRODUCTION -> "555666777" // Production FCM project
    }
    
    /**
     * Social login configurations
     */
    val GOOGLE_CLIENT_ID: String = when (CURRENT) {
        Type.DEVELOPMENT -> "dev-google-client-id"
        Type.STAGING -> "staging-google-client-id"
        Type.PRODUCTION -> "prod-google-client-id"
    }
    
    val FACEBOOK_APP_ID: String = when (CURRENT) {
        Type.DEVELOPMENT -> "dev-facebook-app-id"
        Type.STAGING -> "staging-facebook-app-id"
        Type.PRODUCTION -> "prod-facebook-app-id"
    }
    
    /**
     * Feature flags por ambiente
     */
    val ENABLE_ANALYTICS: Boolean = CURRENT != Type.DEVELOPMENT
    val ENABLE_CRASHLYTICS: Boolean = CURRENT == Type.PRODUCTION
    val ENABLE_DEBUG_TOOLS: Boolean = CURRENT == Type.DEVELOPMENT
    val ENABLE_PERFORMANCE_MONITORING: Boolean = CURRENT == Type.PRODUCTION
    
    /**
     * Rate limiting
     */
    val API_RATE_LIMIT_PER_MINUTE: Int = when (CURRENT) {
        Type.DEVELOPMENT -> 1000 // Sem limite em dev
        Type.STAGING -> 300
        Type.PRODUCTION -> 100
    }
    
    /**
     * Configuração dinâmica baseada no build
     */
    fun configure(buildType: String, flavor: String = "") {
        // TODO: Implementar configuração dinâmica baseada em BuildConfig
        println("📡 Environment configured: $CURRENT")
        println("🌐 API Base URL: $BASE_URL")
        println("🔒 SSL Pinning: $ENABLE_SSL_PINNING")
        println("📊 Analytics: $ENABLE_ANALYTICS")
    }
}