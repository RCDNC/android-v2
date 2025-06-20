package com.rcdnc.cafezinho.ui.theme

/**
 * Design tokens for consistent spacing across platforms
 * Based on 8dp grid system and legacy margin/padding patterns
 */
object CafezinhoSpacing {
    /**
     * Base spacing scale (8dp grid)
     */
    const val NONE = 0f         // 0dp
    const val XS = 4f           // 4dp - half grid
    const val SMALL = 8f        // 8dp - base grid
    const val MEDIUM = 16f      // 16dp - 2x grid  
    const val LARGE = 24f       // 24dp - 3x grid
    const val XL = 32f          // 32dp - 4x grid
    const val XXL = 48f         // 48dp - 6x grid
    const val XXXL = 64f        // 64dp - 8x grid
    
    /**
     * Component-specific spacing
     */
    const val COMPONENT_PADDING_HORIZONTAL = MEDIUM  // 16dp
    const val COMPONENT_PADDING_VERTICAL = SMALL     // 8dp
    const val CONTENT_PADDING = MEDIUM                // 16dp
    const val SCREEN_PADDING = LARGE                  // 24dp
    
    /**
     * List and grid spacing
     */
    const val LIST_ITEM_SPACING = SMALL               // 8dp
    const val GRID_SPACING = MEDIUM                   // 16dp
    const val CARD_SPACING = MEDIUM                   // 16dp
    
    /**
     * Corner radius values
     * Based on legacy rounded corners: 8dp, 16dp, 150dp
     */
    const val CORNER_NONE = 0f          // 0dp - no rounding
    const val CORNER_SMALL = 8f         // 8dp - subtle rounding
    const val CORNER_MEDIUM = 16f       // 16dp - standard rounding  
    const val CORNER_LARGE = 24f        // 24dp - prominent rounding
    const val CORNER_PILL = 150f        // 150dp - pill shaped (legacy)
    
    /**
     * Elevation values for consistent depth
     */
    const val ELEVATION_NONE = 0f       // 0dp - flat
    const val ELEVATION_SMALL = 2f      // 2dp - subtle shadow
    const val ELEVATION_MEDIUM = 4f     // 4dp - standard shadow
    const val ELEVATION_LARGE = 8f      // 8dp - prominent shadow  
    const val ELEVATION_XL = 16f        // 16dp - floating elements
    
    /**
     * Border widths
     */
    const val BORDER_THIN = 1f          // 1dp - default borders
    const val BORDER_MEDIUM = 2f        // 2dp - emphasized borders
    const val BORDER_THICK = 4f         // 4dp - strong borders
}

/**
 * Icon sizes for different contexts
 */
object CafezinhoIconSize {
    const val SMALL = 16f       // 16dp - inline icons, chips
    const val MEDIUM = 24f      // 24dp - standard icons, buttons  
    const val LARGE = 32f       // 32dp - prominent icons
    const val XL = 48f          // 48dp - hero icons, avatar
    const val XXL = 64f         // 64dp - large avatar, placeholders
    const val XXXL = 120f       // 120dp - error state icons
}

/**
 * Animation duration constants
 */
object CafezinhoAnimation {
    const val FAST = 200        // 200ms - quick transitions
    const val NORMAL = 300      // 300ms - standard transitions
    const val SLOW = 500        // 500ms - deliberate transitions
    const val LAZY = 1000       // 1000ms - slow, attention-grabbing
}