package com.rcdnc.cafezinho.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.features.swipe.presentation.ui.SwipeScreen

/**
 * Main Navigation Host for Cafezinho app
 * Clean Architecture navigation implementation
 */
@Composable
fun CafezinhoNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestinations.BottomNav.SWIPE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth feature navigation
        composable(NavigationDestinations.Auth.LOGIN) {
            PlaceholderScreen(title = "Login Screen")
        }
        
        composable(NavigationDestinations.Auth.SIGNUP) {
            PlaceholderScreen(title = "Signup Screen")
        }
        
        // Swipe feature navigation
        composable(NavigationDestinations.Swipe.CARDS) {
            SwipeScreen(
                onNavigateToChat = { userId ->
                    navController.navigate("${NavigationDestinations.Chat.INBOX}/$userId")
                },
                onNavigateToProfile = {
                    navController.navigate(NavigationDestinations.Profile.VIEW)
                }
            )
        }
        
        // Chat feature navigation
        composable(NavigationDestinations.Chat.INBOX) {
            PlaceholderScreen(title = "Chat Inbox")
        }
        
        // Matches feature navigation
        composable(NavigationDestinations.Matches.LIKES_LIST) {
            PlaceholderScreen(title = "Likes List")
        }
        
        // Profile feature navigation
        composable(NavigationDestinations.Profile.VIEW) {
            PlaceholderScreen(title = "Profile View")
        }
    }
}

/**
 * Placeholder screen for development
 */
@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🚀 $title\n\nCafezinho v2 - Clean Architecture",
            textAlign = TextAlign.Center
        )
    }
}