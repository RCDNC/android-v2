package com.rcdnc.cafezinho.features.auth.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

/**
 * Custom OTP input field with individual digit boxes
 * Provides a clean, modern interface for entering verification codes
 */
@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
    // Ensure value doesn't exceed length and contains only digits
    val sanitizedValue = value.filter { it.isDigit() }.take(length)
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(length) { index ->
                OtpDigitBox(
                    digit = sanitizedValue.getOrNull(index)?.toString() ?: "",
                    isFocused = sanitizedValue.length == index,
                    isError = isError,
                    enabled = enabled
                )
            }
        }
        
        // Hidden text field for input handling
        BasicTextField(
            value = TextFieldValue(
                text = sanitizedValue,
                selection = TextRange(sanitizedValue.length)
            ),
            onValueChange = { textFieldValue ->
                val newValue = textFieldValue.text.filter { it.isDigit() }.take(length)
                onValueChange(newValue)
                
                // Auto-dismiss keyboard when complete
                if (newValue.length == length) {
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier
                .size(0.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            enabled = enabled
        )
    }
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    isError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        digit.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }
    
    val backgroundColor = when {
        !enabled -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        isFocused -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            color = when {
                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                isError -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        
        // Cursor indicator when focused and empty
        if (isFocused && digit.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Empty state
            OtpInputField(
                value = "",
                onValueChange = {},
                length = 6
            )
            
            // Partial input
            OtpInputField(
                value = "123",
                onValueChange = {},
                length = 6
            )
            
            // Complete input
            OtpInputField(
                value = "123456",
                onValueChange = {},
                length = 6
            )
            
            // Error state
            OtpInputField(
                value = "123456",
                onValueChange = {},
                length = 6,
                isError = true
            )
            
            // Disabled state
            OtpInputField(
                value = "123",
                onValueChange = {},
                length = 6,
                enabled = false
            )
        }
    }
}