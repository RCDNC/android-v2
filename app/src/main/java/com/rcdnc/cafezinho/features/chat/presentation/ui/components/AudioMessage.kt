package com.rcdnc.cafezinho.features.chat.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

/**
 * Audio message component for chat interface
 * Based on legacy audio message layouts
 */
@Composable
fun AudioMessage(
    isPlaying: Boolean,
    duration: String,
    currentPosition: String = "00:00",
    progress: Float = 0f,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    isFromCurrentUser: Boolean = false,
    waveformData: List<Float> = generateSampleWaveform()
) {
    val backgroundColor = if (isFromCurrentUser) {
        CafezinhoPrimary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (isFromCurrentUser) {
        CafezinhoOnPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier.widthIn(min = 200.dp, max = 280.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Play/Pause button
            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isFromCurrentUser) {
                        CafezinhoOnPrimary.copy(alpha = 0.2f)
                    } else {
                        CafezinhoPrimary.copy(alpha = 0.1f)
                    },
                    contentColor = contentColor
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Waveform and time info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Waveform visualization
                AudioWaveform(
                    waveformData = waveformData,
                    progress = progress,
                    color = contentColor,
                    progressColor = if (isFromCurrentUser) CafezinhoOnPrimary else CafezinhoPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                )
                
                // Time display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentPosition,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioWaveform(
    waveformData: List<Float>,
    progress: Float,
    color: Color,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / waveformData.size
        val progressWidth = canvasWidth * progress
        
        waveformData.forEachIndexed { index, amplitude ->
            val barHeight = canvasHeight * amplitude
            val x = index * barWidth + barWidth / 2
            val y1 = (canvasHeight - barHeight) / 2
            val y2 = y1 + barHeight
            
            val barColor = if (x <= progressWidth) progressColor else color.copy(alpha = 0.5f)
            
            drawLine(
                color = barColor,
                start = Offset(x, y1),
                end = Offset(x, y2),
                strokeWidth = barWidth * 0.8f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Compact audio message for inline display
 */
@Composable
fun CompactAudioMessage(
    isPlaying: Boolean,
    duration: String,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier
            .background(
                color = tint.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Text(
            text = "🎵 Áudio",
            style = MaterialTheme.typography.bodySmall,
            color = tint
        )
        
        Text(
            text = duration,
            style = MaterialTheme.typography.bodySmall,
            color = tint.copy(alpha = 0.7f)
        )
    }
}

/**
 * Audio recording indicator
 */
@Composable
fun AudioRecordingIndicator(
    duration: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    Row(
        modifier = modifier
            .background(
                color = if (isActive) Color.Red.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Recording dot with animation
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    color = if (isActive) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
        )
        
        Text(
            text = if (isActive) "Gravando..." else "Gravação pausada",
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = duration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper function to generate sample waveform data
private fun generateSampleWaveform(size: Int = 40): List<Float> {
    return (0 until size).map {
        // Generate waveform that looks like speech
        val baseWave = sin(it * 0.3) * 0.5f + 0.5f
        val noise = Random.nextFloat() * 0.3f
        val envelope = if (it < size * 0.1f || it > size * 0.9f) 0.2f else 1.0f
        ((baseWave + noise) * envelope).coerceIn(0.1f, 1.0f)
    }
}

// Preview functions
@Preview(name = "AudioMessage - Sent")
@Composable
private fun AudioMessageSentPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioMessage(
                isPlaying = false,
                duration = "01:23",
                onPlayPause = { },
                isFromCurrentUser = true
            )
            
            AudioMessage(
                isPlaying = true,
                duration = "01:23",
                currentPosition = "00:45",
                progress = 0.6f,
                onPlayPause = { },
                isFromCurrentUser = true
            )
        }
    }
}

@Preview(name = "AudioMessage - Received")
@Composable
private fun AudioMessageReceivedPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioMessage(
                isPlaying = false,
                duration = "00:42",
                onPlayPause = { },
                isFromCurrentUser = false
            )
            
            AudioMessage(
                isPlaying = true,
                duration = "00:42",
                currentPosition = "00:15",
                progress = 0.3f,
                onPlayPause = { },
                isFromCurrentUser = false
            )
        }
    }
}

@Preview(name = "CompactAudioMessage")
@Composable
private fun CompactAudioMessagePreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactAudioMessage(
                isPlaying = false,
                duration = "01:23",
                onPlayPause = { }
            )
            
            CompactAudioMessage(
                isPlaying = true,
                duration = "01:23",
                onPlayPause = { }
            )
        }
    }
}

@Preview(name = "AudioRecordingIndicator")
@Composable
private fun AudioRecordingIndicatorPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioRecordingIndicator(
                duration = "00:15",
                isActive = true
            )
            
            AudioRecordingIndicator(
                duration = "00:45",
                isActive = false
            )
        }
    }
}