package com.rcdnc.cafezinho.features.matches.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// import com.google.accompanist.swiperefresh.SwipeRefresh
// import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rcdnc.cafezinho.features.matches.domain.model.Match
import com.rcdnc.cafezinho.features.matches.presentation.viewmodel.MatchesIntent
import com.rcdnc.cafezinho.features.matches.presentation.viewmodel.MatchesState
import com.rcdnc.cafezinho.features.matches.presentation.viewmodel.MatchesViewModel
// import com.rcdnc.cafezinho.ui.components.ComponentSize
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Tela principal de Matches
 * Migração de MatchFragment + item_matchs_layout.xml
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onMatchClick: (Match) -> Unit = {},
    onMatchDetail: (Match) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    
    val isRefreshing = state is MatchesState.Loading
    
    // Force load matches on first composition if idle
    LaunchedEffect(Unit) {
        if (state is MatchesState.Idle) {
            viewModel.handleIntent(MatchesIntent.LoadMatches())
        }
    }
    // val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    
    // Handle navigation
    LaunchedEffect(state) {
        when (val currentState = state) {
            is MatchesState.NavigateToChat -> {
                // Encontra o match pelo userId e chama callback
                val match = matches.find { it.otherUserId == currentState.otherUserId }
                if (match != null) {
                    onMatchClick(match)
                }
                viewModel.handleIntent(MatchesIntent.ClearError)
            }
            else -> { /* No action needed */ }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CafezinhoBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Matches",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CafezinhoOnSurface
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CafezinhoBackground
            )
        )
        
        // Stats Row
        if (matches.isNotEmpty()) {
            MatchStatsRow(
                totalMatches = matches.size,
                newMatches = viewModel.getNewMatchesCount(),
                superLikes = viewModel.getSuperLikesCount(),
                activeChats = viewModel.getActiveConversationsCount(),
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (val currentState = state) {
                is MatchesState.Loading -> {
                    LoadingState()
                }
                
                is MatchesState.Success -> {
                    MatchesGrid(
                        matches = currentState.matches,
                        onMatchClick = { match ->
                            viewModel.handleIntent(MatchesIntent.OpenChat(match.otherUserId))
                        },
                        onDeleteMatch = { match ->
                            viewModel.handleIntent(MatchesIntent.DeleteMatch(match.id, match.otherUserId))
                        }
                    )
                }
                
                is MatchesState.Empty -> {
                    EmptyMatchesState()
                }
                
                is MatchesState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        onRetry = { viewModel.handleIntent(MatchesIntent.RefreshMatches) }
                    )
                }
                
                else -> {
                    // Idle state - show matches if available, otherwise loading
                    if (matches.isNotEmpty()) {
                        MatchesGrid(
                            matches = matches,
                            onMatchClick = { match ->
                                viewModel.handleIntent(MatchesIntent.OpenChat(match.otherUserId))
                            },
                            onDeleteMatch = { match ->
                                viewModel.handleIntent(MatchesIntent.DeleteMatch(match.id, match.otherUserId))
                            }
                        )
                    } else {
                        LoadingState()
                    }
                }
            }
        }
    }
}

/**
 * Grid de matches baseado em item_matchs_layout.xml
 */
@Composable
private fun MatchesGrid(
    matches: List<Match>,
    onMatchClick: (Match) -> Unit,
    onDeleteMatch: (Match) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = matches,
            key = { it.id }
        ) { match ->
            MatchCard(
                match = match,
                onClick = { onMatchClick(match) },
                onDelete = { onDeleteMatch(match) }
            )
        }
    }
}

/**
 * Card individual de match
 * Baseado em item_matchs_layout.xml com formato coração
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchCard(
    match: Match,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(0.75f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CafezinhoSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // User Image (formato coração seria ideal, mas usando circular por simplicidade)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar com indicadores
                Box {
                    UserImage(
                        imageUrl = match.otherUserAvatar,
                        contentDescription = "Foto de ${match.otherUserName}",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    
                    // Badge para usuário premium
                    if (match.isPremiumUser) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(CafezinhoMatch, CafezinhoSuperLike)
                                    ),
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Premium",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    
                    // Badge para super like
                    if (match.isSuperLike) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(
                                    color = CafezinhoSuperLike,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Super Like",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    
                    // Indicador online
                    if (match.isOnline) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(12.dp)
                                .background(
                                    color = Color.Green,
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Nome e idade
                Text(
                    text = "${match.otherUserName}${match.otherUserAge?.let { ", $it" } ?: ""}",
                    style = MaterialTheme.typography.titleSmall,
                    color = CafezinhoOnSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                
                // Distância (se disponível)
                match.otherUserDistance?.let { distance ->
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = CafezinhoOnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Indicador de mensagens
                if (match.hasUnreadMessages) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = CafezinhoPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Nova mensagem",
                            style = MaterialTheme.typography.labelSmall,
                            color = CafezinhoOnPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                } else if (match.userMessagesCount > 0 || match.otherUserMessagesCount > 0) {
                    Text(
                        text = "${match.userMessagesCount + match.otherUserMessagesCount} mensagens",
                        style = MaterialTheme.typography.labelSmall,
                        color = CafezinhoOnSurfaceVariant
                    )
                }
            }
            
            // Badge "NOVO" para matches recentes
            if (match.isNewMatch) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CafezinhoLike
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "NOVO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // Botão delete no canto superior direito
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deletar match",
                    tint = CafezinhoOnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Deletar Match")
            },
            text = {
                Text("Tem certeza que deseja deletar o match com ${match.otherUserName}? Esta ação não pode ser desfeita.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Deletar", color = CafezinhoDislike)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Row de estatísticas dos matches
 */
@Composable
private fun MatchStatsRow(
    totalMatches: Int,
    newMatches: Int,
    superLikes: Int,
    activeChats: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            title = "Total",
            value = totalMatches.toString(),
            color = CafezinhoPrimary
        )
        
        StatCard(
            title = "Novos",
            value = newMatches.toString(),
            color = CafezinhoLike
        )
        
        StatCard(
            title = "Super Likes",
            value = superLikes.toString(),
            color = CafezinhoSuperLike
        )
        
        StatCard(
            title = "Conversas",
            value = activeChats.toString(),
            color = CafezinhoMatch
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = CafezinhoOnSurfaceVariant
        )
    }
}

/**
 * Estado de loading
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = CafezinhoPrimary
        )
    }
}

/**
 * Estado vazio
 */
@Composable
private fun EmptyMatchesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💕",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Nenhum match ainda",
            style = MaterialTheme.typography.titleLarge,
            color = CafezinhoOnSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Continue curtindo perfis para encontrar suas conexões especiais!",
            style = MaterialTheme.typography.bodyMedium,
            color = CafezinhoOnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado de erro
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😞",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Ops! Algo deu errado",
            style = MaterialTheme.typography.titleLarge,
            color = CafezinhoOnSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = CafezinhoOnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = CafezinhoPrimary
            )
        ) {
            Text("Tentar novamente")
        }
    }
}

// Preview functions
@Preview(name = "MatchesScreen - Loading")
@Composable
private fun MatchesScreenLoadingPreview() {
    CafezinhoTheme {
        LoadingState()
    }
}

@Preview(name = "MatchesScreen - Empty")
@Composable
private fun MatchesScreenEmptyPreview() {
    CafezinhoTheme {
        EmptyMatchesState()
    }
}

@Preview(name = "MatchCard")
@Composable
private fun MatchCardPreview() {
    CafezinhoTheme {
        MatchCard(
            match = sampleMatch,
            onClick = { },
            onDelete = { },
            modifier = Modifier.width(200.dp)
        )
    }
}

// Sample data for previews
private val sampleMatch = Match(
    id = "1",
    userId = "me",
    otherUserId = "user1",
    otherUserName = "Ana Silva",
    otherUserAvatar = "https://example.com/avatar.jpg",
    otherUserAge = 25,
    otherUserDistance = "2 km",
    isSuperLike = true,
    isPremiumUser = true,
    isNewMatch = true,
    isOnline = true,
    hasUnreadMessages = true,
    userMessagesCount = 3,
    otherUserMessagesCount = 5
)