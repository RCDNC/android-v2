package com.rcdnc.cafezinho.features.chat.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.features.chat.domain.model.ChatConversation
import com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.features.chat.presentation.viewmodel.ChatViewModel
import com.rcdnc.cafezinho.features.chat.presentation.viewmodel.ChatIntent
import com.rcdnc.cafezinho.core.auth.AuthManager
import javax.inject.Inject

/**
 * Tela de lista de conversas do Chat
 * Integrada com ChatViewModel e dados demo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onConversationClick: (ChatConversation) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // Observar conversas do ViewModel
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    
    // Dados demo para usuário demo
    val demoConversations = remember {
        listOf(
            ChatConversation(
                id = "demo-1",
                otherUserId = "2",
                otherUserName = "Maria Silva",
                otherUserAvatar = null,
                lastMessage = com.rcdnc.cafezinho.features.chat.domain.model.Message(
                    id = "msg-1",
                    senderId = "2",
                    receiverId = "1",
                    content = "Oi! Como você está?",
                    timestamp = System.currentTimeMillis() - 3600000,
                    type = MessageType.TEXT,
                    status = MessageStatus.DELIVERED
                ),
                lastMessageTimestamp = System.currentTimeMillis() - 3600000, // 1 hora atrás
                hasUnreadMessages = true,
                unreadCount = 2,
                isYourTurn = false,
                isPremium = false,
                isOnline = true,
                isMatch = true
            ),
            ChatConversation(
                id = "demo-2",
                otherUserId = "3",
                otherUserName = "João Santos",
                otherUserAvatar = null,
                lastMessage = com.rcdnc.cafezinho.features.chat.domain.model.Message(
                    id = "msg-2",
                    senderId = "1",
                    receiverId = "3",
                    content = "Adorei te conhecer! 😊",
                    timestamp = System.currentTimeMillis() - 7200000,
                    type = MessageType.TEXT,
                    status = MessageStatus.READ
                ),
                lastMessageTimestamp = System.currentTimeMillis() - 7200000, // 2 horas atrás
                hasUnreadMessages = false,
                unreadCount = 0,
                isYourTurn = true,
                isPremium = true,
                isOnline = false,
                isMatch = true
            ),
            ChatConversation(
                id = "demo-3",
                otherUserId = "4",
                otherUserName = "Ana Costa",
                otherUserAvatar = null,
                lastMessage = com.rcdnc.cafezinho.features.chat.domain.model.Message(
                    id = "msg-3",
                    senderId = "4",
                    receiverId = "1",
                    content = "Que coincidência nos encontrarmos aqui!",
                    timestamp = System.currentTimeMillis() - 86400000,
                    type = MessageType.TEXT,
                    status = MessageStatus.SENT
                ),
                lastMessageTimestamp = System.currentTimeMillis() - 86400000, // 1 dia atrás
                hasUnreadMessages = false,
                unreadCount = 0,
                isYourTurn = false,
                isPremium = false,
                isOnline = true,
                isMatch = true
            )
        )
    }
    
    // Carregar conversas ao inicializar
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ChatIntent.LoadConversations)
    }
    
    // Usar dados demo se lista estiver vazia (para usuário demo)
    val displayConversations = if (conversations.isEmpty()) demoConversations else conversations
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (displayConversations.isEmpty()) {
            // Empty state
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "💬 Suas Conversas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Quando você fizer match com alguém, as conversas aparecerão aqui!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "✅ Chat + API Laravel integrados!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            // Lista de conversas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(displayConversations) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = conversation.otherUserName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (conversation.lastMessage != null) {
                        Text(
                            text = conversation.lastMessage.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Status online
                    if (conversation.isOnline) {
                        Text(
                            text = "🟢 Online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Badge de mensagens não lidas
                    if (conversation.hasUnreadMessages && conversation.unreadCount > 0) {
                        Badge {
                            Text(
                                text = conversation.unreadCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            
            // Indicador de premium e match
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (conversation.isPremium) {
                    Text(
                        text = "⭐ Premium",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                if (conversation.isMatch) {
                    Text(
                        text = "💕 Match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                if (conversation.isYourTurn) {
                    Text(
                        text = "💬 Sua vez",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}