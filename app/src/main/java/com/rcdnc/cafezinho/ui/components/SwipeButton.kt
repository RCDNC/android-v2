package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Swipe action types based on legacy SwipeDirection
 */
enum class SwipeAction {
    REWIND,      // Voltar (refresh_btn)
    DISLIKE,     // Rejeitar (cross_btn) - LEFT swipe
    SUPERLIKE,   // Super Like (supperlike_btn) - UP swipe
    LIKE,        // Curtir (heart_btn) - RIGHT swipe
    BOOST        // Boost (boost_btn)
}

/**
 * Individual swipe button component
 * Based on legacy component_buttons_match.xml button structure
 */
@Composable
fun SwipeButton(
    action: SwipeAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isActive: Boolean = false,
    progress: Float? = null // Para boost progress
) {
    val (icon, colors, size) = getSwipeButtonProperties(action, isActive)
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Background circle (sempre branco como no legacy)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (action == SwipeAction.BOOST && isActive) {
                        Color.Transparent
                    } else {
                        CafezinhoBackground
                    },
                    shape = CircleShape
                )
                .then(
                    if (action == SwipeAction.BOOST && isActive) {
                        Modifier.background(
                            brush = Brush.radialGradient(
                                colors = listOf(CafezinhoBoostStart, CafezinhoBoostEnd)
                            ),
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
        )
        
        // IconButton content
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = getContentDescription(action),
                tint = colors.iconColor,
                modifier = Modifier.size(getIconSize(action))
            )
        }
        
        // Boost progress indicator (sobreposto ao botão)
        if (action == SwipeAction.BOOST && progress != null) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = CafezinhoBoostEnd,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * Row of swipe buttons - main component
 * Based on legacy component_buttons_match.xml structure
 */
@Composable
fun SwipeButtonRow(
    onRewind: () -> Unit,
    onDislike: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit,
    onBoost: () -> Unit,
    modifier: Modifier = Modifier,
    hasRewinds: Boolean = true,
    hasSuperLikes: Boolean = true,
    hasBoosts: Boolean = true,
    isBoostActive: Boolean = false,
    boostProgress: Float? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rewind Button (26x36dp no legacy)
        SwipeButton(
            action = SwipeAction.REWIND,
            onClick = onRewind,
            enabled = hasRewinds,
            modifier = Modifier.weight(1f)
        )
        
        // Dislike Button (34x45dp no legacy)
        SwipeButton(
            action = SwipeAction.DISLIKE,
            onClick = onDislike,
            modifier = Modifier.weight(1f)
        )
        
        // SuperLike Button (34x45dp no legacy)
        SwipeButton(
            action = SwipeAction.SUPERLIKE,
            onClick = onSuperLike,
            enabled = hasSuperLikes,
            modifier = Modifier.weight(1f)
        )
        
        // Like Button (34x45dp no legacy)
        SwipeButton(
            action = SwipeAction.LIKE,
            onClick = onLike,
            modifier = Modifier.weight(1f)
        )
        
        // Boost Button (26x36dp no legacy)
        SwipeButton(
            action = SwipeAction.BOOST,
            onClick = onBoost,
            enabled = hasBoosts,
            isActive = isBoostActive,
            progress = boostProgress,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Swipe overlay for card feedback
 * Based on legacy overlays: ic_nopeoverlay_outline, ic_superlikeoverlay_outline, ic_like_outline
 */
@Composable
fun SwipeOverlay(
    action: SwipeAction,
    opacity: Float,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (action) {
        SwipeAction.DISLIKE -> "NOPE!" to CafezinhoDislike
        SwipeAction.LIKE -> "LIKE!" to CafezinhoLike
        SwipeAction.SUPERLIKE -> "SUPER\nLIKE!" to CafezinhoSuperLike
        else -> return // Não há overlay para rewind e boost
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .border(
                    width = 4.dp,
                    color = color.copy(alpha = opacity),
                    shape = CafezinhoComponentShapes.Card
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = CafezinhoComponentShapes.Card
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = color.copy(alpha = opacity),
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

// Helper functions
private data class SwipeButtonColors(
    val iconColor: Color,
    val backgroundColor: Color = Color.Transparent
)

@Composable
private fun getSwipeButtonProperties(
    action: SwipeAction,
    isActive: Boolean
): Triple<ImageVector, SwipeButtonColors, androidx.compose.ui.unit.Dp> {
    return when (action) {
        SwipeAction.REWIND -> Triple(
            Icons.Default.Refresh,
            SwipeButtonColors(iconColor = CafezinhoOnSurfaceVariant),
            44.dp // Menor que os principais (26dp no legacy)
        )
        
        SwipeAction.DISLIKE -> Triple(
            Icons.Default.Close,
            SwipeButtonColors(iconColor = CafezinhoDislike),
            52.dp // Tamanho principal (34dp no legacy)
        )
        
        SwipeAction.SUPERLIKE -> Triple(
            Icons.Default.Star,
            SwipeButtonColors(iconColor = CafezinhoSuperLike),
            52.dp // Tamanho principal (34dp no legacy)
        )
        
        SwipeAction.LIKE -> Triple(
            Icons.Default.Favorite,
            SwipeButtonColors(iconColor = CafezinhoLike),
            52.dp // Tamanho principal (34dp no legacy)
        )
        
        SwipeAction.BOOST -> Triple(
            if (isActive) Icons.Default.Star else Icons.Default.Add,
            SwipeButtonColors(
                iconColor = if (isActive) CafezinhoOnPrimary else CafezinhoMatch
            ),
            44.dp // Menor que os principais (26dp no legacy)
        )
    }
}

@Composable
private fun getIconSize(action: SwipeAction): androidx.compose.ui.unit.Dp {
    return when (action) {
        SwipeAction.REWIND, SwipeAction.BOOST -> 20.dp
        SwipeAction.DISLIKE, SwipeAction.SUPERLIKE, SwipeAction.LIKE -> 24.dp
    }
}

private fun getContentDescription(action: SwipeAction): String {
    return when (action) {
        SwipeAction.REWIND -> "Voltar"
        SwipeAction.DISLIKE -> "Não curtir"
        SwipeAction.SUPERLIKE -> "Super like"
        SwipeAction.LIKE -> "Curtir"
        SwipeAction.BOOST -> "Boost"
    }
}

// Preview functions
@Preview(name = "SwipeButton - All Actions")
@Composable
private fun SwipeButtonPreview() {
    CafezinhoTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SwipeButton(
                action = SwipeAction.REWIND,
                onClick = { }
            )
            SwipeButton(
                action = SwipeAction.DISLIKE,
                onClick = { }
            )
            SwipeButton(
                action = SwipeAction.SUPERLIKE,
                onClick = { }
            )
            SwipeButton(
                action = SwipeAction.LIKE,
                onClick = { }
            )
            SwipeButton(
                action = SwipeAction.BOOST,
                onClick = { }
            )
        }
    }
}

@Preview(name = "SwipeButton - States")
@Composable
private fun SwipeButtonStatesPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Normal")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SwipeButton(action = SwipeAction.LIKE, onClick = { })
                SwipeButton(action = SwipeAction.BOOST, onClick = { })
            }
            
            Text("Disabled")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SwipeButton(action = SwipeAction.LIKE, onClick = { }, enabled = false)
                SwipeButton(action = SwipeAction.BOOST, onClick = { }, enabled = false)
            }
            
            Text("Boost Active")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SwipeButton(action = SwipeAction.BOOST, onClick = { }, isActive = true)
                SwipeButton(action = SwipeAction.BOOST, onClick = { }, isActive = true, progress = 0.7f)
            }
        }
    }
}

@Preview(name = "SwipeButtonRow")
@Composable
private fun SwipeButtonRowPreview() {
    CafezinhoTheme {
        SwipeButtonRow(
            onRewind = { },
            onDislike = { },
            onSuperLike = { },
            onLike = { },
            onBoost = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "SwipeOverlay")
@Composable
private fun SwipeOverlayPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(200.dp, 300.dp)) {
                SwipeOverlay(action = SwipeAction.LIKE, opacity = 0.8f)
            }
            Box(modifier = Modifier.size(200.dp, 300.dp)) {
                SwipeOverlay(action = SwipeAction.DISLIKE, opacity = 0.8f)
            }
            Box(modifier = Modifier.size(200.dp, 300.dp)) {
                SwipeOverlay(action = SwipeAction.SUPERLIKE, opacity = 0.8f)
            }
        }
    }
}