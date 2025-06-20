package com.rcdnc.cafezinho.navigation

/**
 * Navigation destinations constants following Clean Architecture
 * Organized by feature domains
 */
object NavigationDestinations {
    
    /**
     * Authentication feature routes
     */
    object Auth {
        const val ROUTE = "auth"
        const val LOGIN = "auth/login"
        const val SIGNUP = "auth/signup"
        const val PHONE_VERIFICATION = "auth/phone/{phoneNumber}"
        const val COMPLETE_PROFILE = "auth/complete_profile"
        
        fun phoneVerificationRoute(phoneNumber: String) = "auth/phone/$phoneNumber"
    }
    
    /**
     * Swipe/Discovery feature routes
     */
    object Swipe {
        const val ROUTE = "swipe"
        const val CARDS = "swipe/cards"
        const val USER_DETAIL = "swipe/user/{userId}"
        const val FILTERS = "swipe/filters"
        const val BOOST = "swipe/boost"
        
        fun userDetailRoute(userId: String) = "swipe/user/$userId"
    }
    
    /**
     * Chat/Messaging feature routes
     */
    object Chat {
        const val ROUTE = "chat"
        const val INBOX = "chat/inbox"
        const val CONVERSATION = "chat/conversation/{matchId}"
        const val AUDIO_PLAYER = "chat/audio/{audioId}"
        
        fun conversationRoute(matchId: String) = "chat/conversation/$matchId"
        fun audioPlayerRoute(audioId: String) = "chat/audio/$audioId"
    }
    
    /**
     * Matches/"Who liked you" feature routes
     */
    object Matches {
        const val ROUTE = "matches"
        const val LIKES_LIST = "matches/likes"
        const val MATCH_DETAIL = "matches/match/{matchId}"
        const val SUPER_LIKES = "matches/super_likes"
        
        fun matchDetailRoute(matchId: String) = "matches/match/$matchId"
    }
    
    /**
     * Profile feature routes
     */
    object Profile {
        const val ROUTE = "profile"
        const val VIEW = "profile/view"
        const val EDIT = "profile/edit"
        const val PHOTOS = "profile/photos"
        const val SETTINGS = "profile/settings"
        const val VERIFICATION = "profile/verification"
    }
    
    /**
     * Shared/Common routes
     */
    object Common {
        const val ERROR = "error/{errorType}"
        const val LOADING = "loading"
        const val DEEP_LINK_HANDLER = "deep_link/{action}"
        
        fun errorRoute(errorType: String) = "error/$errorType"
        fun deepLinkRoute(action: String) = "deep_link/$action"
    }
    
    /**
     * Bottom Navigation destinations
     * Maps to existing bottom navigation structure
     */
    object BottomNav {
        const val SWIPE = Swipe.CARDS          // users_fragment
        const val MATCHES = Matches.LIKES_LIST  // users_likes_fragment  
        const val CHAT = Chat.INBOX            // inbox_fragment
        const val PROFILE = Profile.VIEW       // profile_fragment
    }
}