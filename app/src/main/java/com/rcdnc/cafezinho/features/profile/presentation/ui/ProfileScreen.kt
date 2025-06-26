package com.rcdnc.cafezinho.features.profile.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.features.profile.presentation.viewmodel.ProfileIntent
import com.rcdnc.cafezinho.features.profile.presentation.viewmodel.ProfileViewModel
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
// import com.rcdnc.cafezinho.ui.components.ComponentSize
import com.rcdnc.cafezinho.ui.components.UserImage

/**
 * Tela principal do Profile
 * TODO: Integrar com ProfileViewModel e API Laravel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val profileStats by viewModel.profileStats.collectAsStateWithLifecycle()
    
    // Load profile on first composition
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProfileIntent.LoadProfile("1")) // Demo user
        viewModel.handleIntent(ProfileIntent.LoadProfileStats("1"))
    }
    
    val userName = profile?.let { "${it.firstName} ${it.lastName}" } ?: "Carregando..."
    val userAge = profile?.age ?: 0
    val userBio = profile?.bio ?: ""
    val userPhotos = profile?.photos?.map { it.url } ?: emptyList()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Photo
        UserImage(
            imageUrl = null,
            contentDescription = "Foto do perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Info
        Text(
            text = "$userName, $userAge",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = userBio,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CafezinhoButton(
                text = "Editar Perfil",
                onClick = onEditClick,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Configurações")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile stats
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Estatísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Visualizações",
                        value = profileStats?.profileViews?.toString() ?: "0"
                    )
                    StatItem(
                        label = "Curtidas",
                        value = profileStats?.likes?.toString() ?: "0"
                    )
                    StatItem(
                        label = "Matches",
                        value = profileStats?.matches?.toString() ?: "0"
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Profile completion
                LinearProgressIndicator(
                    progress = (profile?.profileCompletion ?: 0) / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Text(
                    text = "Perfil ${profile?.profileCompletion ?: 0}% completo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}