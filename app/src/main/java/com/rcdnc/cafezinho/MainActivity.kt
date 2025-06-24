package com.rcdnc.cafezinho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.core.preferences.PreferencesManager
import com.rcdnc.cafezinho.features.auth.presentation.ui.LoginScreen
import com.rcdnc.cafezinho.features.auth.presentation.ui.OnboardingScreen
import com.rcdnc.cafezinho.features.auth.presentation.ui.SplashScreen
import com.rcdnc.cafezinho.features.main.presentation.ui.MainAppScreen
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authManager: AuthManager
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafezinhoTheme {
                CafezinhoApp()
            }
        }
    }
    
    @Composable
    private fun CafezinhoApp() {
        val isAuthenticated by authManager.isAuthenticatedFlow().collectAsStateWithLifecycle(false)
        val hasSeenOnboarding by preferencesManager.hasSeenOnboarding().collectAsStateWithLifecycle(false)
        val scope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        // Estados de navegação
        var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
        
        when (currentScreen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onNavigateToOnboarding = {
                        currentScreen = if (hasSeenOnboarding) {
                            AppScreen.LOGIN
                        } else {
                            AppScreen.ONBOARDING
                        }
                    },
                    onNavigateToLogin = {
                        currentScreen = AppScreen.LOGIN
                    },
                    onNavigateToMain = {
                        currentScreen = AppScreen.MAIN
                    }
                )
            }
            
            AppScreen.ONBOARDING -> {
                OnboardingScreen(
                    onNavigateToLogin = {
                        scope.launch {
                            preferencesManager.setHasSeenOnboarding(true)
                            currentScreen = AppScreen.LOGIN
                        }
                    },
                    onSkip = {
                        scope.launch {
                            preferencesManager.setHasSeenOnboarding(true)
                            currentScreen = AppScreen.LOGIN
                        }
                    }
                )
            }
            
            AppScreen.LOGIN -> {
                LoginScreen(
                    onLoginClick = { email, password, rememberMe ->
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            
                            val success = authManager.demoLogin(email, password)
                            
                            if (success) {
                                currentScreen = AppScreen.MAIN
                            } else {
                                errorMessage = "Email ou senha incorretos"
                            }
                            
                            isLoading = false
                        }
                    },
                    onRegisterClick = {
                        // TODO: Implementar navegação para registro
                    },
                    onForgotPasswordClick = {
                        // TODO: Implementar esqueci minha senha
                    },
                    onGoogleLoginClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            
                            // Simula login com Google para demo
                            val success = authManager.demoLogin("google@demo.com", "google_token")
                            
                            if (success) {
                                currentScreen = AppScreen.MAIN
                            } else {
                                errorMessage = "Erro no login com Google"
                            }
                            
                            isLoading = false
                        }
                    },
                    onFacebookLoginClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            
                            // Simula login com Facebook para demo
                            val success = authManager.demoLogin("facebook@demo.com", "facebook_token")
                            
                            if (success) {
                                currentScreen = AppScreen.MAIN
                            } else {
                                errorMessage = "Erro no login com Facebook"
                            }
                            
                            isLoading = false
                        }
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
            
            AppScreen.MAIN -> {
                MainAppScreen(
                    onLogout = {
                        scope.launch {
                            authManager.clearAuthData()
                            currentScreen = AppScreen.LOGIN
                        }
                    }
                )
            }
        }
        
        // Observa mudanças de autenticação
        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated && currentScreen != AppScreen.MAIN) {
                currentScreen = AppScreen.MAIN
            } else if (!isAuthenticated && currentScreen == AppScreen.MAIN) {
                currentScreen = AppScreen.LOGIN
            }
        }
    }
}

/**
 * Estados das telas principais do app
 */
private enum class AppScreen {
    SPLASH,
    ONBOARDING, 
    LOGIN,
    MAIN
}