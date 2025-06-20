package com.rcdnc.cafezinho.features.auth.compose.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rcdnc.cafezinho.features.auth.mvi.AuthViewModel
import com.rcdnc.cafezinho.features.auth.presentation.screen.LoginScreen
import com.rcdnc.cafezinho.features.auth.presentation.screen.SignupScreen
import com.rcdnc.cafezinho.features.auth.presentation.screen.PhoneVerificationScreen
import com.rcdnc.cafezinho.features.auth.presentation.screen.CompleteProfileScreen
import com.rcdnc.cafezinho.navigation.NavigationDestinations

/**
 * Authentication feature navigation graph
 * Handles login, signup, phone verification, and profile completion flows
 */
fun NavGraphBuilder.authNavigation(
    navController: NavController,
    onNavigateToSwipe: () -> Unit
) {
    navigation(
        startDestination = NavigationDestinations.Auth.LOGIN,
        route = NavigationDestinations.Auth.ROUTE
    ) {
        // Login screen
        composable(route = NavigationDestinations.Auth.LOGIN) {
            val viewModel: AuthViewModel = hiltViewModel()
            
            LoginScreen(
                viewModel = viewModel,
                onNavigateToSignup = {
                    navController.navigate(NavigationDestinations.Auth.SIGNUP)
                },
                onNavigateToSwipe = onNavigateToSwipe,
                onNavigateToPhoneVerification = { phoneNumber ->
                    navController.navigate(
                        NavigationDestinations.Auth.phoneVerificationRoute(phoneNumber)
                    )
                }
            )
        }
        
        // Signup screen
        composable(route = NavigationDestinations.Auth.SIGNUP) {
            val viewModel: AuthViewModel = hiltViewModel()
            
            SignupScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToPhoneVerification = { phoneNumber ->
                    navController.navigate(
                        NavigationDestinations.Auth.phoneVerificationRoute(phoneNumber)
                    )
                },
                onNavigateToCompleteProfile = {
                    navController.navigate(NavigationDestinations.Auth.COMPLETE_PROFILE)
                }
            )
        }
        
        // Phone verification screen
        composable(
            route = NavigationDestinations.Auth.PHONE_VERIFICATION,
            arguments = listOf(
                navArgument("phoneNumber") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val viewModel: AuthViewModel = hiltViewModel()
            
            PhoneVerificationScreen(
                phoneNumber = phoneNumber,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(NavigationDestinations.Auth.COMPLETE_PROFILE) {
                        // Remove phone verification from back stack
                        popUpTo(NavigationDestinations.Auth.PHONE_VERIFICATION) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSwipe = onNavigateToSwipe
            )
        }
        
        // Complete profile screen (for new users)
        composable(route = NavigationDestinations.Auth.COMPLETE_PROFILE) {
            val viewModel: AuthViewModel = hiltViewModel()
            
            CompleteProfileScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProfileComplete = onNavigateToSwipe,
                onSkipForNow = onNavigateToSwipe
            )
        }
    }
}

/**
 * Navigation helpers for Auth feature
 */
object AuthNavigationActions {
    
    fun navigateToLogin(navController: NavController) {
        navController.navigate(NavigationDestinations.Auth.LOGIN) {
            // Clear back stack when navigating to login
            popUpTo(0) {
                inclusive = true
            }
        }
    }
    
    fun navigateToSignup(navController: NavController) {
        navController.navigate(NavigationDestinations.Auth.SIGNUP)
    }
    
    fun navigateToPhoneVerification(navController: NavController, phoneNumber: String) {
        navController.navigate(NavigationDestinations.Auth.phoneVerificationRoute(phoneNumber))
    }
    
    fun navigateToCompleteProfile(navController: NavController) {
        navController.navigate(NavigationDestinations.Auth.COMPLETE_PROFILE)
    }
    
    fun clearAuthStackAndNavigateToSwipe(
        navController: NavController,
        onNavigateToSwipe: () -> Unit
    ) {
        // Clear entire auth stack and navigate to main app
        navController.popBackStack(NavigationDestinations.Auth.ROUTE, inclusive = true)
        onNavigateToSwipe()
    }
}

/**
 * Integration bridge for existing authentication Activities
 * Allows gradual migration from Activity-based to Compose navigation
 */
object AuthNavigationBridge {
    
    /**
     * Convert from Activity navigation to Compose navigation
     */
    fun fromActivityToCompose(
        activityDestination: String,
        args: Map<String, String> = emptyMap()
    ): String {
        return when (activityDestination) {
            "LoginActivity" -> NavigationDestinations.Auth.LOGIN
            "SignupActivity" -> NavigationDestinations.Auth.SIGNUP
            "PhoneVerificationActivity" -> {
                val phoneNumber = args["phoneNumber"] ?: ""
                NavigationDestinations.Auth.phoneVerificationRoute(phoneNumber)
            }
            "CompleteProfileActivity" -> NavigationDestinations.Auth.COMPLETE_PROFILE
            else -> NavigationDestinations.Auth.LOGIN
        }
    }
    
    /**
     * Convert from Compose navigation to Activity navigation
     * Used during transition period when some screens are still Activities
     */
    fun fromComposeToActivity(composeDestination: String): String {
        return when {
            composeDestination == NavigationDestinations.Auth.LOGIN -> "LoginActivity"
            composeDestination == NavigationDestinations.Auth.SIGNUP -> "SignupActivity"
            composeDestination.startsWith("auth/phone/") -> "PhoneVerificationActivity"
            composeDestination == NavigationDestinations.Auth.COMPLETE_PROFILE -> "CompleteProfileActivity"
            else -> "LoginActivity"
        }
    }
    
    /**
     * Handle deep link authentication flows
     */
    fun handleAuthDeepLink(deepLink: String): String {
        return when {
            deepLink.contains("login") -> NavigationDestinations.Auth.LOGIN
            deepLink.contains("signup") -> NavigationDestinations.Auth.SIGNUP
            deepLink.contains("verify") -> {
                // Extract phone number from deep link if available
                val phoneNumber = extractPhoneFromDeepLink(deepLink)
                if (phoneNumber.isNotEmpty()) {
                    NavigationDestinations.Auth.phoneVerificationRoute(phoneNumber)
                } else {
                    NavigationDestinations.Auth.LOGIN
                }
            }
            deepLink.contains("complete") -> NavigationDestinations.Auth.COMPLETE_PROFILE
            else -> NavigationDestinations.Auth.LOGIN
        }
    }
    
    private fun extractPhoneFromDeepLink(deepLink: String): String {
        // Extract phone number from deep link URL
        // Implementation depends on deep link format
        return deepLink.substringAfterLast("phone=").substringBefore("&").takeIf { it.isNotEmpty() } ?: ""
    }
}

/**
 * Auth navigation state management
 */
data class AuthNavigationState(
    val isLoggedIn: Boolean = false,
    val needsPhoneVerification: Boolean = false,
    val needsProfileCompletion: Boolean = false,
    val currentPhoneNumber: String = "",
    val authToken: String = ""
)

/**
 * Auth navigation effects
 */
sealed class AuthNavigationEffect {
    object NavigateToSwipe : AuthNavigationEffect()
    object NavigateToLogin : AuthNavigationEffect()
    data class NavigateToPhoneVerification(val phoneNumber: String) : AuthNavigationEffect()
    object NavigateToCompleteProfile : AuthNavigationEffect()
    data class ShowError(val message: String) : AuthNavigationEffect()
}