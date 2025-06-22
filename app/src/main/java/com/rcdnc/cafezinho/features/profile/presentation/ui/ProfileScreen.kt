package com.rcdnc.cafezinho.features.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.model.ProfilePhoto
import com.rcdnc.cafezinho.features.profile.domain.model.Interest
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileStats
import com.rcdnc.cafezinho.features.profile.presentation.viewmodel.ProfileIntent
import com.rcdnc.cafezinho.features.profile.presentation.viewmodel.ProfileState
import com.rcdnc.cafezinho.features.profile.presentation.viewmodel.ProfileViewModel
import com.rcdnc.cafezinho.ui.components.ComponentSize
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Tela principal de Profile
 * Migração de ProfileFragment + activity_edit_profile.xml
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val profileStats by viewModel.profileStats.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CafezinhoBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with main photo and basic info
        item {
            ProfileHeader(
                profile = profile,
                onEditClick = onNavigateToEdit
            )
        }
        
        // Profile completion progress
        item {
            profileStats?.let { stats ->
                ProfileCompletionCard(
                    stats = stats,
                    isComplete = viewModel.isProfileComplete()
                )
            }
        }
        
        // Photo gallery
        item {
            profile?.let { profileData ->
                PhotoGallerySection(
                    photos = profileData.photos,
                    canUploadMore = viewModel.canUploadMorePhotos(),
                    onAddPhotoClick = { 
                        // TODO: Implementar seleção de foto
                    },
                    onPhotoClick = { photo ->
                        // TODO: Implementar visualização de foto
                    }
                )
            }
        }
        
        // Bio section
        item {
            profile?.bio?.let { bio ->
                ProfileInfoCard(
                    title = "Sobre mim",
                    content = bio,
                    icon = Icons.Default.Person
                )
            }
        }
        
        // Interests section
        item {
            profile?.let { profileData ->
                if (profileData.interests.isNotEmpty()) {
                    InterestsSection(
                        interests = profileData.interests.filter { it.isSelected }
                    )
                }
            }
        }
        
        // Work info
        item {
            profile?.let { profileData ->
                if (!profileData.jobTitle.isNullOrBlank() || !profileData.company.isNullOrBlank()) {
                    WorkInfoSection(
                        jobTitle = profileData.jobTitle,
                        company = profileData.company,
                        school = profileData.school
                    )
                }
            }
        }
        
        // Profile stats
        item {
            profileStats?.let { stats ->
                ProfileStatsSection(stats = stats)
            }
        }
        
        // Loading/Error states
        item {
            when (state) {
                is ProfileState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CafezinhoPrimary)
                    }
                }
                
                is ProfileState.Error -> {
                    ErrorCard(
                        message = state.message,
                        onRetry = { viewModel.handleIntent(ProfileIntent.LoadProfile()) }
                    )
                }
                
                else -> { /* No action needed */ }
            }
        }
    }
}

/**
 * Header do perfil com foto principal e informações básicas
 */
@Composable
private fun ProfileHeader(
    profile: ProfileData?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main profile photo with progress ring
            Box {
                val mainPhotoUrl = profile?.photos?.find { it.isMainPhoto }?.url 
                    ?: profile?.photos?.firstOrNull()?.url
                
                UserImage(
                    imageUrl = mainPhotoUrl,
                    size = ComponentSize.ExtraLarge,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.clip(CircleShape)
                )
                
                // Progress ring overlay
                if (profile != null) {
                    CircularProgressIndicator(
                        progress = profile.profileCompletion / 100f,
                        modifier = Modifier
                            .size(ComponentSize.ExtraLarge)
                            .align(Alignment.Center),
                        color = CafezinhoPrimary,
                        strokeWidth = 4.dp,
                        trackColor = CafezinhoOutline
                    )
                }
                
                // Edit button
                FloatingActionButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp),
                    containerColor = CafezinhoPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = CafezinhoOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Premium/Verified badges
                if (profile?.isPremium == true) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(CafezinhoMatch, CafezinhoSuperLike)
                                ),
                                shape = CircleShape
                            )
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                if (profile?.isVerified == true) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = CafezinhoLike,
                                shape = CircleShape
                            )
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verificado",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name and age
            Text(
                text = "${profile?.firstName ?: "Usuário"}, ${profile?.age ?: ""}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CafezinhoOnSurface
            )
            
            // Location and distance
            profile?.location?.let { location ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = CafezinhoOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CafezinhoOnSurfaceVariant
                    )
                    profile.distance?.let { distance ->
                        Text(
                            text = " • $distance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CafezinhoOnSurfaceVariant
                        )
                    }
                }
            }
            
            // Online status
            if (profile?.isOnline == true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Text(
                        text = "Online agora",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green
                    )
                }
            }
        }
    }
}

/**
 * Card de progresso do perfil
 */
@Composable
private fun ProfileCompletionCard(
    stats: ProfileStats,
    isComplete: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) CafezinhoLike.copy(alpha = 0.1f) else CafezinhoSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isComplete) "Perfil completo!" else "Complete seu perfil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isComplete) CafezinhoLike else CafezinhoOnSurface
                )
                Text(
                    text = "${stats.profileCompletion}% concluído",
                    style = MaterialTheme.typography.bodySmall,
                    color = CafezinhoOnSurfaceVariant
                )
            }
            
            Icon(
                imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.TrendingUp,
                contentDescription = null,
                tint = if (isComplete) CafezinhoLike else CafezinhoPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Galeria de fotos
 */
@Composable
private fun PhotoGallerySection(
    photos: List<ProfilePhoto>,
    canUploadMore: Boolean,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (ProfilePhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fotos (${photos.size}/6)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = CafezinhoOnSurface
                )
                
                if (canUploadMore) {
                    TextButton(onClick = onAddPhotoClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos.sortedBy { it.orderSequence }) { photo ->
                    Card(
                        onClick = { onPhotoClick(photo) },
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        UserImage(
                            imageUrl = photo.url,
                            contentDescription = "Foto do perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Seção de interesses
 */
@Composable
private fun InterestsSection(
    interests: List<Interest>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Interesses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = CafezinhoOnSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Wrap layout for interest chips
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                interests.chunked(3).forEach { rowInterests ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowInterests.forEach { interest ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = interest.name,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = CafezinhoPrimary.copy(alpha = 0.1f),
                                    labelColor = CafezinhoPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Seção de informações de trabalho
 */
@Composable
private fun WorkInfoSection(
    jobTitle: String?,
    company: String?,
    school: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Trabalho",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = CafezinhoOnSurface
            )
            
            jobTitle?.let { job ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = CafezinhoOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = job,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CafezinhoOnSurface
                    )
                }
            }
            
            company?.let { comp ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = CafezinhoOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = comp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CafezinhoOnSurface
                    )
                }
            }
            
            school?.let { sch ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = CafezinhoOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = sch,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CafezinhoOnSurface
                    )
                }
            }
        }
    }
}

/**
 * Seção de estatísticas do perfil
 */
@Composable
private fun ProfileStatsSection(
    stats: ProfileStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estatísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = CafezinhoOnSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Visualizações",
                    value = stats.profileViews.toString(),
                    color = CafezinhoPrimary
                )
                
                StatItem(
                    title = "Likes",
                    value = stats.likes.toString(),
                    color = CafezinhoLike
                )
                
                StatItem(
                    title = "Matches",
                    value = stats.matches.toString(),
                    color = CafezinhoMatch
                )
            }
        }
    }
}

@Composable
private fun StatItem(
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
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = CafezinhoOnSurfaceVariant
        )
    }
}

/**
 * Card genérico para informações do perfil
 */
@Composable
private fun ProfileInfoCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CafezinhoPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = CafezinhoOnSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = CafezinhoOnSurface,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Card de erro
 */
@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CafezinhoDislike.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = CafezinhoDislike,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ops! Algo deu errado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = CafezinhoOnSurface
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = CafezinhoOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(onClick = onRetry) {
                Text("Tentar novamente")
            }
        }
    }
}

// Preview functions
@Preview(name = "ProfileScreen")
@Composable
private fun ProfileScreenPreview() {
    CafezinhoTheme {
        ProfileScreen(
            onNavigateToEdit = { }
        )
    }
}

@Preview(name = "ProfileHeader")
@Composable
private fun ProfileHeaderPreview() {
    CafezinhoTheme {
        ProfileHeader(
            profile = sampleProfile,
            onEditClick = { }
        )
    }
}

// Sample data for previews
private val sampleProfile = ProfileData(
    id = "1",
    firstName = "Ana",
    age = 25,
    bio = "Adoro viajar, conhecer culturas diferentes e experimentar comidas novas. Sempre em busca de novas aventuras!",
    location = "São Paulo, SP",
    distance = "2 km",
    profileCompletion = 85,
    isVerified = true,
    isPremium = true,
    isOnline = true,
    jobTitle = "Designer",
    company = "Tech Company",
    photos = listOf(
        ProfilePhoto("1", "url1", 0, isMainPhoto = true),
        ProfilePhoto("2", "url2", 1),
        ProfilePhoto("3", "url3", 2)
    ),
    interests = listOf(
        Interest("1", "Viajar", isSelected = true),
        Interest("2", "Culinária", isSelected = true),
        Interest("3", "Fotografia", isSelected = true)
    )
)