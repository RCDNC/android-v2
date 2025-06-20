package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.component.LoadingState
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Cafezinho loading spinner component
 * Based on legacy loading patterns with brand colors
 * Supports different sizes and optional text
 */
@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: ComponentSize = ComponentSize.MEDIUM,
    color: Color = CafezinhoPrimary,
    text: String? = null
) {
    val spinnerSize = when (size) {
        ComponentSize.SMALL -> 24.dp
        ComponentSize.MEDIUM -> 48.dp
        ComponentSize.LARGE -> 64.dp
    }
    
    val strokeWidth = when (size) {
        ComponentSize.SMALL -> 2.dp
        ComponentSize.MEDIUM -> 3.dp
        ComponentSize.LARGE -> 4.dp
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(spinnerSize),
            color = color,
            strokeWidth = strokeWidth
        )
        
        if (text != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = when (size) {
                    ComponentSize.SMALL -> MaterialTheme.typography.bodySmall
                    ComponentSize.MEDIUM -> MaterialTheme.typography.bodyMedium
                    ComponentSize.LARGE -> MaterialTheme.typography.bodyLarge
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Full-screen loading overlay
 * Based on legacy loading screens
 */
@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    text: String = "Carregando...",
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = CafezinhoComponentShapes.Card,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingSpinner(
                        size = ComponentSize.LARGE,
                        text = text
                    )
                }
            }
        }
    }
}

/**
 * Loading state wrapper for content
 * Shows loading, content, or error based on state
 */
@Composable
fun <T> LoadingStateContent(
    loadingState: LoadingState,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = {
        LoadingSpinner(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            text = "Carregando..."
        )
    },
    content: @Composable () -> Unit
) {
    when (loadingState) {
        LoadingState.IDLE -> content()
        LoadingState.LOADING -> loadingContent()
        LoadingState.SUCCESS -> content()
        LoadingState.ERROR -> {
            ErrorState(
                title = "Erro ao carregar",
                message = "Ocorreu um erro. Tente novamente.",
                onRetry = onRetry
            )
        }
        LoadingState.REFRESHING -> {
            Box {
                content()
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = CafezinhoPrimary
                )
            }
        }
    }
}

/**
 * Inline loading indicator for smaller contexts
 */
@Composable
fun InlineLoading(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = CafezinhoPrimary
        )
        
        if (text != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}