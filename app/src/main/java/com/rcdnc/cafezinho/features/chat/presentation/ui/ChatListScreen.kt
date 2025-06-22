package com.rcdnc.cafezinho.features.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.features.chat.domain.model.ChatConversation
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*
import com.rcdnc.cafezinho.ui.components.ComponentSize
import java.text.SimpleDateFormat
import java.util.*

/**
 * Lista de conversas do Chat
 * Migração de fragment_inbox.xml + InboxAdapter.java
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    conversations: List<ChatConversation>,
    onConversationClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CafezinhoBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Conversas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CafezinhoOnSurface
                )
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar conversas",
                        tint = CafezinhoOnSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CafezinhoBackground
            )
        )
        
        if (conversations.isEmpty()) {
            EmptyConversationsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = conversations,
                    key = { it.id }
                ) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Item individual da lista de conversas
 * Migração de item_inbox_list.xml
 */
@Composable
private fun ConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CafezinhoSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar do usuário
            Box {
                UserImage(
                    imageUrl = conversation.otherUserAvatar,
                    size = ComponentSize.ExtraLarge,
                    contentDescription = "Avatar de ${conversation.otherUserName}",
                    modifier = Modifier.clip(CircleShape)
                )
                
                // Indicador de usuário premium (coração dourado)
                if (conversation.isPremium) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = CafezinhoMatch,
                                shape = CircleShape
                            )
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "♥",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                
                // Indicador online/offline
                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = Color.Green,
                                shape = CircleShape
                            )
                            .align(Alignment.TopEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Conteúdo da conversa
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nome do usuário
                    Text(
                        text = conversation.otherUserName,
                        style = MaterialTheme.typography.titleMedium,
                        color = CafezinhoOnSurface,
                        fontWeight = if (conversation.hasUnreadMessages) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Timestamp da última mensagem
                    Text(
                        text = formatTimestamp(conversation.lastMessageTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (conversation.hasUnreadMessages) CafezinhoPrimary else CafezinhoOnSurfaceVariant,
                        fontWeight = if (conversation.hasUnreadMessages) FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Última mensagem
                    Text(
                        text = formatLastMessage(conversation.lastMessage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (conversation.hasUnreadMessages) CafezinhoOnSurface else CafezinhoOnSurfaceVariant,
                        fontWeight = if (conversation.hasUnreadMessages) FontWeight.Medium else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Indicadores
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Badge de mensagens não lidas
                        if (conversation.unreadCount > 0) {
                            Badge(
                                modifier = Modifier.size(20.dp),
                                containerColor = CafezinhoPrimary
                            ) {
                                Text(
                                    text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                                    color = CafezinhoOnPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        
                        // Indicador "Sua vez"
                        if (conversation.isYourTurn) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CafezinhoSuperLike
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Sua vez",
                                    color = CafezinhoOnPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Estado vazio quando não há conversas
 */
@Composable
private fun EmptyConversationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💬",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Nenhuma conversa ainda",
            style = MaterialTheme.typography.titleLarge,
            color = CafezinhoOnBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Quando você der match com alguém,\nsuas conversas aparecerão aqui",
            style = MaterialTheme.typography.bodyMedium,
            color = CafezinhoOnSurfaceVariant
        )
    }
}

// Helper functions
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60 * 1000 -> "Agora"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h"
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            val days = diff / (24 * 60 * 60 * 1000)
            "${days}d"
        }
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatLastMessage(message: Message?): String {
    return when {
        message == null -> "Iniciar conversa"
        message.type == MessageType.IMAGE -> "📷 Foto"
        message.type == MessageType.AUDIO -> "🎤 Áudio"
        message.type == MessageType.FILE -> "📎 Arquivo"
        else -> message.content
    }
}

// Preview functions
@Preview(name = "ChatListScreen - With Conversations")
@Composable
private fun ChatListScreenPreview() {
    CafezinhoTheme {
        ChatListScreen(
            conversations = sampleConversations,
            onConversationClick = { },
            onSearchClick = { }
        )
    }
}

@Preview(name = "ChatListScreen - Empty")
@Composable
private fun ChatListScreenEmptyPreview() {
    CafezinhoTheme {
        ChatListScreen(
            conversations = emptyList(),
            onConversationClick = { },
            onSearchClick = { }
        )
    }
}

@Preview(name = "ConversationItem")
@Composable
private fun ConversationItemPreview() {
    CafezinhoTheme {
        ConversationItem(
            conversation = sampleConversations.first(),
            onClick = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Sample data for previews
private val sampleConversations = listOf(
    ChatConversation(
        id = "1",
        otherUserId = "user1",
        otherUserName = "Ana Silva",
        otherUserAvatar = "https://example.com/avatar1.jpg",
        lastMessage = Message(
            id = "msg1",
            senderId = "user1",
            receiverId = "me",
            content = "Oi! Como você está?",
            timestamp = System.currentTimeMillis() - 5 * 60 * 1000
        ),
        lastMessageTimestamp = System.currentTimeMillis() - 5 * 60 * 1000,
        hasUnreadMessages = true,
        unreadCount = 2,
        isYourTurn = false,
        isPremium = true,
        isOnline = true
    ),
    ChatConversation(
        id = "2",
        otherUserId = "user2",
        otherUserName = "Bruno Costa",
        otherUserAvatar = "https://example.com/avatar2.jpg",
        lastMessage = Message(
            id = "msg2",
            senderId = "me",
            receiverId = "user2",
            content = "Combinado! Te vejo lá 😊",
            timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000
        ),
        lastMessageTimestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
        hasUnreadMessages = false,
        unreadCount = 0,
        isYourTurn = true,
        isPremium = false,
        isOnline = false
    ),
    ChatConversation(
        id = "3",
        otherUserId = "user3",
        otherUserName = "Carla Mendes",
        otherUserAvatar = "https://example.com/avatar3.jpg",
        lastMessage = null,
        lastMessageTimestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
        hasUnreadMessages = false,
        unreadCount = 0,
        isYourTurn = false,
        isPremium = false,
        isOnline = true
    )
)