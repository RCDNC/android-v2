package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * User image pager component for multiple profile images
 * Based on legacy ImageView + navigation buttons in item_user_layout.xml
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserImagePager(
    images: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: ((direction: Int) -> Unit)? = null,
    showNavigationArrows: Boolean = true,
    showPageIndicators: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {
        UserImage(
            imageUrl = null,
            contentDescription = "User image placeholder",
            type = UserImageType.CARD,
            size = ComponentSize.LARGE,
            modifier = Modifier.fillMaxSize()
        )
    }
) {
    val pagerState = rememberPagerState(pageCount = { maxOf(1, images.size) })
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Main image pager
        if (images.isEmpty()) {
            // Show placeholder when no images
            placeholder()
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val imageUrl = images.getOrNull(page)
                
                if (imageUrl.isNullOrEmpty()) {
                    placeholder()
                } else {
                    UserImage(
                        imageUrl = imageUrl,
                        contentDescription = "User image ${page + 1}",
                        type = UserImageType.CARD,
                        size = ComponentSize.LARGE,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        
        // Navigation areas (invisible clickable areas)
        if (images.size > 1 && onImageClick != null) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Left navigation area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { 
                            if (pagerState.currentPage > 0) {
                                onImageClick(-1)
                            }
                        }
                )
                
                // Right navigation area  
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { 
                            if (pagerState.currentPage < images.size - 1) {
                                onImageClick(1)
                            }
                        }
                )
            }
        }
        
        // Navigation arrows (visible when hovering/active)
        if (showNavigationArrows && images.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left arrow
                if (pagerState.currentPage > 0) {
                    NavigationArrow(
                        direction = -1,
                        onClick = { onImageClick?.invoke(-1) }
                    )
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
                
                // Right arrow
                if (pagerState.currentPage < images.size - 1) {
                    NavigationArrow(
                        direction = 1,
                        onClick = { onImageClick?.invoke(1) }
                    )
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
        
        // Page indicators
        if (showPageIndicators && images.size > 1) {
            PageIndicators(
                pageCount = images.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun NavigationArrow(
    direction: Int, // -1 for left, 1 for right
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (direction < 0) Icons.Default.KeyboardArrowLeft else Icons.Default.KeyboardArrowRight,
            contentDescription = if (direction < 0) "Previous image" else "Next image",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.3f),
                        Color.Black.copy(alpha = 0.5f),
                        Color.Black.copy(alpha = 0.3f)
                    )
                ),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            PageIndicator(
                isActive = index == currentPage,
                modifier = Modifier.size(
                    width = if (index == currentPage) 16.dp else 6.dp,
                    height = 6.dp
                )
            )
        }
    }
}

@Composable
private fun PageIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                shape = CircleShape
            )
    )
}

/**
 * Image gallery component for profile editing
 * Shows multiple images in a grid with add/edit functionality
 */
@Composable
fun UserImageGallery(
    images: List<String>,
    maxImages: Int = 6,
    onAddImage: () -> Unit,
    onEditImage: (index: Int) -> Unit,
    onRemoveImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Existing images
        items(images.size) { index ->
            UserImage(
                imageUrl = images[index],
                contentDescription = "User image ${index + 1}",
                type = UserImageType.GALLERY,
                size = ComponentSize.MEDIUM,
                state = UserImageState.EDITABLE,
                onEdit = { onEditImage(index) },
                modifier = Modifier.aspectRatio(1f)
            )
        }
        
        // Add image slot
        if (images.size < maxImages) {
            item {
                UserImage(
                    imageUrl = null,
                    contentDescription = "Add image",
                    type = UserImageType.GALLERY,
                    size = ComponentSize.MEDIUM,
                    state = UserImageState.ADDABLE,
                    onAdd = onAddImage,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

// Preview functions
@Preview(name = "UserImagePager - Single Image")
@Composable
private fun UserImagePagerSinglePreview() {
    CafezinhoTheme {
        Box(
            modifier = Modifier.size(300.dp, 400.dp)
        ) {
            UserImagePager(
                images = listOf("https://example.com/image1.jpg"),
                onImageClick = { direction ->
                    // Handle navigation
                }
            )
        }
    }
}

@Preview(name = "UserImagePager - Multiple Images")
@Composable
private fun UserImagePagerMultiplePreview() {
    CafezinhoTheme {
        Box(
            modifier = Modifier.size(300.dp, 400.dp)
        ) {
            UserImagePager(
                images = listOf(
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg",
                    "https://example.com/image3.jpg"
                ),
                onImageClick = { direction ->
                    // Handle navigation
                }
            )
        }
    }
}

@Preview(name = "UserImagePager - Empty")
@Composable
private fun UserImagePagerEmptyPreview() {
    CafezinhoTheme {
        Box(
            modifier = Modifier.size(300.dp, 400.dp)
        ) {
            UserImagePager(
                images = emptyList(),
                onImageClick = { direction ->
                    // Handle navigation
                }
            )
        }
    }
}

@Preview(name = "UserImageGallery")
@Composable
private fun UserImageGalleryPreview() {
    CafezinhoTheme {
        UserImageGallery(
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