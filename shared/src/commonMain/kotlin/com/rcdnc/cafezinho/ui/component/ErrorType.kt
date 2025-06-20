package com.rcdnc.cafezinho.ui.component

/**
 * Error types for error state components
 * Based on legacy error screens: activity_custom_error.xml variants
 */
enum class ErrorType {
    /**
     * Network connectivity error
     * Show: WiFi icon, "Sem conexão com a internet", "Tentar novamente"
     * Legacy: activity_custom_error_internet.xml
     */
    NETWORK,
    
    /**
     * General application error
     * Show: Broken cup icon, "Algo deu errado", "Reiniciar"
     * Legacy: activity_custom_error.xml with img_brokencup_transparent
     */
    GENERAL,
    
    /**
     * Server/API error
     * Show: Server icon, "Problema no servidor", "Tentar novamente"
     */
    SERVER,
    
    /**
     * Authentication error
     * Show: Lock icon, "Sessão expirada", "Fazer login"
     */
    AUTH,
    
    /**
     * Permission denied error
     * Show: Shield icon, "Acesso negado", "Verificar permissões"
     */
    PERMISSION,
    
    /**
     * Not found error (404, empty states)
     * Show: Search icon, "Nada encontrado", "Tentar novamente"
     */
    NOT_FOUND
}

/**
 * Error data class with all information needed for error display
 */
data class ErrorInfo(
    val type: ErrorType,
    val title: String,
    val message: String,
    val actionText: String = "Tentar novamente",
    val secondaryActionText: String? = null,
    val exception: Throwable? = null
)