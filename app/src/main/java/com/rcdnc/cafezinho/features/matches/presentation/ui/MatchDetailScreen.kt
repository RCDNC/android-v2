package com.rcdnc.cafezinho.features.matches.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Tela de detalhe do Match
 * Mostra informações completas sobre o match
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onStartChatClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // TODO: Carregar dados do match baseado no userId
    // Por enquanto usando dados demo
    val matchName = when (userId) {
        "2" -> "Maria Silva"
        "3" -> "João Santos"
        "4" -> "Ana Costa"
        "5" -> "Pedro Oliveira"
        "6" -> "Julia Mendes"
        "7" -> "Lucas Ferreira"
        else -> "Usuário $userId"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Delete match */ }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar match",
                            tint = CafezinhoDislike
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onStartChatClick(userId) },
                containerColor = CafezinhoPrimary,
                contentColor = CafezinhoOnPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Conversa")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar grande
            Box(
                modifier = Modifier.size(200.dp)
            ) {
                UserImage(
                    imageUrl = null,
                    contentDescription = "Foto de $matchName",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                
                // Badge de SuperLike
                if (userId in listOf("2", "4", "7")) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(48.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(CafezinhoSuperLike, CafezinhoMatch)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💙",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Nome e idade
            Text(
                text = matchName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CafezinhoOnSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Informações do match
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CafezinhoSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Informações do Match",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CafezinhoOnSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow("Data do Match", "Hoje às 14:30")
                    InfoRow("Distância", "2 km")
                    InfoRow("Tipo", if (userId in listOf("2", "4", "7")) "Super Like" else "Like")
                    InfoRow("Status", "Online agora")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estatísticas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CafezinhoSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Conversa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CafezinhoOnSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow("Mensagens trocadas", "15")
                    InfoRow("Última mensagem", "Há 2 horas")
                    InfoRow("Tempo de resposta", "~5 minutos")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = CafezinhoOnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = CafezinhoOnSurface
        )
    }
}