package com.rcdnc.cafezinho.features.notifications.presentation.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.features.notifications.presentation.viewmodel.NotificationPermissionViewModel
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

/**
 * Tela para solicitar permissão de notificações
 * Mostrada na primeira abertura do app (Android 13+)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    viewModel: NotificationPermissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Launcher para solicitar permissão
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
            onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
    }
    
    CafezinhoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                
                // Ícone
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(60.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Título
                Text(
                    text = "Receba Notificações",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Descrição
                Text(
                    text = "Não perca nenhum match ou mensagem! " +
                          "Mantenha-se conectado com suas conversas.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Lista de benefícios
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NotificationBenefitItem(
                        title = "🎉 Novos Matches",
                        description = "Saiba imediatamente quando alguém curtir você de volta"
                    )
                    
                    NotificationBenefitItem(
                        title = "💬 Mensagens",
                        description = "Nunca perca uma conversa interessante"
                    )
                    
                    NotificationBenefitItem(
                        title = "❤️ Curtidas",
                        description = "Descubra quem te curtiu e retribua o interesse"
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Botões
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                // Android < 13, notificações habilitadas por padrão
                                viewModel.onPermissionGranted()
                                onPermissionGranted()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Permitir Notificações",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    TextButton(
                        onClick = {
                            viewModel.onPermissionDenied()
                            onPermissionDenied()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = "Agora não",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Nota de privacidade
                Text(
                    text = "Você pode alterar essas configurações a qualquer momento " +
                          "nas configurações do app.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationBenefitItem(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(140.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}