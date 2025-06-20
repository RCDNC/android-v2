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
                    CafezinhoNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}