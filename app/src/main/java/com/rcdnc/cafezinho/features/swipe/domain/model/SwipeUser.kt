package com.rcdnc.cafezinho.features.swipe.domain.model

/**
 * Modelo de domínio para usuários na tela de Swipe/Descobrir
 * Baseado na API Laravel existente
 */
data class SwipeUser(
    val id: String,
    val firstName: String,
    val lastName: String? = null,
    val age: Int,
    val bio: String? = null,
    val location: String? = null,
    val distance: String? = null,
    val photos: List<SwipeUserPhoto> = emptyList(),
    val interests: List<String> = emptyList(),
    val jobTitle: String? = null,
    val company: String? = null,
    val school: String? = null,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val isOnline: Boolean = false,
    val lastSeen: Long? = null,
    val rating: Double = 0.0,
    val profileCompletion: Int = 0,
    val mutualConnections: Int = 0,
    val mutualInterests: List<String> = emptyList()
)

/**
 * Foto do usuário na tela de swipe
 */
data class SwipeUserPhoto(
    val id: String,
    val url: String,
    val orderSequence: Int = 0,
    val isMainPhoto: Boolean = false
)

/**
 * Resultado de uma ação de swipe
 */
data class SwipeResult(
    val action: SwipeAction,
    val user: SwipeUser,
    val isMatch: Boolean = false,
    val matchData: MatchData? = null
)

/**
 * Tipos de ação no swipe
 */
enum class SwipeAction {
    LIKE,
    DISLIKE,
    SUPER_LIKE,
    REWIND
}

/**
 * Dados do match quando há match mútuo
 */
data class MatchData(
    val matchId: String,
    val timestamp: Long,
    val message: String? = null
)

/**
 * Filtros para descoberta de usuários
 */
data class SwipeFilters(
    val minAge: Int = 18,
    val maxAge: Int = 80,
    val maxDistance: Int = 100,
    val genderPreference: String = "all",
    val showOnlineOnly: Boolean = false,
    val showVerifiedOnly: Boolean = false,
    val requiredInterests: List<String> = emptyList()
)

/**
 * Métricas e limites do usuário
 */
data class SwipeMetrics(
    val dailyLikesUsed: Int = 0,
    val dailyLikesLimit: Int = 100,
    val superLikesUsed: Int = 0,
    val superLikesLimit: Int = 5,
    val rewindsUsed: Int = 0,
    val rewindsLimit: Int = 3,
    val isPremium: Boolean = false,
    val canUseRewind: Boolean = false,
    val canUseSuperLike: Boolean = true
)