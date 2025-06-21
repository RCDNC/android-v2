package com.rcdnc.cafezinho.features.swipe.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.model.Location
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.components.UserImageType
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Match dialog component for celebrating new matches
 * Full screen dialog with animations and actions
 */
@Composable
fun MatchDialog(
    currentUser: User,
    matchedUser: User,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        MatchDialogContent(
            currentUser = currentUser,
            matchedUser = matchedUser,
            onSendMessage = onSendMessage,
            onKeepSwiping = onKeepSwiping,
            onDismiss = onDismiss,
            showAnimation = showAnimation,
            modifier = modifier
        )
    }
}

@Composable
private fun MatchDialogContent(
    currentUser: User,
    matchedUser: User,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit,
    onDismiss: () -> Unit,
    showAnimation: Boolean,
    modifier: Modifier = Modifier
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val enterAnimation by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "enter"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CafezinhoPrimary.copy(alpha = 0.9f),
                        CafezinhoBoostEnd.copy(alpha = 0.8f),
                        CafezinhoBoostStart.copy(alpha = 0.9f)
                    )
                )
            )
            .scale(enterAnimation),
        contentAlignment = Alignment.Center
    ) {
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fechar",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Match title with animation
            Text(
                text = "✨ É UM MATCH! ✨",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(animatedScale)
            )
            
            Text(
                text = "Vocês curtiram um ao outro!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User images
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current user image
                MatchUserImage(
                    user = currentUser,
                    label = "Você",
                    modifier = Modifier.weight(1f)
                )
                
                // Heart icon between images
                Text(
                    text = "💖",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .scale(animatedScale)
                        .padding(horizontal = 16.dp)
                )
                
                // Matched user image
                MatchUserImage(
                    user = matchedUser,
                    label = matchedUser.name,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Send message button
                Button(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = CafezinhoPrimary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enviar Mensagem",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Keep swiping button
                OutlinedButton(
                    onClick = onKeepSwiping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Color.White, Color.White))
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Continuar Explorando",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchUserImage(
    user: User,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserImage(
            imageUrl = user.photos.firstOrNull(),
            contentDescription = "$label profile image",
            type = UserImageType.AVATAR,
            size = ComponentSize.LARGE,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Super Like match dialog variant
 */
@Composable
fun SuperLikeMatchDialog(
    currentUser: User,
    matchedUser: User,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CafezinhoSuperLike.copy(alpha = 0.9f),
                            CafezinhoBoostStart.copy(alpha = 0.8f),
                            CafezinhoPrimary.copy(alpha = 0.9f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "⭐ SUPER MATCH! ⭐",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "${matchedUser.name} te deu um Super Like!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                
                // User image
                UserImage(
                    imageUrl = matchedUser.photos.firstOrNull(),
                    contentDescription = "${matchedUser.name} profile image",
                    type = UserImageType.AVATAR,
                    size = ComponentSize.LARGE,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
                
                Text(
                    text = matchedUser.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSendMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = CafezinhoSuperLike
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Dizer Olá! 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    OutlinedButton(
                        onClick = onKeepSwiping,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color.White, Color.White))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Continuar Explorando",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Preview functions
@Preview(name = "MatchDialog")
@Composable
private fun MatchDialogPreview() {
    CafezinhoTheme {
        val currentUser = User(
            id = "current",
            name = "Você",
            age = 25,
            photos = listOf("https://example.com/current.jpg")
        )
        
        val matchedUser = User(
            id = "matched",
            name = "Maria Silva",
            age = 28,
            photos = listOf("https://example.com/matched.jpg"),
            location = Location(
                latitude = -23.5505,
                longitude = -46.6333,
                city = "São Paulo",
                country = "Brasil"
            ),
            isVerified = true
        )
        
        MatchDialog(
            currentUser = currentUser,
            matchedUser = matchedUser,
            onSendMessage = { },
            onKeepSwiping = { },
            onDismiss = { }
        )
    }
}

@Preview(name = "SuperLikeMatchDialog")
@Composable
private fun SuperLikeMatchDialogPreview() {
    CafezinhoTheme {
        val currentUser = User(
            id = "current",
            name = "Você",
            age = 25,
            photos = listOf("https://example.com/current.jpg")
        )
        
        val matchedUser = User(
            id = "matched",
            name = "João Santos",
            age = 30,
            photos = listOf("https://example.com/matched.jpg"),
            isVerified = true
        )
        
        SuperLikeMatchDialog(
            currentUser = currentUser,
            matchedUser = matchedUser,
            onSendMessage = { },
            onKeepSwiping = { },
            onDismiss = { }
        )
    }
}