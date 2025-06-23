package com.rcdnc.cafezinho.features.auth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.features.auth.presentation.ui.LoginScreen

/**
 * Simple Auth Navigation for demo purposes
 * No external dependencies - pure Navigation Compose
 */
@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onAuthComplete: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password, rememberMe ->
                    onAuthComplete()
                }
            )
        }
        
        // Future screens can be added here:
        // composable("phone_verification") { ... }
        // composable("profile_completion") { ... }
    }
}