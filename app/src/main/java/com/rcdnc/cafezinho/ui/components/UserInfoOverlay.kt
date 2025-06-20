package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * User information overlay component for profile cards
 * Based on legacy bottomLayout in item_user_layout.xml
 */
@Composable
fun UserInfoOverlay(
    name: String,
    age: Int,
    modifier: Modifier = Modifier,
    isVerified: Boolean = false,
    isOnline: Boolean = false,
    location: String? = null,
    distance: String? = null,
    interests: List<String> = emptyList(),
    maxInterests: Int = 3,
    backgroundGradient: Boolean = true,
    textColor: Color = Color.White
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (backgroundGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Name, age, verified, and online status row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Name and age
            Text(
                text = "$name, $age",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            
            // Verified badge
            if (isVerified) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified user",
                    tint = CafezinhoPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Online indicator
            if (isOnline) {
                OnlineIndicator()
            }
        }
        
        // Location and distance
        if (!location.isNullOrEmpty() || !distance.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                
                val locationText = buildString {
                    if (!location.isNullOrEmpty()) {
                        append(location)
                    }
                    if (!distance.isNullOrEmpty()) {
                        if (isNotEmpty()) append(" • ")
                        append(distance)
                    }
                }
                
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Interests chips
        if (interests.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(interests.take(maxInterests)) { interest ->
                    OverlayInterestChip(
                        text = interest,
                        textColor = textColor
                    )
                }
                
                // Show "+" indicator if there are more interests
                if (interests.size > maxInterests) {
                    item {
                        OverlayInterestChip(
                            text = "+${interests.size - maxInterests}",
                            textColor = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnlineIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(
                color = CafezinhoSuccess,
                shape = CircleShape
            )
    )
}

@Composable
private fun OverlayInterestChip(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Compact user info for smaller contexts
 */
@Composable
fun CompactUserInfo(
    name: String,
    age: Int,
    modifier: Modifier = Modifier,
    isVerified: Boolean = false,
    isOnline: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$name, $age",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        
        if (isVerified) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified user",
                tint = CafezinhoPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = CafezinhoSuccess,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * User info for chat/message contexts
 */
@Composable
fun ChatUserInfo(
    name: String,
    isOnline: Boolean = false,
    lastSeen: String? = null,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        val statusText = when {
            isOnline -> "Online"
            !lastSeen.isNullOrEmpty() -> "Visto $lastSeen"
            else -> ""
        }
        
        if (statusText.isNotEmpty()) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isOnline) CafezinhoSuccess else textColor.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Preview functions
@Preview(name = "UserInfoOverlay - Complete")
@Composable
private fun UserInfoOverlayCompletePreview() {
    CafezinhoTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray)
        ) {
            UserInfoOverlay(
                name = "Maria Silva",
                age = 25,
                isVerified = true,
                isOnline = true,
                location = "São Paulo",
                distance = "2km",
                interests = listOf("Música", "Viagem", "Culinária", "Arte", "Esportes"),
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview(name = "UserInfoOverlay - Minimal")
@Composable
private fun UserInfoOverlayMinimalPreview() {
    CafezinhoTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray)
        ) {
            UserInfoOverlay(
                name = "João",
                age = 28,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview(name = "CompactUserInfo")
@Composable
private fun CompactUserInfoPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactUserInfo(
                name = "Ana Costa",
                age = 30,
                isVerified = true,
                isOnline = true
            )
            
            CompactUserInfo(
                name = "Pedro Santos",
                age = 26
            )
        }
    }
}

@Preview(name = "ChatUserInfo")
@Composable
private fun ChatUserInfoPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChatUserInfo(
                name = "Carolina Mendes",
                isOnline = true
            )
            
            ChatUserInfo(
                name = "Rafael Lima",
                isOnline = false,
                lastSeen = "há 2 horas"
            )
        }
    }
}