package com.rcdnc.cafezinho.features.swipe.compose.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rcdnc.cafezinho.features.swipe.mvi.SwipeViewModel
import com.rcdnc.cafezinho.features.swipe.presentation.screen.SwipeScreen
import com.rcdnc.cafezinho.features.swipe.presentation.screen.UserDetailScreen
import com.rcdnc.cafezinho.features.swipe.presentation.screen.FiltersScreen
import com.rcdnc.cafezinho.features.swipe.presentation.screen.BoostScreen
import com.rcdnc.cafezinho.navigation.NavigationDestinations

/**
 * Swipe feature navigation graph
 * Handles navigation for card swiping, user details, filters, and boost functionality
 */
fun NavGraphBuilder.swipeNavigation(
    navController: NavController,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    // Main swipe/cards screen
    composable(route = NavigationDestinations.Swipe.CARDS) {
        val viewModel: SwipeViewModel = hiltViewModel()
        
        SwipeScreen(
            viewModel = viewModel,
            onNavigateToUserDetail = { userId ->
                navController.navigate(NavigationDestinations.Swipe.userDetailRoute(userId))
            },
            onNavigateToFilters = {
                navController.navigate(NavigationDestinations.Swipe.FILTERS)
            },
            onNavigateToBoost = {
                navController.navigate(NavigationDestinations.Swipe.BOOST)
            },
            onMatchCreated = { matchId ->
                // Navigate to chat when a match is created
                onNavigateToChat(matchId)
            }
        )
    }
    
    // User detail screen
    composable(
        route = NavigationDestinations.Swipe.USER_DETAIL,
        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        
        UserDetailScreen(
            userId = userId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onLikeUser = {
                // Handle like and potentially navigate to match screen
                navController.popBackStack()
            },
            onSuperLikeUser = {
                // Handle super like
                navController.popBackStack()
            },
            onPassUser = {
                // Handle pass/nope
                navController.popBackStack()
            },
            onReportUser = {
                // Navigate to report screen (could be a dialog)
                // For now, handle in-place
            }
        )
    }
    
    // Filters screen
    composable(route = NavigationDestinations.Swipe.FILTERS) {
        FiltersScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onFiltersApplied = {
                navController.popBackStack()
            }
        )
    }
    
    // Boost screen
    composable(route = NavigationDestinations.Swipe.BOOST) {
        BoostScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onBoostPurchased = {
                navController.popBackStack()
            }
        )
    }
}

/**
 * Navigation helpers for Swipe feature
 */
object SwipeNavigationActions {
    
    fun navigateToUserDetail(navController: NavController, userId: String) {
        navController.navigate(NavigationDestinations.Swipe.userDetailRoute(userId))
    }
    
    fun navigateToFilters(navController: NavController) {
        navController.navigate(NavigationDestinations.Swipe.FILTERS)
    }
    
    fun navigateToBoost(navController: NavController) {
        navController.navigate(NavigationDestinations.Swipe.BOOST)
    }
    
    fun navigateToSwipeCards(navController: NavController) {
        navController.navigate(NavigationDestinations.Swipe.CARDS) {
            // Clear back stack to avoid multiple instances
            popUpTo(NavigationDestinations.Swipe.CARDS) {
                inclusive = true
            }
        }
    }
}

/**
 * Integration bridge for existing XML navigation
 * Allows gradual migration from Fragment-based to Compose navigation
 */
object SwipeNavigationBridge {
    
    /**
     * Convert from Fragment navigation to Compose navigation
     */
    fun fromFragmentToCompose(
        fragmentDestination: String,
        args: Map<String, String> = emptyMap()
    ): String {
        return when (fragmentDestination) {
            "users_fragment" -> NavigationDestinations.Swipe.CARDS
            "user_detail" -> {
                val userId = args["userId"] ?: ""
                NavigationDestinations.Swipe.userDetailRoute(userId)
            }
            "filters_dialog" -> NavigationDestinations.Swipe.FILTERS
            "boost_screen" -> NavigationDestinations.Swipe.BOOST
            else -> NavigationDestinations.Swipe.CARDS
        }
    }
    
    /**
     * Convert from Compose navigation to Fragment navigation
     * Used during transition period
     */
    fun fromComposeToFragment(composeDestination: String): String {
        return when {
            composeDestination == NavigationDestinations.Swipe.CARDS -> "users_fragment"
            composeDestination.startsWith("swipe/user/") -> "user_detail"
            composeDestination == NavigationDestinations.Swipe.FILTERS -> "filters_dialog"
            composeDestination == NavigationDestinations.Swipe.BOOST -> "boost_screen"
            else -> "users_fragment"
        }
    }
}