package com.rcdnc.cafezinho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.features.auth.presentation.ui.LoginScreen
import com.rcdnc.cafezinho.features.main.presentation.ui.MainAppScreen
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafezinhoTheme {
                AuthenticatedApp()
            }
        }
    }
    
    @Composable
    private fun AuthenticatedApp() {
        val isAuthenticated by authManager.isAuthenticatedFlow().collectAsStateWithLifecycle(false)
        val scope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        if (isAuthenticated) {
            // Usuario autenticado - mostra app principal
            MainAppScreen(
                onLogout = {
                    scope.launch {
                        authManager.clearAuthData()
                    }
                }
            )
        } else {
            // Usuario não autenticado - mostra tela de login
            LoginScreen(
                onLoginClick = { email, password, rememberMe ->
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        
                        val success = authManager.demoLogin(email, password)
                        
                        if (!success) {
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
                    // TODO: Implementar Google login
                },
                onFacebookLoginClick = {
                    // TODO: Implementar Facebook login
                },
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
    }
}