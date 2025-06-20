package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.component.ErrorType
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Cafezinho error state component
 * Based on legacy error screens with consistent messaging
 * Supports different error types and retry actions
 */
@Composable
fun ErrorState(
    title: String,
    message: String? = null,
    errorType: ErrorType = ErrorType.GENERAL,
    onRetry: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor) = getErrorIcon(errorType)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Error Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Error Message
        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Primary Action (Retry)
            if (onRetry != null) {
                CafezinhoButton(
                    text = getRetryText(errorType),
                    onClick = onRetry,
                    variant = com.rcdnc.cafezinho.ui.component.ButtonVariant.PRIMARY,
                    size = ComponentSize.MEDIUM
                )
            }
            
            // Secondary Action
            if (onSecondaryAction != null && secondaryActionText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                CafezinhoButton(
                    text = secondaryActionText,
                    onClick = onSecondaryAction,
                    variant = com.rcdnc.cafezinho.ui.component.ButtonVariant.SECONDARY,
                    size = ComponentSize.MEDIUM
                )
            }
        }
    }
}

/**
 * Compact error message for inline display
 */
@Composable
fun InlineError(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CafezinhoError.copy(alpha = 0.1f)
        ),
        shape = CafezinhoComponentShapes.Card
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = CafezinhoError,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = CafezinhoError,
                modifier = Modifier.weight(1f)
            )
            
            if (onRetry != null) {
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = CafezinhoError
                    )
                ) {
                    Text(
                        text = "Tentar novamente",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

/**
 * Network error with specific messaging
 */
@Composable
fun NetworkError(
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Sem conexão",
        message = "Verifique sua conexão com a internet e tente novamente.",
        errorType = ErrorType.NETWORK,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Empty state component (no data to show)
 */
@Composable
fun EmptyState(
    title: String,
    message: String? = null,
    icon: ImageVector = Icons.Default.Search,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        if (onAction != null && actionText != null) {
            Spacer(modifier = Modifier.height(32.dp))
            CafezinhoButton(
                text = actionText,
                onClick = onAction,
                variant = com.rcdnc.cafezinho.ui.component.ButtonVariant.PRIMARY,
                size = ComponentSize.MEDIUM
            )
        }
    }
}

// Helper functions
@Composable
private fun getErrorIcon(errorType: ErrorType): Pair<ImageVector, androidx.compose.ui.graphics.Color> {
    return when (errorType) {
        ErrorType.NETWORK -> Icons.Default.Warning to CafezinhoError
        ErrorType.SERVER -> Icons.Default.Warning to CafezinhoError
        ErrorType.AUTH -> Icons.Default.Lock to CafezinhoError
        ErrorType.PERMISSION -> Icons.Default.Warning to CafezinhoWarning
        ErrorType.NOT_FOUND -> Icons.Default.Search to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ErrorType.GENERAL -> Icons.Default.Warning to CafezinhoError
    }
}

private fun getRetryText(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.NETWORK -> "Tentar novamente"
        ErrorType.SERVER -> "Recarregar"
        ErrorType.AUTH -> "Fazer login"
        ErrorType.PERMISSION -> "Permitir acesso"
        ErrorType.NOT_FOUND -> "Voltar"
        ErrorType.GENERAL -> "Tentar novamente"
    }
}