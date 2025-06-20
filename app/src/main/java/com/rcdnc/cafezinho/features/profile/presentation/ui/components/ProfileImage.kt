package com.rcdnc.cafezinho.features.profile.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.components.UserImageState
import com.rcdnc.cafezinho.ui.components.UserImageType
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Profile image component with upload and edit functionality
 * Based on UserImage base component with profile-specific features
 */
@Composable
fun ProfileImage(
    imageUrl: String?,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    size: ComponentSize = ComponentSize.LARGE,
    type: UserImageType = UserImageType.AVATAR,
    isEditable: Boolean = true,
    isUploading: Boolean = false,
    onEdit: (() -> Unit)? = null,
    contentDescription: String = "Profile image"
) {
    val imageState = when {
        isUploading -> UserImageState.LOADING
        imageUrl.isNullOrEmpty() -> UserImageState.ADDABLE
        isEditable -> UserImageState.EDITABLE
        else -> UserImageState.NORMAL
    }
    
    UserImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        type = type,
        size = size,
        state = imageState,
        onAdd = onUpload,
        onEdit = onEdit ?: onUpload,
        modifier = modifier
    )
}

/**
 * Profile image gallery for managing multiple profile photos
 */
@Composable
fun ProfileImageGallery(
    images: List<String>,
    onAddImage: () -> Unit,
    onEditImage: (index: Int) -> Unit,
    onRemoveImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    maxImages: Int = 6,
    isUploading: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Existing images
        items(images.size) { index ->
            ProfileImage(
                imageUrl = images[index],
                onUpload = { onEditImage(index) },
                size = ComponentSize.MEDIUM,
                type = UserImageType.GALLERY,
                isEditable = true,
                onEdit = { onEditImage(index) },
                contentDescription = "Profile image ${index + 1}",
                modifier = Modifier.aspectRatio(1f)
            )
        }
        
        // Add image slot
        if (images.size < maxImages) {
            item {
                ProfileImage(
                    imageUrl = null,
                    onUpload = onAddImage,
                    size = ComponentSize.MEDIUM,
                    type = UserImageType.GALLERY,
                    isEditable = true,
                    isUploading = isUploading,
                    contentDescription = "Add profile image",
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

/**
 * Main profile image with upload progress and validation
 */
@Composable
fun MainProfileImage(
    imageUrl: String?,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    isUploading: Boolean = false,
    uploadProgress: Float = 0f,
    hasError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        val imageState = when {
            hasError -> UserImageState.ERROR
            isUploading -> UserImageState.LOADING
            imageUrl.isNullOrEmpty() -> UserImageState.ADDABLE
            else -> UserImageState.EDITABLE
        }
        
        UserImage(
            imageUrl = imageUrl,
            contentDescription = "Main profile image",
            type = UserImageType.AVATAR,
            size = ComponentSize.LARGE,
            state = imageState,
            onAdd = onUpload,
            onEdit = onUpload,
            onRetry = onRetry ?: onUpload,
            modifier = Modifier.size(120.dp)
        )
        
        // Upload progress indicator
        if (isUploading && uploadProgress > 0f) {
            CircularProgressIndicator(
                progress = uploadProgress,
                modifier = Modifier
                    .size(120.dp)
                    .padding(4.dp),
                color = CafezinhoPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * Profile image selector with reorder functionality
 */
@Composable
fun ProfileImageSelector(
    images: List<String>,
    selectedIndex: Int,
    onImageSelected: (index: Int) -> Unit,
    onAddImage: () -> Unit,
    onRemoveImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    maxImages: Int = 6
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main selected image
        if (images.isNotEmpty()) {
            MainProfileImage(
                imageUrl = images.getOrNull(selectedIndex),
                onUpload = { onImageSelected(selectedIndex) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Image thumbnails
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(200.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Existing images
            items(images.size) { index ->
                val isSelected = index == selectedIndex
                
                Card(
                    onClick = { onImageSelected(index) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CafezinhoPrimary.copy(alpha = 0.1f) 
                                       else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                ) {
                    UserImage(
                        imageUrl = images[index],
                        contentDescription = "Profile image ${index + 1}",
                        type = UserImageType.THUMBNAIL,
                        size = ComponentSize.SMALL,
                        state = UserImageState.NORMAL,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
            
            // Add image slot
            if (images.size < maxImages) {
                item {
                    ProfileImage(
                        imageUrl = null,
                        onUpload = onAddImage,
                        size = ComponentSize.SMALL,
                        type = UserImageType.THUMBNAIL,
                        isEditable = true,
                        contentDescription = "Add profile image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

// Preview functions
@Preview(name = "ProfileImage - States")
@Composable
private fun ProfileImageStatesPreview() {
    CafezinhoTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileImage(
                imageUrl = null,
                onUpload = { },
                contentDescription = "Empty state"
            )
            
            ProfileImage(
                imageUrl = "https://example.com/image.jpg",
                onUpload = { },
                contentDescription = "With image"
            )
            
            ProfileImage(
                imageUrl = null,
                onUpload = { },
                isUploading = true,
                contentDescription = "Uploading"
            )
        }
    }
}

@Preview(name = "ProfileImageGallery")
@Composable
private fun ProfileImageGalleryPreview() {
    CafezinhoTheme {
        ProfileImageGallery(
            images = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
            ),
            onAddImage = { },
            onEditImage = { },
            onRemoveImage = { }
        )
    }
}

@Preview(name = "MainProfileImage")
@Composable
private fun MainProfileImagePreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainProfileImage(
                imageUrl = null,
                onUpload = { }
            )
            
            MainProfileImage(
                imageUrl = "https://example.com/image.jpg",
                onUpload = { },
                isUploading = true,
                uploadProgress = 0.7f
            )
            
            MainProfileImage(
                imageUrl = null,
                onUpload = { },
                hasError = true,
                onRetry = { }
            )
        }
    }
}

@Preview(name = "ProfileImageSelector")
@Composable
private fun ProfileImageSelectorPreview() {
    CafezinhoTheme {
        ProfileImageSelector(
            images = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg",
                "https://example.com/image4.jpg"
            ),
            selectedIndex = 1,
            onImageSelected = { },
            onAddImage = { },
            onRemoveImage = { }
        )
    }
}