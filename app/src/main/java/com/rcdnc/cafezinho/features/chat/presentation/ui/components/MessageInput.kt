package com.rcdnc.cafezinho.features.chat.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Message input component for chat interface
 * Based on legacy message input layouts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Digite uma mensagem...",
    enabled: Boolean = true,
    showAttachButton: Boolean = true,
    onAttachClick: (() -> Unit)? = null,
    maxLines: Int = 5
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Attach button
            if (showAttachButton && onAttachClick != null) {
                IconButton(
                    onClick = onAttachClick,
                    enabled = enabled,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Anexar arquivo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Text input
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                enabled = enabled,
                maxLines = maxLines,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CafezinhoPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (value.isNotBlank()) {
                            onSendMessage()
                        }
                    }
                )
            )
            
            // Send button
            SendButton(
                onClick = onSendMessage,
                enabled = enabled && value.isNotBlank(),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun SendButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (enabled) CafezinhoPrimary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled) CafezinhoOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Enviar mensagem",
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Compact message input for smaller contexts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactMessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Mensagem...",
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            enabled = enabled,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (value.isNotBlank()) {
                        onSendMessage()
                    }
                }
            )
        )
        
        SendButton(
            onClick = onSendMessage,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Voice message input with record functionality
 */
@Composable
fun VoiceMessageInput(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier,
    recordingDuration: String = "00:00"
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isRecording) CafezinhoPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isRecording) {
                // Recording state
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Recording indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = Color.Red,
                                shape = CircleShape
                            )
                    )
                    
                    Text(
                        text = "Gravando... $recordingDuration",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CafezinhoPrimary
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel button
                    OutlinedButton(
                        onClick = onCancelRecording,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar")
                    }
                    
                    // Stop button
                    FilledIconButton(
                        onClick = onStopRecording,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = CafezinhoPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Parar gravação",
                            tint = CafezinhoOnPrimary
                        )
                    }
                }
            } else {
                // Default state
                Text(
                    text = "Toque para gravar mensagem de voz",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                FilledIconButton(
                    onClick = onStartRecording,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = CafezinhoPrimary
                    )
                ) {
                    Text(
                        text = "🎤",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Preview functions
@Preview(name = "MessageInput - Default")
@Composable
private fun MessageInputPreview() {
    CafezinhoTheme {
        var text by remember { mutableStateOf("") }
        
        MessageInput(
            value = text,
            onValueChange = { text = it },
            onSendMessage = { text = "" },
            onAttachClick = { }
        )
    }
}

@Preview(name = "MessageInput - With Text")
@Composable
private fun MessageInputWithTextPreview() {
    CafezinhoTheme {
        var text by remember { mutableStateOf("Esta é uma mensagem de exemplo que o usuário está digitando...") }
        
        MessageInput(
            value = text,
            onValueChange = { text = it },
            onSendMessage = { text = "" },
            onAttachClick = { }
        )
    }
}

@Preview(name = "CompactMessageInput")
@Composable
private fun CompactMessageInputPreview() {
    CafezinhoTheme {
        var text by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactMessageInput(
                value = text,
                onValueChange = { text = it },
                onSendMessage = { text = "" }
            )
            
            CompactMessageInput(
                value = "Mensagem digitada",
                onValueChange = { },
                onSendMessage = { }
            )
        }
    }
}

@Preview(name = "VoiceMessageInput")
@Composable
private fun VoiceMessageInputPreview() {
    CafezinhoTheme {
        var isRecording by remember { mutableStateOf(false) }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Estado normal:")
            VoiceMessageInput(
                isRecording = false,
                onStartRecording = { isRecording = true },
                onStopRecording = { isRecording = false },
                onCancelRecording = { isRecording = false }
            )
            
            Text("Estado gravando:")
            VoiceMessageInput(
                isRecording = true,
                onStartRecording = { },
                onStopRecording = { isRecording = false },
                onCancelRecording = { isRecording = false },
                recordingDuration = "01:23"
            )
        }
    }
}