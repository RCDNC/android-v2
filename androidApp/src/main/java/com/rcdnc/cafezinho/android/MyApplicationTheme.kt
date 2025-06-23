package com.rcdnc.cafezinho.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF0087F9),
            background = Color(0xFF181A20),
            surface = Color(0xFF1F222A),
            onBackground = Color(0xFFFAFAFA),
            onSurface = Color(0xFFFAF9F6),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF0087F9),
            background = Color(0xFFFAFAFA),
            surface = Color(0xFFFAF9F6),
            onBackground = Color(0xFF181A20),
            onSurface = Color(0xFF1F222A),
        )
    }

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

object CustomAppColors {

    // Consumíveis
    val Boost = Color(0xFF9D28AC)
    val Like = Color(0xFF60A74B)
    val Superlike = Color(0xFF0087F9)

    // Planos
    val Plus = Color(0xFF0087F9)
    val Gold = Color(0xFFFF981F)
    val Platinum = Color(0xFF20272F)

    // Ações
    val Success = Color(0xFF60A74B)
    val Error = Color(0xFFF75555)
    val Attention = Color(0xFFFFBE00)
}