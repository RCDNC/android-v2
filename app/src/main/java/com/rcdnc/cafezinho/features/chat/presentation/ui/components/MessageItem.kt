package com.rcdnc.cafezinho.features.chat.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus
import com.rcdnc.cafezinho.ui.components.UserImage
import com.rcdnc.cafezinho.ui.components.UserImageType
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Message item component for chat interface
 * Based on legacy chat message layouts
 */
@Composable
fun MessageItem(
    message: Message,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true,
    showTimestamp: Boolean = true,
    previousMessage: Message? = null
) {
    val isFirstInGroup = previousMessage?.senderId != message.senderId
    val spacing = if (isFirstInGroup) 12.dp else 4.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = spacing / 2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            // Avatar for received messages
            if (!isFromCurrentUser && showAvatar && isFirstInGroup) {
                UserImage(
                    imageUrl = message.senderAvatar,
                    contentDescription = "Sender avatar",
                    type = UserImageType.AVATAR,
                    size = ComponentSize.SMALL,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else if (!isFromCurrentUser && showAvatar) {
                // Spacer to maintain alignment when no avatar is shown
                Spacer(modifier = Modifier.width(40.dp))
            }
            
            // Message bubble
            MessageBubble(
                message = message,
                isFromCurrentUser = isFromCurrentUser,
                modifier = Modifier.widthIn(max = 280.dp)
            )
            
            // Spacer for sent messages to push them to the right
            if (isFromCurrentUser && showAvatar) {
                Spacer(modifier = Modifier.width(40.dp))
            }
        }
        
        // Timestamp
        if (showTimestamp && isFirstInGroup) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                if (!isFromCurrentUser && showAvatar) {
                    Spacer(modifier = Modifier.width(40.dp))
                }
                
                Text(
                    text = formatMessageTime(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
                )
                
                if (isFromCurrentUser && showAvatar) {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isFromCurrentUser) {
        CafezinhoPrimary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isFromCurrentUser) {
        CafezinhoOnPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val bubbleShape = if (isFromCurrentUser) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    }
    
    Surface(
        modifier = modifier,
        shape = bubbleShape,
        color = bubbleColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            when (message.type) {
                MessageType.TEXT -> {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
                
                MessageType.IMAGE -> {
                    // TODO: Implement image message
                    Text(
                        text = "📷 Imagem",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
                
                MessageType.AUDIO -> {
                    // TODO: Implement audio message
                    Text(
                        text = "🎵 Áudio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
                
                MessageType.FILE -> {
                    // TODO: Implement file message
                    Text(
                        text = "📎 Arquivo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
            
            // Message status (only for sent messages)
            if (isFromCurrentUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatMessageTime(message.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    MessageStatusIcon(
                        status = message.status,
                        tint = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageStatusIcon(
    status: MessageStatus,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val statusText = when (status) {
        MessageStatus.SENDING -> "⏳"
        MessageStatus.SENT -> "✓"
        MessageStatus.DELIVERED -> "✓✓"
        MessageStatus.READ -> "✓✓"
        MessageStatus.FAILED -> "❌"
    }
    
    Text(
        text = statusText,
        style = MaterialTheme.typography.bodySmall,
        color = if (status == MessageStatus.READ) CafezinhoSuccess else tint,
        modifier = modifier
    )
}

private fun formatMessageTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


// Preview functions
@Preview(name = "MessageItem - Conversation")
@Composable
private fun MessageItemConversationPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Received message
            MessageItem(
                message = Message(
                    id = "1",
                    content = "Oi! Tudo bem?",
                    senderId = "other",
                    receiverId = "current",
                    senderAvatar = "https://example.com/avatar.jpg",
                    timestamp = System.currentTimeMillis() - 300000
                ),
                isFromCurrentUser = false
            )
            
            // Another received message (grouped)
            MessageItem(
                message = Message(
                    id = "2",
                    content = "Como foi seu dia?",
                    senderId = "other",
                    receiverId = "current",
                    senderAvatar = "https://example.com/avatar.jpg",
                    timestamp = System.currentTimeMillis() - 290000
                ),
                isFromCurrentUser = false,
                previousMessage = Message(
                    id = "1",
                    content = "Oi! Tudo bem?",
                    senderId = "other",
                    receiverId = "current",
                    timestamp = System.currentTimeMillis() - 300000
                )
            )
            
            // Sent message
            MessageItem(
                message = Message(
                    id = "3",
                    content = "Oi! Estou bem, obrigado! E você?",
                    senderId = "me",
                    receiverId = "other",
                    timestamp = System.currentTimeMillis() - 280000,
                    status = MessageStatus.READ
                ),
                isFromCurrentUser = true
            )
            
            // Another sent message
            MessageItem(
                message = Message(
                    id = "4",
                    content = "Meu dia foi ótimo! Acabei de chegar do trabalho.",
                    senderId = "me",
                    receiverId = "other",
                    timestamp = System.currentTimeMillis() - 270000,
                    status = MessageStatus.DELIVERED
                ),
                isFromCurrentUser = true,
                previousMessage = Message(
                    id = "3",
                    content = "Oi! Estou bem, obrigado! E você?",
                    senderId = "me",
                    receiverId = "other",
                    timestamp = System.currentTimeMillis() - 280000
                )
            )
        }
    }
}

@Preview(name = "MessageItem - Status Types")
@Composable
private fun MessageItemStatusPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MessageStatus.values().forEach { status ->
                MessageItem(
                    message = Message(
                        id = status.name,
                        content = "Mensagem com status: ${status.name}",
                        senderId = "me",
                        receiverId = "other",
                        timestamp = System.currentTimeMillis(),
                        status = status
                    ),
                    isFromCurrentUser = true
                )
            }
        }
    }
}