package com.rcdnc.cafezinho.features.matches.compose.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rcdnc.cafezinho.features.matches.mvi.MatchesViewModel
import com.rcdnc.cafezinho.features.matches.presentation.screen.MatchesListScreen
import com.rcdnc.cafezinho.features.matches.presentation.screen.MatchDetailScreen
import com.rcdnc.cafezinho.features.matches.presentation.screen.SuperLikesScreen
import com.rcdnc.cafezinho.navigation.NavigationDestinations

/**
 * Matches feature navigation graph
 * Handles "Who liked you", matches list, and super likes navigation
 */
fun NavGraphBuilder.matchesNavigation(
    navController: NavController,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    navigation(
        startDestination = NavigationDestinations.Matches.LIKES_LIST,
        route = NavigationDestinations.Matches.ROUTE
    ) {
        // Matches/Likes list screen
        composable(route = NavigationDestinations.Matches.LIKES_LIST) {
            val viewModel: MatchesViewModel = hiltViewModel()
            
            MatchesListScreen(
                viewModel = viewModel,
                onNavigateToMatchDetail = { matchId ->
                    navController.navigate(NavigationDestinations.Matches.matchDetailRoute(matchId))
                },
                onNavigateToChat = onNavigateToChat,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToSuperLikes = {
                    navController.navigate(NavigationDestinations.Matches.SUPER_LIKES)
                },
                onLikeBack = { matchId ->
                    // Handle like back action
                    onNavigateToChat(matchId)
                },
                onPassUser = { userId ->
                    // Handle pass action
                }
            )
        }
        
        // Match detail screen
        composable(
            route = NavigationDestinations.Matches.MATCH_DETAIL,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            val viewModel: MatchesViewModel = hiltViewModel()
            
            MatchDetailScreen(
                matchId = matchId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = {
                    onNavigateToChat(matchId)
                },
                onNavigateToProfile = onNavigateToProfile,
                onUnmatch = {
                    // Handle unmatch and navigate back
                    navController.popBackStack()
                }
            )
        }
        
        // Super likes screen
        composable(route = NavigationDestinations.Matches.SUPER_LIKES) {
            val viewModel: MatchesViewModel = hiltViewModel()
            
            SuperLikesScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = onNavigateToProfile,
                onLikeBack = { matchId ->
                    onNavigateToChat(matchId)
                },
                onPassUser = { userId ->
                    // Handle pass action
                }
            )
        }
    }
}

/**
 * Navigation helpers for Matches feature
 */
object MatchesNavigationActions {
    
    fun navigateToMatches(navController: NavController) {
        navController.navigate(NavigationDestinations.Matches.LIKES_LIST) {
            // Clear back stack to avoid multiple instances
            popUpTo(NavigationDestinations.Matches.LIKES_LIST) {
                inclusive = true
            }
        }
    }
    
    fun navigateToMatchDetail(navController: NavController, matchId: String) {
        navController.navigate(NavigationDestinations.Matches.matchDetailRoute(matchId))
    }
    
    fun navigateToSuperLikes(navController: NavController) {
        navController.navigate(NavigationDestinations.Matches.SUPER_LIKES)
    }
    
    fun navigateBackFromMatch(navController: NavController) {
        navController.popBackStack(NavigationDestinations.Matches.LIKES_LIST, inclusive = false)
    }
}

/**
 * Integration bridge for existing matches Activities and Fragments
 * Allows gradual migration from Fragment-based to Compose navigation
 */
object MatchesNavigationBridge {
    
    /**
     * Convert from Fragment navigation to Compose navigation
     */
    fun fromFragmentToCompose(
        fragmentDestination: String,
        args: Map<String, String> = emptyMap()
    ): String {
        return when (fragmentDestination) {
            "UserLikesFragment", "users_likes_fragment" -> NavigationDestinations.Matches.LIKES_LIST
            "MatchFragment" -> {
                val matchId = args["matchId"] ?: args["match_id"] ?: ""
                NavigationDestinations.Matches.matchDetailRoute(matchId)
            }
            "SuperLikesFragment" -> NavigationDestinations.Matches.SUPER_LIKES
            else -> NavigationDestinations.Matches.LIKES_LIST
        }
    }
    
    /**
     * Convert from Compose navigation to Fragment navigation
     * Used during transition period
     */
    fun fromComposeToFragment(composeDestination: String): String {
        return when {
            composeDestination == NavigationDestinations.Matches.LIKES_LIST -> "UserLikesFragment"
            composeDestination.startsWith("matches/match/") -> "MatchFragment"
            composeDestination == NavigationDestinations.Matches.SUPER_LIKES -> "SuperLikesFragment"
            else -> "UserLikesFragment"
        }
    }
    
    /**
     * Handle matches deep links
     */
    fun handleMatchesDeepLink(deepLink: String): String {
        return when {
            deepLink.contains("match") -> {
                val matchId = extractMatchIdFromDeepLink(deepLink)
                if (matchId.isNotEmpty()) {
                    NavigationDestinations.Matches.matchDetailRoute(matchId)
                } else {
                    NavigationDestinations.Matches.LIKES_LIST
                }
            }
            deepLink.contains("super") || deepLink.contains("superlike") -> {
                NavigationDestinations.Matches.SUPER_LIKES
            }
            deepLink.contains("likes") || deepLink.contains("who_liked") -> {
                NavigationDestinations.Matches.LIKES_LIST
            }
            else -> NavigationDestinations.Matches.LIKES_LIST
        }
    }
    
    private fun extractMatchIdFromDeepLink(deepLink: String): String {
        // Extract match ID from deep link URL
        return deepLink.substringAfterLast("match=").substringBefore("&").takeIf { it.isNotEmpty() }
            ?: deepLink.substringAfterLast("/").takeIf { it.isNotEmpty() }
            ?: ""
    }
}

/**
 * Matches navigation state management
 */
data class MatchesNavigationState(
    val currentMatchId: String = "",
    val totalLikesCount: Int = 0,
    val superLikesCount: Int = 0,
    val isInMatchDetail: Boolean = false,
    val hasNewMatches: Boolean = false
)

/**
 * Matches navigation effects
 */
sealed class MatchesNavigationEffect {
    object NavigateToMatches : MatchesNavigationEffect()
    data class NavigateToMatchDetail(val matchId: String) : MatchesNavigationEffect()
    data class NavigateToChat(val matchId: String) : MatchesNavigationEffect()
    data class NavigateToProfile(val userId: String) : MatchesNavigationEffect()
    object NavigateToSuperLikes : MatchesNavigationEffect()
    object NavigateBack : MatchesNavigationEffect()
    data class ShowMatchCelebration(val matchId: String) : MatchesNavigationEffect()
    data class ShowUnmatchConfirmation(val matchId: String) : MatchesNavigationEffect()
    data class ShowError(val message: String) : MatchesNavigationEffect()
    data class ShowNewMatchNotification(val matchId: String, val userName: String) : MatchesNavigationEffect()
}