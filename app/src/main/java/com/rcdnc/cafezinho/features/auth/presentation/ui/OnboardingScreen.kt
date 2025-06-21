package com.rcdnc.cafezinho.features.auth.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import kotlinx.coroutines.launch

/**
 * Onboarding Screen - 5 slides de introdução
 * Equivalente ao PreLoginActivity do legacy
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    
    val slides = listOf(
        OnboardingSlide(
            title = "Bem-vindo ao Cafezinho",
            description = "O maior app de relacionamento em Portugal",
            emoji = "🇵🇹"
        ),
        OnboardingSlide(
            title = "Brasileiros em Lisboa",
            description = "Conheça novos brasileiros em Lisboa e em todo Portugal",
            emoji = "🇧🇷"
        ),
        OnboardingSlide(
            title = "Descubra Matches",
            description = "Descubra quem está afim de você e faça novas conexões",
            emoji = "💝"
        ),
        OnboardingSlide(
            title = "Filtros Inteligentes",
            description = "Encontre brasileiros em Portugal com nossos filtros",
            emoji = "🔍"
        ),
        OnboardingSlide(
            title = "Dê Match!",
            description = "Dê match e tome um cafezinho juntos",
            emoji = "☕"
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
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
                // Logo
                Text(
                    text = "☕ cafezinho",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Skip button
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
                OnboardingSlideContent(slide = slides[page])
            }
            
            // Indicadores e navegação
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
                    repeat(5) { index ->
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
                
                // Continue/Finish button
                CafezinhoButton(
                    text = if (pagerState.currentPage == 4) "Começar" else "Continuar",
                    onClick = {
                        if (pagerState.currentPage == 4) {
                            onNavigateToLogin()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun OnboardingSlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji icon
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slide.emoji,
                    fontSize = 48.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
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

data class OnboardingSlide(
    val title: String,
    val description: String,
    val emoji: String
)

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    CafezinhoTheme {
        OnboardingScreen()
    }
}