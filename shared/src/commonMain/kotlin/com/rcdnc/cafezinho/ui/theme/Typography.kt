package com.rcdnc.cafezinho.ui.theme

/**
 * Typography definitions for shared use across platforms
 * Based on legacy styles.xml typography scale and Urbanist font
 */
object CafezinhoTypography {
    /**
     * Font weights
     * Urbanist supports: Regular (400), SemiBold (600), Bold (700)
     */
    const val WEIGHT_REGULAR = 400
    const val WEIGHT_SEMIBOLD = 600  
    const val WEIGHT_BOLD = 700
    
    /**
     * Font sizes (sp)
     * Based on legacy title/subtitle/paragraph scale: 12sp -> 48sp
     */
    const val SIZE_SMALL = 12f          // titleSmall, labelSmall
    const val SIZE_NORMAL = 14f         // bodyNormal, labelNormal
    const val SIZE_MEDIUM = 16f         // titleNormal, bodyLarge
    const val SIZE_LARGE = 20f          // titleLarge
    const val SIZE_XL = 24f             // titleXL, headlineSmall
    const val SIZE_XXL = 32f            // headlineMedium
    const val SIZE_HUGE = 48f           // titleHuge, headlineLarge
    
    /**
     * Line heights (sp)
     * Following Material Design recommendations: 1.4x-1.6x font size
     */
    const val LINE_HEIGHT_TIGHT = 1.2f     // For large headlines
    const val LINE_HEIGHT_NORMAL = 1.4f    // For body text
    const val LINE_HEIGHT_RELAXED = 1.6f   // For small text
    
    /**
     * Letter spacing (em)
     * Material Design letter spacing values
     */
    const val LETTER_SPACING_TIGHT = -0.02f    // Large headlines
    const val LETTER_SPACING_NORMAL = 0f       // Body text
    const val LETTER_SPACING_WIDE = 0.1f       // Labels, caps
    
    /**
     * Typography scale mapping
     * Maps legacy styles to modern typography roles
     */
    object Scale {
        // Headlines (for hero content, page titles)
        val HEADLINE_LARGE = TypographyStyle(
            fontSize = SIZE_HUGE,       // 48sp (titleHuge)
            fontWeight = WEIGHT_BOLD,
            lineHeight = SIZE_HUGE * LINE_HEIGHT_TIGHT,
            letterSpacing = LETTER_SPACING_TIGHT
        )
        
        val HEADLINE_MEDIUM = TypographyStyle(
            fontSize = SIZE_XXL,        // 32sp  
            fontWeight = WEIGHT_BOLD,
            lineHeight = SIZE_XXL * LINE_HEIGHT_TIGHT,
            letterSpacing = LETTER_SPACING_TIGHT
        )
        
        val HEADLINE_SMALL = TypographyStyle(
            fontSize = SIZE_XL,         // 24sp
            fontWeight = WEIGHT_BOLD,
            lineHeight = SIZE_XL * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        // Titles (for section headers, card titles)
        val TITLE_LARGE = TypographyStyle(
            fontSize = SIZE_LARGE,      // 20sp
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = SIZE_LARGE * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        val TITLE_MEDIUM = TypographyStyle(
            fontSize = SIZE_MEDIUM,     // 16sp (titleNormal)
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = SIZE_MEDIUM * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        val TITLE_SMALL = TypographyStyle(
            fontSize = SIZE_NORMAL,     // 14sp
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = SIZE_NORMAL * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        // Body text (for content, descriptions)
        val BODY_LARGE = TypographyStyle(
            fontSize = SIZE_MEDIUM,     // 16sp
            fontWeight = WEIGHT_REGULAR,
            lineHeight = SIZE_MEDIUM * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        val BODY_MEDIUM = TypographyStyle(
            fontSize = SIZE_NORMAL,     // 14sp
            fontWeight = WEIGHT_REGULAR,
            lineHeight = SIZE_NORMAL * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        val BODY_SMALL = TypographyStyle(
            fontSize = SIZE_SMALL,      // 12sp
            fontWeight = WEIGHT_REGULAR,
            lineHeight = SIZE_SMALL * LINE_HEIGHT_RELAXED,
            letterSpacing = LETTER_SPACING_NORMAL
        )
        
        // Labels (for buttons, chips, captions)
        val LABEL_LARGE = TypographyStyle(
            fontSize = SIZE_NORMAL,     // 14sp
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = SIZE_NORMAL * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_WIDE
        )
        
        val LABEL_MEDIUM = TypographyStyle(
            fontSize = SIZE_SMALL,      // 12sp
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = SIZE_SMALL * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_WIDE
        )
        
        val LABEL_SMALL = TypographyStyle(
            fontSize = 10f,             // 10sp
            fontWeight = WEIGHT_SEMIBOLD,
            lineHeight = 10f * LINE_HEIGHT_NORMAL,
            letterSpacing = LETTER_SPACING_WIDE
        )
    }
}

/**
 * Typography style data class for shared definitions
 */
data class TypographyStyle(
    val fontSize: Float,        // sp
    val fontWeight: Int,        // 400, 600, 700
    val lineHeight: Float,      // sp
    val letterSpacing: Float    // em
)