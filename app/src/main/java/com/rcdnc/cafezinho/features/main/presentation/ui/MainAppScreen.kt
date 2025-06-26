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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
 * Main App Screen - Com Bottom Navigation + Navigation Component
 * Integra bottom navigation com NavHost para navegação completa
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onLogout: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    // Observar mudanças de navegação para sincronizar tabs
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Atualizar tab selecionada baseado na rota atual
    LaunchedEffect(currentRoute) {
        val tabIndex = navigationItems.indexOfFirst { it.route == currentRoute }
        if (tabIndex != -1 && tabIndex != selectedTab) {
            selectedTab = tabIndex
        }
    }
    
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
                    IconButton(onClick = { showSettingsDialog = true }) {
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
                        onClick = { 
                            selectedTab = index
                            navController.navigate(item.route) {
                                // Evitar múltiplas cópias da mesma tela
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // NavHost integrado com bottom navigation
        CafezinhoNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = navigationItems[selectedTab].route
        )
    }
    
    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Configurações") },
            text = {
                Column {
                    Text("Versão: 1.0.0 (Demo)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Usuário: João Demo")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ID: 1")
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSettingsDialog = false
                    onLogout()
                }) {
                    Text("Sair", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}





@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    CafezinhoTheme {
        MainAppScreen()
    }
}