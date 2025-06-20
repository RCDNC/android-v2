package com.rcdnc.cafezinho.ui.theme

/**
 * Corner radius tokens for consistent rounding across platforms
 * Based on legacy corner radius patterns and Material Design guidelines
 */
object CafezinhoCornerRadius {
    /**
     * No rounding - sharp corners
     * Use for: geometric elements, certain cards
     */
    const val NONE = 0f         // 0dp
    
    /**
     * Extra small rounding - subtle softening
     * Use for: small chips, badges
     */
    const val XS = 4f           // 4dp
    
    /**
     * Small rounding - gentle corners
     * Use for: input fields, small cards
     * Legacy: subtle rounding in some components
     */
    const val SMALL = 8f        // 8dp
    
    /**
     * Medium rounding - standard app rounding
     * Use for: cards, dialogs, major components
     * Legacy: 16dp corner radius in CardViews
     */
    const val MEDIUM = 16f      // 16dp
    
    /**
     * Large rounding - prominent rounding
     * Use for: hero cards, major containers
     */
    const val LARGE = 24f       // 24dp
    
    /**
     * Extra large rounding - dramatic curves
     * Use for: special containers, artistic elements
     */
    const val XL = 32f          // 32dp
    
    /**
     * Pill shape - fully rounded
     * Use for: buttons, tags, toggle switches
     * Legacy: 150dp radius for pill-shaped buttons
     */
    const val PILL = 150f       // 150dp (legacy value)
    
    /**
     * Circle - perfect circle
     * Use for: avatars, floating action buttons, icon buttons
     */
    const val CIRCLE = 999f     // Large value to ensure circle
    
    /**
     * Component-specific radius recommendations
     */
    object Component {
        const val BUTTON = PILL         // Pill-shaped buttons (legacy style)
        const val CARD = MEDIUM         // Standard card rounding
        const val DIALOG = LARGE        // Dialog rounding for prominence
        const val INPUT_FIELD = SMALL   // Input field subtle rounding
        const val CHIP = PILL           // Pill-shaped chips
        const val AVATAR = CIRCLE       // Circular avatars
        const val IMAGE = MEDIUM        // Standard image rounding
        const val FLOATING_ACTION = CIRCLE  // FAB circular
        const val BOTTOM_SHEET = LARGE  // Bottom sheet top corners
    }
}

/**
 * Border radius helper for platform-specific implementations
 */
object BorderRadiusHelper {
    /**
     * Gets appropriate radius for component type
     */
    fun getRadiusForComponent(componentType: String): Float {
        return when (componentType.lowercase()) {
            "button" -> CafezinhoCornerRadius.Component.BUTTON
            "card" -> CafezinhoCornerRadius.Component.CARD  
            "dialog" -> CafezinhoCornerRadius.Component.DIALOG
            "input" -> CafezinhoCornerRadius.Component.INPUT_FIELD
            "chip" -> CafezinhoCornerRadius.Component.CHIP
            "avatar" -> CafezinhoCornerRadius.Component.AVATAR
            "image" -> CafezinhoCornerRadius.Component.IMAGE
            "fab" -> CafezinhoCornerRadius.Component.FLOATING_ACTION
            "bottomsheet" -> CafezinhoCornerRadius.Component.BOTTOM_SHEET
            else -> CafezinhoCornerRadius.MEDIUM
        }
    }
    
    /**
     * Checks if radius creates a circle for given dimension
     */
    fun isCircular(radius: Float, dimension: Float): Boolean {
        return radius >= dimension / 2
    }
    
    /**
     * Ensures radius doesn't exceed half the smaller dimension
     */
    fun constrainRadius(radius: Float, width: Float, height: Float): Float {
        val maxRadius = minOf(width, height) / 2
        return minOf(radius, maxRadius)
    }
}