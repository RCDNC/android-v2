package com.rcdnc.cafezinho.ui.theme

import androidx.compose.ui.graphics.Color
import com.rcdnc.cafezinho.ui.theme.CafezinhoColors
import com.rcdnc.cafezinho.ui.theme.CafezinhoAlpha

/**
 * Cafezinho Android-specific color definitions
 * Uses shared KMP tokens from CafezinhoColors
 */

// Primary brand colors
val CafezinhoPrimary = Color(CafezinhoColors.PRIMARY)
val CafezinhoOnPrimary = Color(CafezinhoColors.BACKGROUND_LIGHT)

// Secondary brand colors
val CafezinhoSecondary = Color(CafezinhoColors.MATCH_PURPLE)
val CafezinhoOnSecondary = Color(CafezinhoColors.BACKGROUND_LIGHT)

// Background colors
val CafezinhoBackground = Color(CafezinhoColors.BACKGROUND_LIGHT)
val CafezinhoBackgroundDark = Color(CafezinhoColors.BACKGROUND_DARK)

// Surface colors (light theme)
val CafezinhoSurface = Color(CafezinhoColors.SURFACE_50)
val CafezinhoSurfaceVariant = Color(CafezinhoColors.SURFACE_100)
val CafezinhoOnSurface = Color(CafezinhoColors.SURFACE_900)
val CafezinhoOnSurfaceVariant = Color(CafezinhoColors.SURFACE_600)

// Surface colors (dark theme)
val CafezinhoSurfaceDark = Color(CafezinhoColors.SURFACE_900)
val CafezinhoSurfaceVariantDark = Color(CafezinhoColors.SURFACE_800)
val CafezinhoOnSurfaceDark = Color(CafezinhoColors.SURFACE_50)
val CafezinhoOnSurfaceVariantDark = Color(CafezinhoColors.SURFACE_400)

// Semantic colors
val CafezinhoError = Color(CafezinhoColors.ERROR)
val CafezinhoOnError = Color(CafezinhoColors.BACKGROUND_LIGHT)
val CafezinhoSuccess = Color(CafezinhoColors.SUCCESS)
val CafezinhoWarning = Color(CafezinhoColors.WARNING)

// Swipe action colors
val CafezinhoLike = Color(CafezinhoColors.LIKE_GREEN)
val CafezinhoDislike = Color(CafezinhoColors.DISLIKE_RED)
val CafezinhoSuperLike = Color(CafezinhoColors.SUPERLIKE_BLUE)
val CafezinhoMatch = Color(CafezinhoColors.MATCH_PURPLE)

// Boost colors
val CafezinhoBoostStart = Color(CafezinhoColors.BOOST_START)
val CafezinhoBoostEnd = Color(CafezinhoColors.BOOST_END)

// Outline colors
val CafezinhoOutline = Color(CafezinhoColors.SURFACE_300)
val CafezinhoOutlineVariant = Color(CafezinhoColors.SURFACE_200)
val CafezinhoOutlineDark = Color(CafezinhoColors.SURFACE_700)
val CafezinhoOutlineVariantDark = Color(CafezinhoColors.SURFACE_800)