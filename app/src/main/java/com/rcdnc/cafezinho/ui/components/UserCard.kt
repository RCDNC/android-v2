package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.model.Location
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Complete user card component for swipe interface
 * Based on legacy item_user_layout.xml (385 lines)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: User,
    modifier: Modifier = Modifier,
    onImageClick: ((direction: Int) -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    onPass: (() -> Unit)? = null,
    onSuperLike: (() -> Unit)? = null,
    interests: List<String> = emptyList(),
    distance: String? = null,
    isOnline: Boolean = false,
    swipeNodeMessage: String? = null,
    showSwipeButtons: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main image pager
            UserImagePager(
                images = user.photos,
                onImageClick = onImageClick,
                showNavigationArrows = true,
                showPageIndicators = true,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // SwipeNode message (if present)
            if (!swipeNodeMessage.isNullOrEmpty()) {
                SwipeNodeMessage(
                    message = swipeNodeMessage,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                )
            }
            
            // User info overlay
            UserInfoOverlay(
                name = user.name,
                age = user.age,
                isVerified = user.isVerified,
                isOnline = isOnline,
                location = user.location?.city,
                distance = distance,
                interests = interests,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            
            // Swipe buttons (if enabled)
            if (showSwipeButtons) {
                SwipeButtonRow(
                    onRewind = { /* Handle rewind */ },
                    onDislike = { onPass?.invoke() },
                    onSuperLike = { onSuperLike?.invoke() },
                    onLike = { onLike?.invoke() },
                    onBoost = { /* Handle boost */ },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun SwipeNodeMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CafezinhoPrimary,
        shadowElevation = 4.dp
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = CafezinhoOnPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Simplified user card for matches/chat list
 */
@Composable
fun CompactUserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    isOnline: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User avatar
            UserImage(
                imageUrl = user.photos.firstOrNull(),
                contentDescription = "${user.name} avatar",
                type = UserImageType.AVATAR,
                isOnline = isOnline,
                modifier = Modifier.size(56.dp)
            )
            
            // User info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CompactUserInfo(
                    name = user.name,
                    age = user.age,
                    isVerified = user.isVerified,
                    isOnline = isOnline
                )
                
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Trailing content
            trailing?.invoke()
        }
    }
}

/**
 * User card for grid layouts (discovery, etc.)
 */
@Composable
fun GridUserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.75f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main image
            UserImage(
                imageUrl = user.photos.firstOrNull(),
                contentDescription = "${user.name} photo",
                type = UserImageType.CARD,
                modifier = Modifier.fillMaxSize()
            )
            
            // Info overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                CompactUserInfo(
                    name = user.name,
                    age = user.age,
                    isVerified = user.isVerified,
                    isOnline = isOnline,
                    textColor = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

// Preview functions
@Preview(name = "UserCard - Complete")
@Composable
private fun UserCardCompletePreview() {
    CafezinhoTheme {
        val sampleUser = User(
            id = "1",
            name = "Maria Silva",
            age = 25,
            bio = "Amo viajar e conhecer pessoas novas!",
            photos = listOf(
                "https://example.com/photo1.jpg",
                "https://example.com/photo2.jpg",
                "https://example.com/photo3.jpg"
            ),
            location = Location(
                latitude = -23.5505,
                longitude = -46.6333,
                city = "São Paulo",
                country = "Brasil"
            ),
            isVerified = true
        )
        
        UserCard(
            user = sampleUser,
            interests = listOf("Viagem", "Música", "Culinária"),
            distance = "2km",
            isOnline = true,
            swipeNodeMessage = "Você tem um Super Like!",
            showSwipeButtons = true,
            onLike = { },
            onPass = { },
            onSuperLike = { },
            onImageClick = { }
        )
    }
}

@Preview(name = "UserCard - Minimal")
@Composable
private fun UserCardMinimalPreview() {
    CafezinhoTheme {
        val sampleUser = User(
            id = "2",
            name = "João",
            age = 28,
            photos = listOf("https://example.com/photo1.jpg")
        )
        
        UserCard(
            user = sampleUser,
            onImageClick = { }
        )
    }
}

@Preview(name = "CompactUserCard")
@Composable
private fun CompactUserCardPreview() {
    CafezinhoTheme {
        val sampleUser = User(
            id = "3",
            name = "Ana Costa",
            age = 30,
            photos = listOf("https://example.com/photo1.jpg"),
            isVerified = true
        )
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactUserCard(
                user = sampleUser,
                onClick = { },
                subtitle = "Você tem um match!",
                isOnline = true,
                trailing = {
                    Text(
                        text = "agora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
            
            CompactUserCard(
                user = sampleUser.copy(name = "Pedro Santos", age = 26),
                onClick = { },
                subtitle = "Última mensagem há 2 horas"
            )
        }
    }
}

@Preview(name = "GridUserCard")
@Composable
private fun GridUserCardPreview() {
    CafezinhoTheme {
        val sampleUser = User(
            id = "4",
            name = "Carolina",
            age = 24,
            photos = listOf("https://example.com/photo1.jpg"),
            isVerified = true
        )
        
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GridUserCard(
                user = sampleUser,
                onClick = { },
                isOnline = true,
                modifier = Modifier.width(150.dp)
            )
            
            GridUserCard(
                user = sampleUser.copy(name = "Rafael", age = 29),
                onClick = { },
                modifier = Modifier.width(150.dp)
            )
        }
    }
}