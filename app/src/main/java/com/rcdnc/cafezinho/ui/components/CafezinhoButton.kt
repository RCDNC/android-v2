package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.ui.component.ButtonVariant
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.component.ComponentSizeValues
import com.rcdnc.cafezinho.ui.component.LoadingState
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Cafezinho branded button component
 * Supports all button variants and sizes from shared tokens
 * Based on legacy button patterns with pill-shaped design
 */
@Composable
fun CafezinhoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ComponentSize = ComponentSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val buttonHeight = when (size) {
        ComponentSize.SMALL -> ComponentSizeValues.BUTTON_HEIGHT_SMALL.dp
        ComponentSize.MEDIUM -> ComponentSizeValues.BUTTON_HEIGHT_MEDIUM.dp
        ComponentSize.LARGE -> ComponentSizeValues.BUTTON_HEIGHT_LARGE.dp
    }
    
    val horizontalPadding = when (size) {
        ComponentSize.SMALL -> ComponentSizeValues.PADDING_SMALL.dp
        ComponentSize.MEDIUM -> ComponentSizeValues.PADDING_MEDIUM.dp
        ComponentSize.LARGE -> ComponentSizeValues.PADDING_LARGE.dp
    }
    
    val fontSize = when (size) {
        ComponentSize.SMALL -> 12.sp
        ComponentSize.MEDIUM -> 14.sp
        ComponentSize.LARGE -> 16.sp
    }

    when (variant) {
        ButtonVariant.PRIMARY -> PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            size = size,
            enabled = enabled,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            buttonHeight = buttonHeight,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
        
        ButtonVariant.SECONDARY -> SecondaryButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            size = size,
            enabled = enabled,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            buttonHeight = buttonHeight,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
        
        ButtonVariant.DANGER -> DangerButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            size = size,
            enabled = enabled,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            buttonHeight = buttonHeight,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
        
        ButtonVariant.SUCCESS -> SuccessButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            size = size,
            enabled = enabled,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            buttonHeight = buttonHeight,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
        
        ButtonVariant.BOOST -> BoostButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            size = size,
            enabled = enabled,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            buttonHeight = buttonHeight,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: ComponentSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    buttonHeight: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        enabled = enabled && !loading,
        shape = CafezinhoComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = CafezinhoPrimary,
            contentColor = CafezinhoOnPrimary,
            disabledContainerColor = CafezinhoPrimary.copy(alpha = 0.38f),
            disabledContentColor = CafezinhoOnPrimary.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: ComponentSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    buttonHeight: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        enabled = enabled && !loading,
        shape = CafezinhoComponentShapes.Button,
        border = BorderStroke(1.dp, CafezinhoPrimary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = CafezinhoPrimary,
            disabledContentColor = CafezinhoPrimary.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: ComponentSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    buttonHeight: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        enabled = enabled && !loading,
        shape = CafezinhoComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = CafezinhoError,
            contentColor = CafezinhoOnError,
            disabledContainerColor = CafezinhoError.copy(alpha = 0.38f),
            disabledContentColor = CafezinhoOnError.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun SuccessButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: ComponentSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    buttonHeight: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        enabled = enabled && !loading,
        shape = CafezinhoComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = CafezinhoSuccess,
            contentColor = CafezinhoOnPrimary,
            disabledContainerColor = CafezinhoSuccess.copy(alpha = 0.38f),
            disabledContentColor = CafezinhoOnPrimary.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun BoostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: ComponentSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    buttonHeight: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(CafezinhoBoostStart, CafezinhoBoostEnd)
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(buttonHeight)
            .background(
                brush = gradientBrush,
                shape = CafezinhoComponentShapes.Button
            ),
        enabled = enabled && !loading,
        shape = CafezinhoComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = CafezinhoOnPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = CafezinhoOnPrimary.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            horizontalPadding = horizontalPadding,
            fontSize = fontSize
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = horizontalPadding)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold
        )
        
        if (!loading && trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
        }
    }
}