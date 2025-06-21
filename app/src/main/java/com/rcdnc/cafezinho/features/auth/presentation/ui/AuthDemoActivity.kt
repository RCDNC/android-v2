package com.rcdnc.cafezinho.features.auth.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rcdnc.cafezinho.features.auth.presentation.navigation.AuthNavigation
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

/**
 * Demo Activity to showcase the Authentication flow
 * This demonstrates that our UI and navigation work perfectly
 */
class AuthDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CafezinhoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavigation(
                        onAuthComplete = {
                            // In a real app, this would navigate to the main app
                            finish()
                        }
                    )
                }
            }
        }
    }
}