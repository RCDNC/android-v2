package com.rcdnc.cafezinho.features.swipe.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.model.Location
import com.rcdnc.cafezinho.ui.components.UserCard
import com.rcdnc.cafezinho.ui.components.SwipeOverlay
import com.rcdnc.cafezinho.ui.components.SwipeAction
import com.rcdnc.cafezinho.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * Swipe direction enum
 */
enum class SwipeDirection {
    LEFT, RIGHT, UP, NONE
}

/**
 * Swipe result data class
 */
data class SwipeResult(
    val direction: SwipeDirection,
    val user: User
)

/**
 * SwipeCard component with gesture detection and animations
 * Based on UserCard component with swipe functionality
 */
@Composable
fun SwipeCard(
    user: User,
    onSwipe: (SwipeResult) -> Unit,
    modifier: Modifier = Modifier,
    interests: List<String> = emptyList(),
    distance: String? = null,
    isOnline: Boolean = false,
    swipeNodeMessage: String? = null,
    enableSwipeGestures: Boolean = true,
    swipeThreshold: Float = 300f
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    
    val scope = rememberCoroutineScope()
    
    // Animation states
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "offsetX"
    )
    
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "offsetY"
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rotation"
    )
    
    // Calculate swipe direction and opacity
    val swipeDirection = when {
        abs(offsetX) > abs(offsetY) && abs(offsetX) > swipeThreshold / 2 -> {
            if (offsetX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
        }
        offsetY < -swipeThreshold / 2 -> SwipeDirection.UP
        else -> SwipeDirection.NONE
    }
    
    val overlayOpacity = when (swipeDirection) {
        SwipeDirection.LEFT, SwipeDirection.RIGHT, SwipeDirection.UP -> {
            (abs(if (swipeDirection == SwipeDirection.UP) offsetY else offsetX) / swipeThreshold).coerceIn(0f, 1f)
        }
        SwipeDirection.NONE -> 0f
    }
    
    // Reset position
    fun resetPosition() {
        scope.launch {
            offsetX = 0f
            offsetY = 0f
            rotation = 0f
        }
    }
    
    // Trigger swipe
    fun triggerSwipe(direction: SwipeDirection) {
        scope.launch {
            when (direction) {
                SwipeDirection.LEFT -> {
                    offsetX = -screenWidth.value * 1.5f
                    rotation = -30f
                }
                SwipeDirection.RIGHT -> {
                    offsetX = screenWidth.value * 1.5f
                    rotation = 30f
                }
                SwipeDirection.UP -> {
                    offsetY = -screenHeight.value * 1.5f
                    rotation = (offsetX / 50f).coerceIn(-15f, 15f)
                }
                SwipeDirection.NONE -> resetPosition()
            }
            
            if (direction != SwipeDirection.NONE) {
                onSwipe(SwipeResult(direction, user))
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
    ) {
        UserCard(
            user = user,
            interests = interests,
            distance = distance,
            isOnline = isOnline,
            swipeNodeMessage = swipeNodeMessage,
            onImageClick = { direction ->
                // Navigate through images
            },
            onLike = { triggerSwipe(SwipeDirection.RIGHT) },
            onPass = { triggerSwipe(SwipeDirection.LEFT) },
            onSuperLike = { triggerSwipe(SwipeDirection.UP) },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffsetX
                    translationY = animatedOffsetY
                    rotationZ = animatedRotation
                }
                .then(
                    if (enableSwipeGestures) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    // Check if should trigger swipe or reset
                                    when {
                                        abs(offsetX) > swipeThreshold -> {
                                            triggerSwipe(if (offsetX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT)
                                        }
                                        offsetY < -swipeThreshold -> {
                                            triggerSwipe(SwipeDirection.UP)
                                        }
                                        else -> resetPosition()
                                    }
                                }
                            ) { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                rotation = (offsetX / 20f).coerceIn(-30f, 30f)
                            }
                        }
                    } else {
                        Modifier
                    }
                )
        )
        
        // Swipe overlay
        if (overlayOpacity > 0f) {
            val overlayAction = when (swipeDirection) {
                SwipeDirection.LEFT -> SwipeAction.DISLIKE
                SwipeDirection.RIGHT -> SwipeAction.LIKE
                SwipeDirection.UP -> SwipeAction.SUPERLIKE
                SwipeDirection.NONE -> null
            }
            
            overlayAction?.let { action ->
                SwipeOverlay(
                    action = action,
                    opacity = overlayOpacity,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Stack of swipe cards for continuous swiping
 */
@Composable
fun SwipeCardStack(
    users: List<User>,
    onSwipe: (SwipeResult) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleCards: Int = 3,
    cardSpacing: Float = 8f,
    scaleRatio: Float = 0.05f,
    userInterests: Map<String, List<String>> = emptyMap(),
    userDistances: Map<String, String> = emptyMap(),
    onlineUsers: Set<String> = emptySet()
) {
    if (users.isEmpty()) {
        // Empty state
        EmptySwipeState(modifier = modifier)
        return
    }
    
    Box(modifier = modifier) {
        // Show up to maxVisibleCards cards
        users.take(maxVisibleCards).forEachIndexed { index, user ->
            val scale = 1f - (index * scaleRatio)
            val translationY = index * cardSpacing
            
            SwipeCard(
                user = user,
                onSwipe = onSwipe,
                interests = userInterests[user.id] ?: emptyList(),
                distance = userDistances[user.id],
                isOnline = user.id in onlineUsers,
                enableSwipeGestures = index == 0, // Only top card is swipeable
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = this@graphicsLayer.translationY + translationY
                    }
            )
        }
    }
}

@Composable
private fun EmptySwipeState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎉",
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge
            )
            
            Text(
                text = "Não há mais pessoas por aqui",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Volte mais tarde para ver novos perfis",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Preview functions
@Preview(name = "SwipeCard")
@Composable
private fun SwipeCardPreview() {
    CafezinhoTheme {
        val sampleUser = User(
            id = "1",
            name = "Maria Silva",
            age = 25,
            bio = "Amo viajar e conhecer pessoas novas!",
            photos = listOf(
                "https://example.com/photo1.jpg",
                "https://example.com/photo2.jpg"
            ),
            location = Location(
                latitude = -23.5505,
                longitude = -46.6333,
                city = "São Paulo",
                country = "Brasil"
            ),
            isVerified = true
        )
        
        SwipeCard(
            user = sampleUser,
            onSwipe = { },
            interests = listOf("Viagem", "Música", "Culinária"),
            distance = "2km",
            isOnline = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "SwipeCardStack")
@Composable
private fun SwipeCardStackPreview() {
    CafezinhoTheme {
        val sampleUsers = listOf(
            User(
                id = "1",
                name = "Maria",
                age = 25,
                photos = listOf("https://example.com/photo1.jpg"),
                isVerified = true
            ),
            User(
                id = "2",
                name = "João",
                age = 28,
                photos = listOf("https://example.com/photo2.jpg")
            ),
            User(
                id = "3",
                name = "Ana",
                age = 23,
                photos = listOf("https://example.com/photo3.jpg"),
                isVerified = true
            )
        )
        
        SwipeCardStack(
            users = sampleUsers,
            onSwipe = { },
            userInterests = mapOf(
                "1" to listOf("Viagem", "Música"),
                "2" to listOf("Esportes", "Cinema"),
                "3" to listOf("Arte", "Culinária")
            ),
            userDistances = mapOf(
                "1" to "2km",
                "2" to "5km",
                "3" to "1km"
            ),
            onlineUsers = setOf("1", "3"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "EmptySwipeState")
@Composable
private fun EmptySwipeStatePreview() {
    CafezinhoTheme {
        EmptySwipeState(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}