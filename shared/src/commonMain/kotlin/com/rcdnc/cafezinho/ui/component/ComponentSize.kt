package com.rcdnc.cafezinho.ui.component

/**
 * Standardized component sizes across the app
 * Based on legacy button heights and component dimensions
 */
enum class ComponentSize {
    /**
     * Small size - compact UI, secondary actions
     * Button height: 40dp
     * Icon size: 16dp
     * Padding: 8dp horizontal
     */
    SMALL,
    
    /**
     * Medium size - default for most components
     * Button height: 48dp  
     * Icon size: 24dp
     * Padding: 16dp horizontal
     */
    MEDIUM,
    
    /**
     * Large size - prominent actions, accessibility
     * Button height: 56dp
     * Icon size: 32dp  
     * Padding: 24dp horizontal
     */
    LARGE
}

/**
 * Size values for consistent component sizing
 */
object ComponentSizeValues {
    /**
     * Button heights by size
     */
    const val BUTTON_HEIGHT_SMALL = 40f    // dp
    const val BUTTON_HEIGHT_MEDIUM = 48f   // dp  
    const val BUTTON_HEIGHT_LARGE = 56f    // dp
    
    /**
     * Icon sizes by component size
     */
    const val ICON_SIZE_SMALL = 16f        // dp
    const val ICON_SIZE_MEDIUM = 24f       // dp
    const val ICON_SIZE_LARGE = 32f        // dp
    
    /**
     * Horizontal padding by size
     */
    const val PADDING_SMALL = 8f           // dp
    const val PADDING_MEDIUM = 16f         // dp
    const val PADDING_LARGE = 24f          // dp
}