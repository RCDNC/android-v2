package com.rcdnc.cafezinho.features.auth.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import kotlinx.coroutines.launch

/**
 * Tutorial Screen - Como usar o app de swipe
 * Equivalente ao TutorialActivity do legacy
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(
    onNavigateToMain: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    
    val slides = listOf(
        TutorialSlide(
            title = "Deslize para direita",
            description = "Deslize para direita para curtir alguém que você gostou",
            icon = Icons.Default.Favorite,
            color = Color(0xFF4CAF50), // Green
            gesture = "→"
        ),
        TutorialSlide(
            title = "Deslize para esquerda", 
            description = "Deslize para esquerda para passar para o próximo perfil",
            icon = Icons.Default.Close,
            color = Color(0xFFF44336), // Red
            gesture = "←"
        ),
        TutorialSlide(
            title = "Ainda tem mais!",
            description = "Use Rewind para voltar, SuperLike para se destacar, e Boost para ser visto por mais pessoas",
            icon = Icons.Default.Favorite,
            color = MaterialTheme.colorScheme.primary,
            gesture = "✨"
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header com Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tutorial",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Pular",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                TutorialSlideContent(slide = slides[page])
            }
            
            // Navegação
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (index == pagerState.currentPage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (pagerState.currentPage == 0) {
                        Arrangement.End
                    } else {
                        Arrangement.SpaceBetween
                    }
                ) {
                    // Previous button (only show after first slide)
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Text("Anterior")
                        }
                    }
                    
                    // Next/Finish button
                    CafezinhoButton(
                        text = if (pagerState.currentPage == 2) "VAMOS COMEÇAR" else "Próximo",
                        onClick = {
                            if (pagerState.currentPage == 2) {
                                onNavigateToMain()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialSlideContent(slide: TutorialSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gesture animation area
        Card(
            modifier = Modifier
                .size(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = slide.color.copy(alpha = 0.1f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = slide.icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = slide.color
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = slide.gesture,
                        fontSize = 48.sp,
                        color = slide.color
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Title
        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = slide.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

data class TutorialSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val gesture: String
)

@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    CafezinhoTheme {
        TutorialScreen()
    }
}