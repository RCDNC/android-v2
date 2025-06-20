package com.rcdnc.cafezinho.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rcdnc.cafezinho.ui.theme.CafezinhoTypography

/**
 * Cafezinho Typography system using shared KMP tokens
 * Based on Urbanist font family and legacy typography scale
 */

// Convert font weight integers to FontWeight objects
private fun Int.toFontWeight(): FontWeight = when (this) {
    CafezinhoTypography.WEIGHT_REGULAR -> FontWeight.Normal
    CafezinhoTypography.WEIGHT_SEMIBOLD -> FontWeight.SemiBold
    CafezinhoTypography.WEIGHT_BOLD -> FontWeight.Bold
    else -> FontWeight.Normal
}

val CafezinhoTypographySystem = Typography(
    // Headlines
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default, // TODO: Replace with Urbanist font
        fontWeight = CafezinhoTypography.Scale.HEADLINE_LARGE.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.HEADLINE_LARGE.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.HEADLINE_LARGE.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.HEADLINE_LARGE.letterSpacing.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.HEADLINE_MEDIUM.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.HEADLINE_MEDIUM.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.HEADLINE_MEDIUM.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.HEADLINE_MEDIUM.letterSpacing.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.HEADLINE_SMALL.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.HEADLINE_SMALL.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.HEADLINE_SMALL.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.HEADLINE_SMALL.letterSpacing.sp
    ),
    
    // Titles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.TITLE_LARGE.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.TITLE_LARGE.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.TITLE_LARGE.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.TITLE_LARGE.letterSpacing.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.TITLE_MEDIUM.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.TITLE_MEDIUM.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.TITLE_MEDIUM.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.TITLE_MEDIUM.letterSpacing.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.TITLE_SMALL.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.TITLE_SMALL.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.TITLE_SMALL.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.TITLE_SMALL.letterSpacing.sp
    ),
    
    // Body text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.BODY_LARGE.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.BODY_LARGE.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.BODY_LARGE.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.BODY_LARGE.letterSpacing.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.BODY_MEDIUM.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.BODY_MEDIUM.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.BODY_MEDIUM.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.BODY_MEDIUM.letterSpacing.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.BODY_SMALL.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.BODY_SMALL.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.BODY_SMALL.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.BODY_SMALL.letterSpacing.sp
    ),
    
    // Labels
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.LABEL_LARGE.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.LABEL_LARGE.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.LABEL_LARGE.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.LABEL_LARGE.letterSpacing.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.LABEL_MEDIUM.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.LABEL_MEDIUM.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.LABEL_MEDIUM.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.LABEL_MEDIUM.letterSpacing.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = CafezinhoTypography.Scale.LABEL_SMALL.fontWeight.toFontWeight(),
        fontSize = CafezinhoTypography.Scale.LABEL_SMALL.fontSize.sp,
        lineHeight = CafezinhoTypography.Scale.LABEL_SMALL.lineHeight.sp,
        letterSpacing = CafezinhoTypography.Scale.LABEL_SMALL.letterSpacing.sp
    )
)

// Legacy alias for backward compatibility
val Typography = CafezinhoTypographySystem