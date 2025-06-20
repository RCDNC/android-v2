package com.rcdnc.cafezinho.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.theme.CafezinhoCornerRadius

/**
 * Cafezinho Shape system using shared KMP corner radius tokens
 * Based on legacy corner radius patterns: 8dp, 16dp, 150dp (pill)
 */

val CafezinhoShapes = Shapes(
    extraSmall = RoundedCornerShape(CafezinhoCornerRadius.XS.dp),      // 4dp - chips, badges
    small = RoundedCornerShape(CafezinhoCornerRadius.SMALL.dp),        // 8dp - input fields, small cards
    medium = RoundedCornerShape(CafezinhoCornerRadius.MEDIUM.dp),      // 16dp - cards, dialogs
    large = RoundedCornerShape(CafezinhoCornerRadius.LARGE.dp),        // 24dp - hero cards, containers
    extraLarge = RoundedCornerShape(CafezinhoCornerRadius.XL.dp)       // 32dp - special containers
)

/**
 * Component-specific shapes for consistent usage
 */
object CafezinhoComponentShapes {
    val Button = RoundedCornerShape(CafezinhoCornerRadius.Component.BUTTON.dp)          // Pill-shaped buttons
    val Card = RoundedCornerShape(CafezinhoCornerRadius.Component.CARD.dp)              // Standard card rounding
    val Dialog = RoundedCornerShape(CafezinhoCornerRadius.Component.DIALOG.dp)          // Dialog rounding
    val InputField = RoundedCornerShape(CafezinhoCornerRadius.Component.INPUT_FIELD.dp) // Input field rounding
    val Chip = RoundedCornerShape(CafezinhoCornerRadius.Component.CHIP.dp)              // Pill-shaped chips
    val Image = RoundedCornerShape(CafezinhoCornerRadius.Component.IMAGE.dp)            // Standard image rounding
    val BottomSheet = RoundedCornerShape(
        topStart = CafezinhoCornerRadius.Component.BOTTOM_SHEET.dp,
        topEnd = CafezinhoCornerRadius.Component.BOTTOM_SHEET.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
}