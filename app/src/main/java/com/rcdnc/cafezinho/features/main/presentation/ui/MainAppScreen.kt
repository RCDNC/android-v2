package com.rcdnc.cafezinho.features.main.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.features.swipe.presentation.ui.SwipeScreen
import com.rcdnc.cafezinho.features.matches.presentation.ui.MatchesScreen
import com.rcdnc.cafezinho.features.chat.presentation.ui.ChatListScreen
import com.rcdnc.cafezinho.features.profile.presentation.ui.ProfileScreen
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

// Navigation items para BottomNav
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavigationItem(
        title = "Descobrir",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        route = "swipe"
    ),
    NavigationItem(
        title = "Matches",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        route = "matches"
    ),
    NavigationItem(
        title = "Chat",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        route = "chat"
    ),
    NavigationItem(
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = "profile"
    )
)

/**
 * Main App Screen - Com Bottom Navigation
 * Mostra a estrutura principal do app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "☕ ${navigationItems[selectedTab].title}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Conteúdo baseado na tab selecionada
        when (selectedTab) {
            0 -> SwipeScreen(
                modifier = Modifier.padding(paddingValues),
                onNavigateToProfile = { selectedTab = 3 },
                onNavigateToChat = { userId -> selectedTab = 2 }
            )
            1 -> MatchesScreen(
                modifier = Modifier.padding(paddingValues),
                onMatchClick = { match -> selectedTab = 2 }, // Navigate to chat on match click
                onMatchDetail = { match -> /* TODO: Navigate to match detail */ },
                onBackClick = { /* In main screen, no back action needed */ }
            )
            2 -> ChatListScreen(
                modifier = Modifier.padding(paddingValues),
                onConversationClick = { conversation -> /* TODO: Navigate to individual chat */ },
                onBackClick = { /* In main screen, no back action needed */ }
            )
            3 -> ProfileScreen(
                modifier = Modifier.padding(paddingValues),
                onEditClick = { /* TODO: Navigate to edit profile */ },
                onSettingsClick = onLogout, // Use logout as settings action for now
                onBackClick = { /* In main screen, no back action needed */ }
            )
        }
    }
}





@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    CafezinhoTheme {
        MainAppScreen()
    }
}