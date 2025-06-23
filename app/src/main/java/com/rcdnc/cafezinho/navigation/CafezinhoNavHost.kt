package com.rcdnc.cafezinho.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rcdnc.cafezinho.features.swipe.presentation.ui.SwipeScreen
import com.rcdnc.cafezinho.features.chat.presentation.ui.ChatListScreen
import com.rcdnc.cafezinho.features.chat.presentation.ui.ChatScreen
import com.rcdnc.cafezinho.features.matches.presentation.ui.MatchesScreen
import com.rcdnc.cafezinho.features.profile.presentation.ui.ProfileScreen
import com.rcdnc.cafezinho.features.profile.presentation.ui.EditProfileScreen

/**
 * NavHost principal do Cafezinho
 * Gerencia toda a navegação do app
 */
@Composable
fun CafezinhoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = CafezinhoNavigation.SWIPE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        
        // === MAIN TABS ===
        
        // Swipe/Descobrir
        composable(
            route = CafezinhoNavigation.SWIPE,
            deepLinks = listOf(navDeepLink { uriPattern = DeepLinks.SWIPE_DEEP_LINK })
        ) {
            SwipeScreen(
                onUserClick = { user ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToUserDetail(user.id)
                    )
                },
                onMatchFound = { user ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToMatchFound(user.id)
                    )
                },
                onFiltersClick = {
                    navController.navigate(CafezinhoNavigation.SWIPE_FILTERS)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Matches
        composable(CafezinhoNavigation.MATCHES) {
            MatchesScreen(
                onMatchClick = { match ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToChatWithUser(match.otherUserId)
                    )
                },
                onMatchDetail = { match ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToMatchDetail(match.otherUserId)
                    )
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Chat List
        composable(CafezinhoNavigation.CHAT_LIST) {
            ChatListScreen(
                onConversationClick = { conversation ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToChatWithUser(conversation.otherUserId)
                    )
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Profile
        composable(CafezinhoNavigation.PROFILE) {
            ProfileScreen(
                onEditClick = {
                    navController.navigate(CafezinhoNavigation.EDIT_PROFILE)
                },
                onSettingsClick = {
                    navController.navigate(CafezinhoNavigation.PROFILE_SETTINGS)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // === DETAIL SCREENS ===
        
        // Chat Conversation
        composable(
            route = CafezinhoNavigation.CHAT_WITH_USER,
            arguments = listOf(
                navArgument(NavigationArgs.USER_ID) { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = DeepLinks.CHAT_DEEP_LINK })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(NavigationArgs.USER_ID) ?: ""
            ChatScreen(
                userId = userId,
                onBackClick = {
                    navController.navigateUp()
                },
                onUserProfileClick = { userId ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToUserDetail(userId.toString())
                    )
                }
            )
        }
        
        // Edit Profile
        composable(CafezinhoNavigation.EDIT_PROFILE) {
            EditProfileScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSaveSuccess = {
                    navController.navigateUp()
                }
            )
        }
        
        // Match Detail
        composable(
            route = CafezinhoNavigation.MATCH_DETAIL,
            arguments = listOf(
                navArgument(NavigationArgs.USER_ID) { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = DeepLinks.MATCH_DEEP_LINK })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(NavigationArgs.USER_ID) ?: ""
            MatchDetailScreen(
                userId = userId,
                onBackClick = {
                    navController.navigateUp()
                },
                onStartChatClick = { userId ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToChatWithUser(userId.toString())
                    ) {
                        // Clear back stack até matches
                        popUpTo(CafezinhoNavigation.MATCHES)
                    }
                }
            )
        }
        
        // User Detail (para Swipe)
        composable(
            route = CafezinhoNavigation.USER_DETAIL,
            arguments = listOf(
                navArgument(NavigationArgs.USER_ID) { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = DeepLinks.PROFILE_DEEP_LINK })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(NavigationArgs.USER_ID) ?: ""
            UserDetailScreen(
                userId = userId,
                onBackClick = {
                    navController.navigateUp()
                },
                onLikeClick = { user ->
                    // Volta para swipe após like
                    navController.navigateUp()
                },
                onDislikeClick = { user ->
                    // Volta para swipe após dislike
                    navController.navigateUp()
                }
            )
        }
        
        // Match Found (success screen)
        composable(
            route = CafezinhoNavigation.MATCH_FOUND,
            arguments = listOf(
                navArgument(NavigationArgs.USER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(NavigationArgs.USER_ID) ?: ""
            MatchFoundScreen(
                userId = userId,
                onContinueSwipingClick = {
                    navController.navigate(CafezinhoNavigation.SWIPE) {
                        popUpTo(CafezinhoNavigation.SWIPE) { inclusive = true }
                    }
                },
                onStartChatClick = { userId ->
                    navController.navigate(
                        CafezinhoNavigation.navigateToChatWithUser(userId.toString())
                    ) {
                        popUpTo(CafezinhoNavigation.SWIPE)
                    }
                }
            )
        }
        
        // Swipe Filters
        composable(CafezinhoNavigation.SWIPE_FILTERS) {
            SwipeFiltersScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onApplyFilters = { filters ->
                    navController.navigateUp()
                }
            )
        }
    }
}

// Temporary placeholder screens até as features serem integradas
@Composable
private fun MatchDetailScreen(
    userId: String,
    onBackClick: () -> Unit,
    onStartChatClick: (Any) -> Unit
) {
    // TODO: Implementar quando MatchDetailScreen for criada
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Match Detail: $userId")
    }
}

@Composable
private fun UserDetailScreen(
    userId: String,
    onBackClick: () -> Unit,
    onLikeClick: (Any) -> Unit,
    onDislikeClick: (Any) -> Unit
) {
    // TODO: Implementar quando UserDetailScreen for criada
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("User Detail: $userId")
    }
}

@Composable
private fun MatchFoundScreen(
    userId: String,
    onContinueSwipingClick: () -> Unit,
    onStartChatClick: (Any) -> Unit
) {
    // TODO: Implementar quando MatchFoundScreen for criada
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Match Found: $userId")
    }
}

@Composable
private fun SwipeFiltersScreen(
    onBackClick: () -> Unit,
    onApplyFilters: (Any) -> Unit
) {
    // TODO: Implementar quando SwipeFiltersScreen for criada
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Swipe Filters")
    }
}