package com.rcdnc.cafezinho.features.chat.compose.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rcdnc.cafezinho.features.chat.mvi.ChatViewModel
import com.rcdnc.cafezinho.features.chat.presentation.screen.InboxScreen
import com.rcdnc.cafezinho.features.chat.presentation.screen.ConversationScreen
import com.rcdnc.cafezinho.features.chat.presentation.screen.AudioPlayerScreen
import com.rcdnc.cafezinho.navigation.NavigationDestinations

/**
 * Chat feature navigation graph
 * Handles inbox, conversations, and audio player navigation
 */
fun NavGraphBuilder.chatNavigation(
    navController: NavController,
    onNavigateToProfile: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    navigation(
        startDestination = NavigationDestinations.Chat.INBOX,
        route = NavigationDestinations.Chat.ROUTE
    ) {
        // Inbox/Chat list screen
        composable(route = NavigationDestinations.Chat.INBOX) {
            val viewModel: ChatViewModel = hiltViewModel()
            
            InboxScreen(
                viewModel = viewModel,
                onNavigateToConversation = { matchId ->
                    navController.navigate(NavigationDestinations.Chat.conversationRoute(matchId))
                },
                onNavigateToProfile = onNavigateToProfile,
                onRefresh = {
                    // Handle refresh action
                }
            )
        }
        
        // Individual conversation screen
        composable(
            route = NavigationDestinations.Chat.CONVERSATION,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            val viewModel: ChatViewModel = hiltViewModel()
            
            ConversationScreen(
                matchId = matchId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToAudioPlayer = { audioId ->
                    navController.navigate(NavigationDestinations.Chat.audioPlayerRoute(audioId))
                },
                onVideoCallRequested = { matchId ->
                    // Handle video call - could navigate to video call activity or screen
                    // For now, maintaining compatibility with existing VideoActivity
                },
                onReportUser = { userId ->
                    // Handle user reporting
                }
            )
        }
        
        // Audio player screen
        composable(
            route = NavigationDestinations.Chat.AUDIO_PLAYER,
            arguments = listOf(
                navArgument("audioId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val audioId = backStackEntry.arguments?.getString("audioId") ?: ""
            
            AudioPlayerScreen(
                audioId = audioId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAudioFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Navigation helpers for Chat feature
 */
object ChatNavigationActions {
    
    fun navigateToInbox(navController: NavController) {
        navController.navigate(NavigationDestinations.Chat.INBOX) {
            // Clear back stack to avoid multiple instances
            popUpTo(NavigationDestinations.Chat.INBOX) {
                inclusive = true
            }
        }
    }
    
    fun navigateToConversation(navController: NavController, matchId: String) {
        navController.navigate(NavigationDestinations.Chat.conversationRoute(matchId))
    }
    
    fun navigateToAudioPlayer(navController: NavController, audioId: String) {
        navController.navigate(NavigationDestinations.Chat.audioPlayerRoute(audioId))
    }
    
    fun navigateBackFromConversation(navController: NavController) {
        navController.popBackStack(NavigationDestinations.Chat.INBOX, inclusive = false)
    }
}

/**
 * Integration bridge for existing chat Activities and Fragments
 * Allows gradual migration from Activity/Fragment-based to Compose navigation
 */
object ChatNavigationBridge {
    
    /**
     * Convert from Activity/Fragment navigation to Compose navigation
     */
    fun fromActivityToCompose(
        activityDestination: String,
        args: Map<String, String> = emptyMap()
    ): String {
        return when (activityDestination) {
            "InboxFragment", "inbox_fragment" -> NavigationDestinations.Chat.INBOX
            "ChatActivity" -> {
                val matchId = args["matchId"] ?: args["match_id"] ?: ""
                NavigationDestinations.Chat.conversationRoute(matchId)
            }
            "PlayAudioFragment" -> {
                val audioId = args["audioId"] ?: args["audio_id"] ?: ""
                NavigationDestinations.Chat.audioPlayerRoute(audioId)
            }
            else -> NavigationDestinations.Chat.INBOX
        }
    }
    
    /**
     * Convert from Compose navigation to Activity/Fragment navigation
     * Used during transition period
     */
    fun fromComposeToActivity(composeDestination: String): String {
        return when {
            composeDestination == NavigationDestinations.Chat.INBOX -> "InboxFragment"
            composeDestination.startsWith("chat/conversation/") -> "ChatActivity"
            composeDestination.startsWith("chat/audio/") -> "PlayAudioFragment"
            else -> "InboxFragment"
        }
    }
    
    /**
     * Handle chat deep links
     */
    fun handleChatDeepLink(deepLink: String): String {
        return when {
            deepLink.contains("conversation") || deepLink.contains("chat") -> {
                val matchId = extractMatchIdFromDeepLink(deepLink)
                if (matchId.isNotEmpty()) {
                    NavigationDestinations.Chat.conversationRoute(matchId)
                } else {
                    NavigationDestinations.Chat.INBOX
                }
            }
            deepLink.contains("audio") -> {
                val audioId = extractAudioIdFromDeepLink(deepLink)
                if (audioId.isNotEmpty()) {
                    NavigationDestinations.Chat.audioPlayerRoute(audioId)
                } else {
                    NavigationDestinations.Chat.INBOX
                }
            }
            else -> NavigationDestinations.Chat.INBOX
        }
    }
    
    private fun extractMatchIdFromDeepLink(deepLink: String): String {
        // Extract match ID from deep link URL
        return deepLink.substringAfterLast("match=").substringBefore("&").takeIf { it.isNotEmpty() }
            ?: deepLink.substringAfterLast("/").takeIf { it.isNotEmpty() }
            ?: ""
    }
    
    private fun extractAudioIdFromDeepLink(deepLink: String): String {
        // Extract audio ID from deep link URL
        return deepLink.substringAfterLast("audio=").substringBefore("&").takeIf { it.isNotEmpty() }
            ?: deepLink.substringAfterLast("/").takeIf { it.isNotEmpty() }
            ?: ""
    }
}

/**
 * Chat navigation state management
 */
data class ChatNavigationState(
    val currentConversationId: String = "",
    val unreadMessagesCount: Int = 0,
    val isInConversation: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val currentAudioId: String = ""
)

/**
 * Chat navigation effects
 */
sealed class ChatNavigationEffect {
    object NavigateToInbox : ChatNavigationEffect()
    data class NavigateToConversation(val matchId: String) : ChatNavigationEffect()
    data class NavigateToProfile(val userId: String) : ChatNavigationEffect()
    data class NavigateToAudioPlayer(val audioId: String) : ChatNavigationEffect()
    data class NavigateToVideoCall(val matchId: String) : ChatNavigationEffect()
    object NavigateBack : ChatNavigationEffect()
    data class ShowError(val message: String) : ChatNavigationEffect()
    data class ShowNewMessageNotification(val matchId: String, val message: String) : ChatNavigationEffect()
}