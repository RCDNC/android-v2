package com.rcdnc.cafezinho.features.auth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.features.auth.presentation.ui.*
import com.rcdnc.cafezinho.features.main.presentation.ui.MainAppScreen

/**
 * Navegação principal do app - Fluxo completo
 * Splash → Onboarding → Login → Tutorial → Main App
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding (5 slides)
        composable("onboarding") {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onSkip = {
                    navController.navigate("login")
                }
            )
        }
        
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password, rememberMe ->
                    // Handle login logic here
                    navController.navigate("tutorial") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        // Tutorial (como usar o app)
        composable("tutorial") {
            TutorialScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("tutorial") { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("main") {
                        popUpTo("tutorial") { inclusive = true }
                    }
                }
            )
        }
        
        // Main App
        composable("main") {
            MainAppScreen(
                onLogout = {
                    navController.navigate("splash") {
                        popUpTo(0) // Clear all back stack
                    }
                }
            )
        }
    }
}