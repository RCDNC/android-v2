package com.rcdnc.cafezinho.features.swipe.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
// import androidx.compose.material.icons.filled.Undo
// import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.features.swipe.domain.model.*
import com.rcdnc.cafezinho.features.swipe.presentation.viewmodel.SwipeIntent
import com.rcdnc.cafezinho.features.swipe.presentation.viewmodel.SwipeState
import com.rcdnc.cafezinho.features.swipe.presentation.viewmodel.SwipeViewModel
// import com.rcdnc.cafezinho.ui.components.ComponentSize // Removed - using direct dp values
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*
import kotlin.math.abs
import kotlin.math.sign

/**
 * Tela principal de Swipe/Descobrir
 * Implementação completa com gestos, animações e integração com API Laravel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    onUserClick: (SwipeUser) -> Unit = {},
    onMatchFound: (SwipeUser) -> Unit = {},
    onFiltersClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SwipeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val swipeStack by viewModel.swipeStack.collectAsStateWithLifecycle()
    val userMetrics by viewModel.userMetrics.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    var showMatchDialog by remember { mutableStateOf(false) }
    var matchedUser by remember { mutableStateOf<SwipeUser?>(null) }
    var showFiltersDialog by remember { mutableStateOf(false) }
    var showUserDetailsDialog by remember { mutableStateOf<SwipeUser?>(null) }
    
    // Handle state changes
    LaunchedEffect(state) {
        when (val currentState = state) {
            is SwipeState.MatchFound -> {
                matchedUser = currentState.result.user
                showMatchDialog = true
                onMatchFound(currentState.result.user)
            }
            is SwipeState.ShowingUserDetails -> {
                showUserDetailsDialog = currentState.user
                onUserClick(currentState.user)
            }
            else -> {}
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CafezinhoPrimary.copy(alpha = 0.1f),
                        CafezinhoBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with metrics and filters
            SwipeTopBar(
                userMetrics = userMetrics,
                onFiltersClick = { showFiltersDialog = true },
                onProfileClick = onNavigateToProfile
            )
            
            // Main swipe area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading && swipeStack.isEmpty() -> {
                        LoadingCard()
                    }
                    
                    swipeStack.isEmpty() -> {
                        NoMoreUsersCard(
                            onRetryClick = { 
                                viewModel.handleIntent(SwipeIntent.LoadUsers(refresh = true))
                            }
                        )
                    }
                    
                    else -> {
                        SwipeCardStack(
                            users = swipeStack.take(3), // Mostra apenas 3 cards por vez
                            onSwipe = { user, action ->
                                viewModel.handleIntent(SwipeIntent.SwipeUser(user, action))
                            },
                            onUserDetailsClick = { user ->
                                viewModel.handleIntent(SwipeIntent.ShowUserDetails(user))
                            }
                        )
                    }
                }
            }
            
            // Bottom action buttons
            SwipeActionButtons(
                canLike = viewModel.canLike(),
                canSuperLike = viewModel.canSuperLike(),
                canRewind = viewModel.canRewind(),
                likesRemaining = viewModel.getLikesRemaining(),
                superLikesRemaining = viewModel.getSuperLikesRemaining(),
                onDislikeClick = {
                    swipeStack.firstOrNull()?.let { user ->
                        viewModel.handleIntent(SwipeIntent.SwipeUser(user, SwipeAction.DISLIKE))
                    }
                },
                onLikeClick = {
                    swipeStack.firstOrNull()?.let { user ->
                        viewModel.handleIntent(SwipeIntent.SwipeUser(user, SwipeAction.LIKE))
                    }
                },
                onSuperLikeClick = {
                    swipeStack.firstOrNull()?.let { user ->
                        viewModel.handleIntent(SwipeIntent.SwipeUser(user, SwipeAction.SUPER_LIKE))
                    }
                },
                onRewindClick = {
                    viewModel.handleIntent(SwipeIntent.RewindLastSwipe)
                }
            )
        }
        
        // Error handling
        when (val currentState = state) {
            is SwipeState.Error -> {
                ErrorSnackbar(
                    message = currentState.message,
                    onDismiss = { viewModel.handleIntent(SwipeIntent.ClearError) }
                )
            }
            else -> {}
        }
    }
    
    // Match Dialog
    if (showMatchDialog && matchedUser != null) {
        MatchDialog(
            user = matchedUser!!,
            onSendMessage = { user ->
                showMatchDialog = false
                onNavigateToChat(user.id)
            },
            onKeepSwiping = {
                showMatchDialog = false
                matchedUser = null
            },
            onDismiss = {
                showMatchDialog = false
                matchedUser = null
            }
        )
    }
    
    // User Details Dialog
    showUserDetailsDialog?.let { user ->
        UserDetailsDialog(
            user = user,
            onDismiss = { showUserDetailsDialog = null },
            onLike = {
                viewModel.handleIntent(SwipeIntent.SwipeUser(user, SwipeAction.LIKE))
                showUserDetailsDialog = null
            },
            onDislike = {
                viewModel.handleIntent(SwipeIntent.SwipeUser(user, SwipeAction.DISLIKE))
                showUserDetailsDialog = null
            },
            onReport = { reason ->
                viewModel.handleIntent(SwipeIntent.ReportUser(user, reason))
                showUserDetailsDialog = null
            }
        )
    }
    
    // Filters Dialog
    if (showFiltersDialog) {
        FiltersDialog(
            currentFilters = viewModel.filters.collectAsStateWithLifecycle().value,
            onFiltersUpdate = { filters ->
                viewModel.handleIntent(SwipeIntent.UpdateFilters(filters))
                showFiltersDialog = false
            },
            onDismiss = { showFiltersDialog = false }
        )
    }
}

/**
 * Barra superior com métricas e filtros
 */
@Composable
private fun SwipeTopBar(
    userMetrics: SwipeMetrics?,
    onFiltersClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metrics
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricChip(
                icon = Icons.Default.Favorite,
                count = userMetrics?.let { it.dailyLikesLimit - it.dailyLikesUsed } ?: 0,
                color = CafezinhoLike
            )
            
            MetricChip(
                icon = Icons.Default.Star,
                count = userMetrics?.let { it.superLikesLimit - it.superLikesUsed } ?: 0,
                color = CafezinhoSuperLike
            )
            
            if (userMetrics?.isPremium == true) {
                MetricChip(
                    icon = Icons.Default.Refresh, // Using Refresh as substitute for Undo
                    count = userMetrics.let { it.rewindsLimit - it.rewindsUsed },
                    color = CafezinhoPrimary
                )
            }
        }
        
        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFiltersClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Filtros",
                    tint = CafezinhoPrimary
                )
            }
            
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = CafezinhoPrimary
                )
            }
        }
    }
}

/**
 * Chip de métrica
 */
@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = count.toString(),
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Stack de cards para swipe
 */
@Composable
private fun SwipeCardStack(
    users: List<SwipeUser>,
    onSwipe: (SwipeUser, SwipeAction) -> Unit,
    onUserDetailsClick: (SwipeUser) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    Box(
        modifier = modifier.size(
            width = (configuration.screenWidthDp * 0.9).dp,
            height = (configuration.screenHeightDp * 0.6).dp
        ),
        contentAlignment = Alignment.Center
    ) {
        users.forEachIndexed { index, user ->
            val scale = 1f - (index * 0.05f)
            val offsetY = (index * 8).dp
            
            SwipeCard(
                user = user,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = with(density) { offsetY.toPx() }
                    },
                isTopCard = index == 0,
                onSwipe = onSwipe,
                onDetailsClick = onUserDetailsClick
            )
        }
    }
}

/**
 * Card individual de usuário com gestos de swipe
 */
@Composable
private fun SwipeCard(
    user: SwipeUser,
    isTopCard: Boolean,
    onSwipe: (SwipeUser, SwipeAction) -> Unit,
    onDetailsClick: (SwipeUser) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    Card(
        modifier = modifier
            .graphicsLayer {
                translationX = animatedOffsetX
                translationY = animatedOffsetY
                rotationZ = animatedRotation
            }
            .then(
                if (isTopCard) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                val threshold = size.width * 0.3f
                                
                                when {
                                    offsetX > threshold -> {
                                        onSwipe(user, SwipeAction.LIKE)
                                    }
                                    offsetX < -threshold -> {
                                        onSwipe(user, SwipeAction.DISLIKE)
                                    }
                                    offsetY < -size.height * 0.2f -> {
                                        onSwipe(user, SwipeAction.SUPER_LIKE)
                                    }
                                    else -> {
                                        // Volta para posição original
                                        offsetX = 0f
                                        offsetY = 0f
                                        rotation = 0f
                                    }
                                }
                            }
                        ) { _, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            rotation = (offsetX / 20f).coerceIn(-15f, 15f)
                        }
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background photo
            val mainPhoto = user.photos.find { it.isMainPhoto } ?: user.photos.firstOrNull()
            
            UserImage(
                imageUrl = mainPhoto?.url,
                contentDescription = "Foto de ${user.firstName}",
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.5f
                        )
                    )
            )
            
            // User info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${user.firstName}, ${user.age}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (user.isVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verificado",
                            tint = CafezinhoLike,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (user.isPremium) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium",
                            tint = CafezinhoSuperLike,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                // Job and location
                if (!user.jobTitle.isNullOrBlank() || !user.location.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!user.jobTitle.isNullOrBlank()) {
                            Text(
                                text = user.jobTitle,
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (!user.location.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = user.distance ?: user.location,
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                
                // Bio preview
                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Info button
            if (isTopCard) {
                IconButton(
                    onClick = { onDetailsClick(user) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Mais informações",
                        tint = Color.White
                    )
                }
            }
            
            // Swipe direction indicators
            if (isTopCard) {
                // Like indicator
                androidx.compose.animation.AnimatedVisibility(
                    visible = offsetX > 50,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(32.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CafezinhoLike,
                        modifier = Modifier.rotate(-30f)
                    ) {
                        Text(
                            text = "CURTIR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Dislike indicator
                androidx.compose.animation.AnimatedVisibility(
                    visible = offsetX < -50,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(32.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CafezinhoDislike,
                        modifier = Modifier.rotate(30f)
                    ) {
                        Text(
                            text = "PASSAR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Super like indicator
                androidx.compose.animation.AnimatedVisibility(
                    visible = offsetY < -50,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(32.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CafezinhoSuperLike
                    ) {
                        Text(
                            text = "SUPER LIKE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Botões de ação na parte inferior
 */
@Composable
private fun SwipeActionButtons(
    canLike: Boolean,
    canSuperLike: Boolean,
    canRewind: Boolean,
    likesRemaining: Int,
    superLikesRemaining: Int,
    onDislikeClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSuperLikeClick: () -> Unit,
    onRewindClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rewind button
        ActionButton(
            onClick = onRewindClick,
            enabled = canRewind,
            icon = Icons.Default.Refresh, // Using Refresh as substitute for Undo
            color = CafezinhoPrimary,
            size = 48.dp
        )
        
        // Dislike button
        ActionButton(
            onClick = onDislikeClick,
            enabled = true,
            icon = Icons.Default.Close,
            color = CafezinhoDislike,
            size = 56.dp
        )
        
        // Super like button
        ActionButton(
            onClick = onSuperLikeClick,
            enabled = canSuperLike && superLikesRemaining > 0,
            icon = Icons.Default.Star,
            color = CafezinhoSuperLike,
            size = 48.dp
        )
        
        // Like button
        ActionButton(
            onClick = onLikeClick,
            enabled = canLike && likesRemaining > 0,
            icon = Icons.Default.Favorite,
            color = CafezinhoLike,
            size = 64.dp
        )
    }
}

/**
 * Botão de ação customizado
 */
@Composable
private fun ActionButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(size),
        containerColor = if (enabled) color else Color.Gray.copy(alpha = 0.3f),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (enabled) 6.dp else 2.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size * 0.4f)
        )
    }
}

/**
 * Card de loading
 */
@Composable
private fun LoadingCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = CafezinhoPrimary)
                Text(
                    text = "Encontrando pessoas incríveis...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CafezinhoOnSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card quando não há mais usuários
 */
@Composable
private fun NoMoreUsersCard(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search, // Using Search as substitute for SearchOff
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = CafezinhoPrimary
                )
                
                Text(
                    text = "Ops! Acabaram as pessoas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = CafezinhoOnSurface
                )
                
                Text(
                    text = "Que tal ampliar seus filtros ou voltar mais tarde?",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = CafezinhoOnSurfaceVariant
                )
                
                Button(
                    onClick = onRetryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafezinhoPrimary
                    )
                ) {
                    Text("Tentar novamente")
                }
            }
        }
    }
}

/**
 * Snackbar de erro
 */
@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        color = CafezinhoDislike.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.White
                )
            }
        }
    }
}

// Placeholder dialogs - implementação completa seria feita em arquivos separados
@Composable
private fun MatchDialog(
    user: SwipeUser,
    onSendMessage: (SwipeUser) -> Unit,
    onKeepSwiping: () -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: Implementar MatchDialog completo
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("É um Match! 💕") },
        text = { Text("Você e ${user.firstName} curtiram um ao outro!") },
        confirmButton = {
            Button(onClick = { onSendMessage(user) }) {
                Text("Enviar mensagem")
            }
        },
        dismissButton = {
            TextButton(onClick = onKeepSwiping) {
                Text("Continuar")
            }
        }
    )
}

@Composable
private fun UserDetailsDialog(
    user: SwipeUser,
    onDismiss: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onReport: (String) -> Unit
) {
    // TODO: Implementar UserDetailsDialog completo
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${user.firstName}, ${user.age}") },
        text = { Text(user.bio ?: "Sem biografia") },
        confirmButton = {
            Button(onClick = onLike) {
                Text("Curtir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDislike) {
                Text("Passar")
            }
        }
    )
}

@Composable
private fun FiltersDialog(
    currentFilters: SwipeFilters,
    onFiltersUpdate: (SwipeFilters) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: Implementar FiltersDialog completo
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros") },
        text = { Text("Filtros de descoberta (em desenvolvimento)") },
        confirmButton = {
            Button(onClick = { onFiltersUpdate(currentFilters) }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Preview
@Preview(showBackground = true)
@Composable
private fun SwipeScreenPreview() {
    CafezinhoTheme {
        SwipeScreen()
    }
}