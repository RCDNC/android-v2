package com.rcdnc.cafezinho.features.profile.compose.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rcdnc.cafezinho.features.profile.mvi.ProfileViewModel
import com.rcdnc.cafezinho.features.profile.presentation.screen.ProfileViewScreen
import com.rcdnc.cafezinho.features.profile.presentation.screen.ProfileEditScreen
import com.rcdnc.cafezinho.features.profile.presentation.screen.ProfilePhotosScreen
import com.rcdnc.cafezinho.features.profile.presentation.screen.ProfileSettingsScreen
import com.rcdnc.cafezinho.features.profile.presentation.screen.ProfileVerificationScreen
import com.rcdnc.cafezinho.navigation.NavigationDestinations

/**
 * Profile feature navigation graph
 * Handles profile viewing, editing, photos management, settings, and verification
 */
fun NavGraphBuilder.profileNavigation(
    navController: NavController,
    onNavigateToAuth: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    navigation(
        startDestination = NavigationDestinations.Profile.VIEW,
        route = NavigationDestinations.Profile.ROUTE
    ) {
        // Profile view screen (main profile display)
        composable(route = NavigationDestinations.Profile.VIEW) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfileViewScreen(
                viewModel = viewModel,
                onNavigateToEdit = {
                    navController.navigate(NavigationDestinations.Profile.EDIT)
                },
                onNavigateToPhotos = {
                    navController.navigate(NavigationDestinations.Profile.PHOTOS)
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationDestinations.Profile.SETTINGS)
                },
                onNavigateToVerification = {
                    navController.navigate(NavigationDestinations.Profile.VERIFICATION)
                },
                onLogout = onNavigateToAuth
            )
        }
        
        // Profile edit screen
        composable(route = NavigationDestinations.Profile.EDIT) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfileEditScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                },
                onNavigateToPhotos = {
                    navController.navigate(NavigationDestinations.Profile.PHOTOS)
                }
            )
        }
        
        // Profile photos management screen
        composable(route = NavigationDestinations.Profile.PHOTOS) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfilePhotosScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPhotosUpdated = {
                    // Could navigate back or stay on screen
                    navController.popBackStack()
                },
                onNavigateToImagePicker = {
                    // Handle image picker - could be external activity or compose screen
                }
            )
        }
        
        // Profile settings screen
        composable(route = NavigationDestinations.Profile.SETTINGS) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfileSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAppSettings = onNavigateToSettings,
                onNavigateToPrivacySettings = {
                    // Handle privacy settings navigation
                },
                onNavigateToBlockedUsers = {
                    // Handle blocked users navigation
                },
                onDeleteAccount = {
                    // Handle account deletion flow
                },
                onLogout = onNavigateToAuth
            )
        }
        
        // Profile verification screen
        composable(route = NavigationDestinations.Profile.VERIFICATION) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfileVerificationScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationComplete = {
                    navController.popBackStack()
                },
                onVerificationSubmitted = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Navigation helpers for Profile feature
 */
object ProfileNavigationActions {
    
    fun navigateToProfile(navController: NavController) {
        navController.navigate(NavigationDestinations.Profile.VIEW) {
            // Clear back stack to avoid multiple instances
            popUpTo(NavigationDestinations.Profile.VIEW) {
                inclusive = true
            }
        }
    }
    
    fun navigateToEditProfile(navController: NavController) {
        navController.navigate(NavigationDestinations.Profile.EDIT)
    }
    
    fun navigateToPhotos(navController: NavController) {
        navController.navigate(NavigationDestinations.Profile.PHOTOS)
    }
    
    fun navigateToSettings(navController: NavController) {
        navController.navigate(NavigationDestinations.Profile.SETTINGS)
    }
    
    fun navigateToVerification(navController: NavController) {
        navController.navigate(NavigationDestinations.Profile.VERIFICATION)
    }
    
    fun navigateBackFromProfile(navController: NavController) {
        navController.popBackStack(NavigationDestinations.Profile.VIEW, inclusive = false)
    }
}

/**
 * Integration bridge for existing profile Activities and Fragments
 * Allows gradual migration from Activity/Fragment-based to Compose navigation
 */
object ProfileNavigationBridge {
    
    /**
     * Convert from Activity/Fragment navigation to Compose navigation
     */
    fun fromActivityToCompose(
        activityDestination: String,
        args: Map<String, String> = emptyMap()
    ): String {
        return when (activityDestination) {
            "ProfileFragment", "profile_fragment" -> NavigationDestinations.Profile.VIEW
            "EditProfileActivity" -> NavigationDestinations.Profile.EDIT
            "ProfilePhotosActivity", "AddPhotosFragment" -> NavigationDestinations.Profile.PHOTOS
            "SettingsActivity" -> NavigationDestinations.Profile.SETTINGS
            "VerifyProfile", "ProfileVerificationActivity" -> NavigationDestinations.Profile.VERIFICATION
            else -> NavigationDestinations.Profile.VIEW
        }
    }
    
    /**
     * Convert from Compose navigation to Activity/Fragment navigation
     * Used during transition period
     */
    fun fromComposeToActivity(composeDestination: String): String {
        return when (composeDestination) {
            NavigationDestinations.Profile.VIEW -> "ProfileFragment"
            NavigationDestinations.Profile.EDIT -> "EditProfileActivity"
            NavigationDestinations.Profile.PHOTOS -> "AddPhotosFragment"
            NavigationDestinations.Profile.SETTINGS -> "SettingsActivity"
            NavigationDestinations.Profile.VERIFICATION -> "VerifyProfile"
            else -> "ProfileFragment"
        }
    }
    
    /**
     * Handle profile deep links
     */
    fun handleProfileDeepLink(deepLink: String): String {
        return when {
            deepLink.contains("edit") -> NavigationDestinations.Profile.EDIT
            deepLink.contains("photos") || deepLink.contains("pictures") -> NavigationDestinations.Profile.PHOTOS
            deepLink.contains("settings") -> NavigationDestinations.Profile.SETTINGS
            deepLink.contains("verify") || deepLink.contains("verification") -> NavigationDestinations.Profile.VERIFICATION
            deepLink.contains("profile") -> NavigationDestinations.Profile.VIEW
            else -> NavigationDestinations.Profile.VIEW
        }
    }
}

/**
 * Profile navigation state management
 */
data class ProfileNavigationState(
    val isEditMode: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isVerified: Boolean = false,
    val photoCount: Int = 0,
    val profileCompleteness: Float = 0f
)

/**
 * Profile navigation effects
 */
sealed class ProfileNavigationEffect {
    object NavigateToProfile : ProfileNavigationEffect()
    object NavigateToEdit : ProfileNavigationEffect()
    object NavigateToPhotos : ProfileNavigationEffect()
    object NavigateToSettings : ProfileNavigationEffect()
    object NavigateToVerification : ProfileNavigationEffect()
    object NavigateToAuth : ProfileNavigationEffect()
    object NavigateBack : ProfileNavigationEffect()
    data class ShowSaveConfirmation(val hasChanges: Boolean) : ProfileNavigationEffect()
    data class ShowLogoutConfirmation(val userName: String) : ProfileNavigationEffect()
    data class ShowDeleteAccountConfirmation(val userName: String) : ProfileNavigationEffect()
    data class ShowError(val message: String) : ProfileNavigationEffect()
    data class ShowSuccessMessage(val message: String) : ProfileNavigationEffect()
    object ShowPhotoUploadDialog : ProfileNavigationEffect()
    object ShowVerificationSuccessDialog : ProfileNavigationEffect()
}