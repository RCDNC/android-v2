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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.navigation.CafezinhoNavHost
import com.rcdnc.cafezinho.navigation.CafezinhoNavigation
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
        route = CafezinhoNavigation.SWIPE
    ),
    NavigationItem(
        title = "Matches",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        route = CafezinhoNavigation.MATCHES
    ),
    NavigationItem(
        title = "Chat",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        route = CafezinhoNavigation.CHAT_LIST
    ),
    NavigationItem(
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = CafezinhoNavigation.PROFILE
    )
)

/**
 * Main App Screen - Com Navigation integrada
 * Mostra a estrutura principal do app com navegação completa
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Encontra o item de navegação atual
    val currentNavigationItem = navigationItems.find { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    } ?: navigationItems.first()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "☕ ${currentNavigationItem.title}",
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
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            val isSelected = currentDestination?.hierarchy?.any { 
                                it.route == item.route 
                            } == true
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == item.route 
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // NavHost com todas as telas
        CafezinhoNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// Conteúdo das tabs antigas removido - agora usamos navegação real com as features implementadas

@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    CafezinhoTheme {
        MainAppScreen()
    }
}