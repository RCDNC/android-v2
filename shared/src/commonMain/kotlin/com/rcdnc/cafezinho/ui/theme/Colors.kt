package com.rcdnc.cafezinho.ui.theme

/**
 * Core Cafezinho colors (multiplataforma)
 * Based on legacy colors.xml - these values are shared across all platforms
 */
object CafezinhoColors {
    /**
     * Primary brand color - main blue
     * Legacy: primaryColor = #0087F9
     */
    const val PRIMARY = 0xFF0087F9
    
    /**
     * Background colors
     * Legacy: background = #FFFFFF
     */
    const val BACKGROUND_LIGHT = 0xFFFFFFFF
    const val BACKGROUND_DARK = 0xFF000000
    
    /**
     * Surface color scale (dark to light)
     * Legacy: surface_950 to surface_50
     */
    const val SURFACE_950 = 0xFF0A0A0A    // Darkest
    const val SURFACE_900 = 0xFF171717
    const val SURFACE_800 = 0xFF262626  
    const val SURFACE_700 = 0xFF404040
    const val SURFACE_600 = 0xFF525252
    const val SURFACE_500 = 0xFF737373
    const val SURFACE_400 = 0xFF9CA3AF
    const val SURFACE_300 = 0xFFD1D5DB
    const val SURFACE_200 = 0xFFE5E7EB
    const val SURFACE_100 = 0xFFF3F4F6
    const val SURFACE_50 = 0xFFF9F9F9     // Lightest
    
    /**
     * Semantic colors
     * Legacy: success, error from colors.xml
     */
    const val SUCCESS = 0xFF60A74B        // Green for success states
    const val ERROR = 0xFFF75555          // Red for error states  
    const val WARNING = 0xFFEAB308        // Yellow for warning states
    const val INFO = 0xFF3B82F6           // Blue for info states
    
    /**
     * Boost theme colors (gradient)
     * Legacy: boost theme gradients
     */
    const val BOOST_START = 0xFFFFD700    // Gold
    const val BOOST_END = 0xFFFF8C00      // Orange
    
    /**
     * Café theme colors
     * For coffee-themed features and premium elements
     */
    const val CAFE_BROWN = 0xFF8B4513     // Coffee brown
    const val CAFE_CREAM = 0xFFF5DEB3     // Cream
    const val CAFE_DARK = 0xFF3C2415      // Dark roast
    
    /**
     * Social platform colors
     * For social login buttons and integrations
     */
    const val FACEBOOK_BLUE = 0xFF1877F2
    const val GOOGLE_RED = 0xFFDB4437
    const val INSTAGRAM_PINK = 0xFFE4405F
    const val TIKTOK_BLACK = 0xFF000000
    
    /**
     * Swipe action colors
     * For swipe buttons and feedback
     */
    const val LIKE_GREEN = SUCCESS         // Reuse success color
    const val DISLIKE_RED = ERROR         // Reuse error color  
    const val SUPERLIKE_BLUE = 0xFF00D4FF // Special blue for super like
    const val MATCH_PURPLE = 0xFF8B5CF6   // Purple for matches
}

/**
 * Alpha values for consistent transparency
 */
object CafezinhoAlpha {
    const val DISABLED = 0.38f
    const val INACTIVE = 0.60f  
    const val ACTIVE = 0.87f
    const val OVERLAY_LIGHT = 0.08f
    const val OVERLAY_MEDIUM = 0.16f
    const val OVERLAY_STRONG = 0.24f
}