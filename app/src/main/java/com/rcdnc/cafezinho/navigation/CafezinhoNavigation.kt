package com.rcdnc.cafezinho.navigation

/**
 * Definições de navegação do Cafezinho
 * Centraliza todas as rotas e argumentos do app
 */
object CafezinhoNavigation {
    
    // Main navigation routes
    const val SWIPE = "swipe"
    const val MATCHES = "matches"
    const val CHAT_LIST = "chat_list"
    const val PROFILE = "profile"
    
    // Chat routes with arguments
    const val CHAT_CONVERSATION = "chat_conversation"
    const val CHAT_WITH_USER = "chat_conversation/{userId}"
    
    // Profile routes
    const val EDIT_PROFILE = "edit_profile"
    const val PROFILE_SETTINGS = "profile_settings"
    
    // Match routes
    const val MATCH_DETAIL = "match_detail/{userId}"
    const val MATCH_FOUND = "match_found/{userId}"
    
    // Swipe routes
    const val USER_DETAIL = "user_detail/{userId}"
    const val SWIPE_FILTERS = "swipe_filters"
    
    // Auth routes (já existentes, mas para completude)
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    
    // Helper functions para navigation com argumentos
    fun navigateToChatWithUser(userId: String) = "chat_conversation/$userId"
    fun navigateToMatchDetail(userId: String) = "match_detail/$userId"
    fun navigateToMatchFound(userId: String) = "match_found/$userId"
    fun navigateToUserDetail(userId: String) = "user_detail/$userId"
}

/**
 * Arguments keys for navigation
 */
object NavigationArgs {
    const val USER_ID = "userId"
    const val MATCH_ID = "matchId"
    const val CONVERSATION_ID = "conversationId"
}

/**
 * Deep link patterns para notificações
 */
object DeepLinks {
    const val CHAT_DEEP_LINK = "cafezinho://chat/{userId}"
    const val MATCH_DEEP_LINK = "cafezinho://match/{userId}"
    const val PROFILE_DEEP_LINK = "cafezinho://profile/{userId}"
    const val SWIPE_DEEP_LINK = "cafezinho://discover"
}