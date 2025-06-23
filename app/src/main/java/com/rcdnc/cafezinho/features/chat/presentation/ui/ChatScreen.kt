package com.rcdnc.cafezinho.features.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
// MessageInput e MessageItem components existentes
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.theme.*
// import com.rcdnc.cafezinho.ui.components.ComponentSize
import kotlinx.coroutines.launch

/**
 * Tela de conversa individual
 * Migração de activity_chat.xml + ChatActivity.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onUserProfileClick: (Any) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // TODO: Usar ChatViewModel para carregar dados do userId
    val otherUserName = "Usuário $userId"
    val otherUserAvatar: String? = null
    val isOnline = false
    val messages = remember { emptyList<Message>() }
    val currentUserId = "me"
    val isTyping = false
    
    val onMenuClick = { onUserProfileClick(userId) }
    val onSendMessage: (String) -> Unit = { /* TODO: Implementar envio */ }
    val onSendVoiceMessage: (String) -> Unit = { /* TODO: Implementar áudio */ }
    val onSendImage: () -> Unit = { /* TODO: Implementar imagem */ }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Auto-scroll para última mensagem quando nova mensagem é adicionada
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CafezinhoBackground)
    ) {
        // Top Bar personalizada
        ChatTopBar(
            userName = otherUserName,
            userAvatar = otherUserAvatar,
            isOnline = isOnline,
            isTyping = isTyping,
            onBackClick = onBackClick,
            onMenuClick = onMenuClick
        )
        
        // Lista de mensagens
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = false
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                // TODO: Usar MessageItem component quando disponível
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.senderId == currentUserId) CafezinhoPrimary else CafezinhoSurface
                    )
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(12.dp),
                        color = if (message.senderId == currentUserId) CafezinhoOnPrimary else CafezinhoOnSurface
                    )
                }
            }
            
            // Indicador de digitação
            if (isTyping) {
                item {
                    TypingIndicator(
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        
        // TODO: Usar MessageInput component quando disponível
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var messageText by remember { mutableStateOf("") }
            
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Digite uma mensagem...") },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                },
                enabled = messageText.isNotBlank()
            ) {
                Text("Enviar")
            }
        }
    }
}

/**
 * TopBar personalizada do Chat
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    userName: String,
    userAvatar: String?,
    isOnline: Boolean,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar do usuário
                Box {
                    UserImage(
                        imageUrl = userAvatar,
                        contentDescription = "Avatar de $userName",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    
                    // Indicador online
                    if (isOnline) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = androidx.compose.ui.graphics.Color.Green,
                                    shape = CircleShape
                                )
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
                
                // Nome e status
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = CafezinhoOnSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = when {
                            isTyping -> "digitando..."
                            isOnline -> "online"
                            else -> "visto por último há pouco"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isTyping) CafezinhoPrimary else CafezinhoOnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = CafezinhoOnSurface
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Mais opções",
                    tint = CafezinhoOnSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CafezinhoSurface
        )
    )
}

/**
 * Indicador de digitação
 */
@Composable
private fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Avatar placeholder para alinhamento
        Spacer(modifier = Modifier.size(32.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = CafezinhoSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    // Animação simplificada do indicador de digitação
                    val alpha = if ((System.currentTimeMillis() / 500) % 3 == index.toLong()) 1f else 0.3f
                    
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = CafezinhoOnSurfaceVariant.copy(alpha = alpha),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

// Helper functions
private fun shouldShowAvatar(
    message: Message,
    messages: List<Message>,
    currentUserId: String
): Boolean {
    val messageIndex = messages.indexOf(message)
    if (messageIndex == -1 || message.senderId == currentUserId) return false
    
    // Mostra avatar se for a última mensagem do outro usuário em sequência
    val nextMessage = messages.getOrNull(messageIndex + 1)
    return nextMessage?.senderId != message.senderId || nextMessage == null
}

private fun shouldShowTimestamp(
    message: Message,
    messages: List<Message>
): Boolean {
    val messageIndex = messages.indexOf(message)
    if (messageIndex == -1) return true
    
    // Mostra timestamp se passou mais de 5 minutos da mensagem anterior
    val previousMessage = messages.getOrNull(messageIndex - 1)
    return if (previousMessage != null) {
        message.timestamp - previousMessage.timestamp > 5 * 60 * 1000
    } else {
        true
    }
}

// Preview functions
@Preview(name = "ChatScreen - With Messages")
@Composable
private fun ChatScreenPreview() {
    CafezinhoTheme {
        ChatScreen(
            userId = "ana_silva",
            onBackClick = { },
            onUserProfileClick = { }
        )
    }
}

@Preview(name = "ChatScreen - Typing")
@Composable
private fun ChatScreenTypingPreview() {
    CafezinhoTheme {
        ChatScreen(
            userId = "ana_silva",
            onBackClick = { },
            onUserProfileClick = { }
        )
    }
}

@Preview(name = "ChatTopBar")
@Composable
private fun ChatTopBarPreview() {
    CafezinhoTheme {
        ChatTopBar(
            userName = "Ana Silva",
            userAvatar = "https://example.com/avatar.jpg",
            isOnline = true,
            isTyping = false,
            onBackClick = { },
            onMenuClick = { }
        )
    }
}

// Sample data for previews
private val sampleMessages = listOf(
    Message(
        id = "1",
        senderId = "other",
        receiverId = "me",
        content = "Oi! Como você está?",
        timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
        type = MessageType.TEXT,
        status = MessageStatus.READ
    ),
    Message(
        id = "2",
        senderId = "me",
        receiverId = "other",
        content = "Oi! Estou bem, obrigado! E você?",
        timestamp = System.currentTimeMillis() - 8 * 60 * 1000,
        type = MessageType.TEXT,
        status = MessageStatus.READ
    ),
    Message(
        id = "3",
        senderId = "other",
        receiverId = "me",
        content = "Também estou bem! Que bom que você apareceu aqui no app 😊",
        timestamp = System.currentTimeMillis() - 6 * 60 * 1000,
        type = MessageType.TEXT,
        status = MessageStatus.READ
    ),
    Message(
        id = "4",
        senderId = "me",
        receiverId = "other",
        content = "Hehe também gostei do seu perfil! Você curte que tipo de música?",
        timestamp = System.currentTimeMillis() - 2 * 60 * 1000,
        type = MessageType.TEXT,
        status = MessageStatus.DELIVERED
    )
)