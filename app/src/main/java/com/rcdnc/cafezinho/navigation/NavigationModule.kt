package com.rcdnc.cafezinho.navigation

import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Navigation Module for Dependency Injection
 * Provides NavController and navigation-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    
    /**
     * Provides navigation destination constants
     * Used for consistent routing across features
     */
    @Provides
    @Singleton
    fun provideNavigationDestinations(): NavigationDestinations = NavigationDestinations
}

/**
 * Activity-scoped navigation module
 * Provides instances that should be scoped to activity lifecycle
 */
@Module
@InstallIn(ActivityComponent::class)
object ActivityNavigationModule {
    
    /**
     * Provides NavHostController for activity scope
     * Each activity gets its own NavController instance
     */
    @Provides
    @ActivityScoped
    fun provideNavHostController(): NavHostController? {
        // NavController is created in Composable context
        // This provides a holder for DI when needed
        return null
    }
}

/**
 * Qualifiers for different navigation contexts
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainNavController

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FeatureNavController

/**
 * Navigation Manager for coordinating between features
 */
@Singleton
class NavigationManager @Inject constructor() {
    
    private var _navController: NavHostController? = null
    
    fun setNavController(navController: NavHostController) {
        _navController = navController
    }
    
    fun getNavController(): NavHostController? = _navController
    
    /**
     * Navigate to specific feature destinations
     */
    fun navigateToAuth() {
        _navController?.navigate(NavigationDestinations.Auth.LOGIN)
    }
    
    fun navigateToSwipe() {
        _navController?.navigate(NavigationDestinations.Swipe.CARDS)
    }
    
    fun navigateToChat() {
        _navController?.navigate(NavigationDestinations.Chat.INBOX)
    }
    
    fun navigateToMatches() {
        _navController?.navigate(NavigationDestinations.Matches.LIKES_LIST)
    }
    
    fun navigateToProfile() {
        _navController?.navigate(NavigationDestinations.Profile.VIEW)
    }
    
    /**
     * Navigate with arguments
     */
    fun navigateToUserDetail(userId: String) {
        _navController?.navigate(NavigationDestinations.Swipe.userDetailRoute(userId))
    }
    
    fun navigateToConversation(matchId: String) {
        _navController?.navigate(NavigationDestinations.Chat.conversationRoute(matchId))
    }
    
    fun navigateToMatchDetail(matchId: String) {
        _navController?.navigate(NavigationDestinations.Matches.matchDetailRoute(matchId))
    }
    
    /**
     * Navigation utilities
     */
    fun popBackStack(): Boolean {
        return _navController?.popBackStack() ?: false
    }
    
    fun navigateUp(): Boolean {
        return _navController?.navigateUp() ?: false
    }
    
    /**
     * Deep link handling
     */
    fun handleDeepLink(deepLink: String) {
        when {
            deepLink.contains("user") -> {
                val userId = extractUserIdFromDeepLink(deepLink)
                if (userId != null) {
                    navigateToUserDetail(userId)
                }
            }
            deepLink.contains("chat") -> {
                val matchId = extractMatchIdFromDeepLink(deepLink)
                if (matchId != null) {
                    navigateToConversation(matchId)
                }
            }
            // Add more deep link patterns as needed
        }
    }
    
    private fun extractUserIdFromDeepLink(deepLink: String): String? {
        // Implement deep link parsing logic
        return deepLink.substringAfterLast("/").takeIf { it.isNotEmpty() }
    }
    
    private fun extractMatchIdFromDeepLink(deepLink: String): String? {
        // Implement deep link parsing logic
        return deepLink.substringAfterLast("/").takeIf { it.isNotEmpty() }
    }
}