package com.rcdnc.cafezinho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.rcdnc.cafezinho.navigation.CafezinhoNavHost
import com.rcdnc.cafezinho.features.auth.presentation.navigation.AuthNavigation
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafezinhoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Show Auth Flow for demo
                    AuthNavigation(
                        onAuthComplete = {
                            // In a real app, this would navigate to main content
                            // For now, just stays in auth flow
                        }
                    )
                }
            }
        }
    }
}