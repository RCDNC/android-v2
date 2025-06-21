package com.rcdnc.cafezinho.features.auth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.features.auth.presentation.ui.LoginScreen
import com.rcdnc.cafezinho.features.auth.presentation.ui.PhoneVerificationScreen
import com.rcdnc.cafezinho.features.auth.presentation.ui.ProfileCompletionScreen

/**
 * Authentication navigation graph
 * Handles navigation between login, phone verification, and profile completion screens
 */

// Navigation destinations
object AuthDestinations {
    const val LOGIN = "login"
    const val PHONE_VERIFICATION = "phone_verification"
    const val PROFILE_COMPLETION = "profile_completion"
}

@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onAuthComplete: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = AuthDestinations.LOGIN
    ) {
        
        // Login Screen
        composable(AuthDestinations.LOGIN) {
            LoginScreen(
                onNavigateToPhoneVerification = { phoneNumber ->
                    navController.navigate("${AuthDestinations.PHONE_VERIFICATION}/$phoneNumber")
                },
                onNavigateToMain = onAuthComplete
            )
        }
        
        // Phone Verification Screen
        composable("${AuthDestinations.PHONE_VERIFICATION}/{phoneNumber}") { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            
            PhoneVerificationScreen(
                phoneNumber = phoneNumber,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(AuthDestinations.PROFILE_COMPLETION) {
                        // Clear back stack so user can't go back to verification
                        popUpTo(AuthDestinations.LOGIN) { inclusive = false }
                    }
                }
            )
        }
        
        // Profile Completion Screen
        composable(AuthDestinations.PROFILE_COMPLETION) {
            ProfileCompletionScreen(
                onProfileComplete = onAuthComplete,
                onNavigateBack = {
                    // Don't allow going back from profile completion
                    // User must complete profile or start over
                }
            )
        }
    }
}

/**
 * Navigation helper functions for external use
 */
object AuthNavigationActions {
    
    fun navigateToPhoneVerification(
        navController: NavHostController,
        phoneNumber: String
    ) {
        navController.navigate("${AuthDestinations.PHONE_VERIFICATION}/$phoneNumber")
    }
    
    fun navigateToProfileCompletion(navController: NavHostController) {
        navController.navigate(AuthDestinations.PROFILE_COMPLETION) {
            popUpTo(AuthDestinations.LOGIN) { inclusive = false }
        }
    }
    
    fun navigateToLogin(navController: NavHostController) {
        navController.navigate(AuthDestinations.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}