package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
// TODO: Add Coil dependency for AsyncImage
// import coil.compose.AsyncImage
// import coil.request.ImageRequest
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * User image types for different contexts
 */
enum class UserImageType {
    AVATAR,           // Circular profile image
    CARD,             // Rectangular card image
    THUMBNAIL,        // Small preview image
    GALLERY,          // Gallery grid image
    STORY             // Story-style image with border
}

/**
 * User image states for editing
 */
enum class UserImageState {
    NORMAL,           // Regular display
    EDITABLE,         // With edit overlay
    ADDABLE,          // Empty state with add button
    LOADING,          // Loading state
    ERROR             // Error state with retry
}

/**
 * Versatile user image component for different contexts
 * Based on legacy image display patterns throughout the app
 */
@Composable
fun UserImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    type: UserImageType = UserImageType.AVATAR,
    state: UserImageState = UserImageState.NORMAL,
    size: ComponentSize = ComponentSize.MEDIUM,
    onClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    placeholder: ImageVector = Icons.Default.Person,
    hasStoryBorder: Boolean = false,
    isOnline: Boolean = false
) {
    val imageSize = getUserImageSize(size, type)
    val shape = getUserImageShape(type)
    
    Box(
        modifier = modifier.size(imageSize),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            UserImageState.ADDABLE -> {
                // Empty state with add button
                AddImagePlaceholder(
                    onClick = onAdd ?: {},
                    size = imageSize,
                    shape = shape
                )
            }
            
            UserImageState.LOADING -> {
                // Loading state
                LoadingImagePlaceholder(
                    size = imageSize,
                    shape = shape
                )
            }
            
            UserImageState.ERROR -> {
                // Error state with retry
                ErrorImagePlaceholder(
                    onClick = onRetry ?: {},
                    size = imageSize,
                    shape = shape,
                    placeholder = placeholder
                )
            }
            
            else -> {
                // Normal or editable image
                ImageContent(
                    imageUrl = imageUrl,
                    contentDescription = contentDescription,
                    size = imageSize,
                    shape = shape,
                    placeholder = placeholder,
                    onClick = onClick,
                    hasStoryBorder = hasStoryBorder
                )
                
                // Edit overlay
                if (state == UserImageState.EDITABLE && onEdit != null) {
                    EditOverlay(
                        onClick = onEdit,
                        size = imageSize
                    )
                }
                
                // Online indicator for avatars
                if (type == UserImageType.AVATAR && isOnline) {
                    OnlineIndicator(
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageContent(
    imageUrl: String?,
    contentDescription: String?,
    size: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape,
    placeholder: ImageVector,
    onClick: (() -> Unit)?,
    hasStoryBorder: Boolean
) {
    val borderModifier = if (hasStoryBorder) {
        Modifier.border(
            width = 3.dp,
            brush = Brush.linearGradient(
                colors = listOf(CafezinhoBoostStart, CafezinhoBoostEnd)
            ),
            shape = shape
        )
    } else {
        Modifier
    }
    
    Box(
        modifier = borderModifier
            .size(size)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrEmpty()) {
            // Placeholder
            Icon(
                imageVector = placeholder,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.6f),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            // TODO: Replace with AsyncImage when Coil is added
            // For now, show placeholder for all images
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = placeholder,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(size * 0.6f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AddImagePlaceholder(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar imagem",
                modifier = Modifier.size(size * 0.3f),
                tint = MaterialTheme.colorScheme.primary
            )
            if (size >= 64.dp) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Adicionar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun LoadingImagePlaceholder(
    size: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size * 0.4f),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun ErrorImagePlaceholder(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape,
    placeholder: ImageVector
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = placeholder,
            contentDescription = "Erro ao carregar imagem - Toque para tentar novamente",
            modifier = Modifier.size(size * 0.6f),
            tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun EditOverlay(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar imagem",
            modifier = Modifier.size(size * 0.4f),
            tint = Color.White
        )
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
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
    )
}

// Helper functions
private fun getUserImageSize(size: ComponentSize, type: UserImageType): androidx.compose.ui.unit.Dp {
    return when (type) {
        UserImageType.AVATAR -> when (size) {
            ComponentSize.SMALL -> 32.dp
            ComponentSize.MEDIUM -> 48.dp
            ComponentSize.LARGE -> 64.dp
        }
        UserImageType.CARD -> when (size) {
            ComponentSize.SMALL -> 120.dp
            ComponentSize.MEDIUM -> 200.dp
            ComponentSize.LARGE -> 300.dp
        }
        UserImageType.THUMBNAIL -> when (size) {
            ComponentSize.SMALL -> 40.dp
            ComponentSize.MEDIUM -> 56.dp
            ComponentSize.LARGE -> 72.dp
        }
        UserImageType.GALLERY -> when (size) {
            ComponentSize.SMALL -> 80.dp
            ComponentSize.MEDIUM -> 120.dp
            ComponentSize.LARGE -> 160.dp
        }
        UserImageType.STORY -> when (size) {
            ComponentSize.SMALL -> 56.dp
            ComponentSize.MEDIUM -> 72.dp
            ComponentSize.LARGE -> 88.dp
        }
    }
}

private fun getUserImageShape(type: UserImageType): RoundedCornerShape {
    return when (type) {
        UserImageType.AVATAR, UserImageType.STORY -> CircleShape
        UserImageType.CARD -> RoundedCornerShape(CafezinhoCornerRadius.MEDIUM.dp)
        UserImageType.THUMBNAIL -> RoundedCornerShape(CafezinhoCornerRadius.SMALL.dp)
        UserImageType.GALLERY -> RoundedCornerShape(CafezinhoCornerRadius.SMALL.dp)
    }
}