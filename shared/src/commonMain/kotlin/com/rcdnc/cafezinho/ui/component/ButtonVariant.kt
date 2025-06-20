package com.rcdnc.cafezinho.ui.component

/**
 * Variants for Cafezinho buttons following brand guidelines
 * Based on legacy styles: buttonDefault, RoundButton variants, etc.
 */
enum class ButtonVariant {
    /**
     * Primary blue button - main actions (like, continue, confirm)
     * Color: #0087F9 (primaryColor from legacy)
     */
    PRIMARY,
    
    /**
     * Secondary outlined button - secondary actions (cancel, back)
     * Transparent background with primary border
     */
    SECONDARY,
    
    /**
     * Danger red button - destructive actions (delete, block, remove)
     * Color: #F75555 (error from legacy)
     */
    DANGER,
    
    /**
     * Success green button - positive actions (match, success)
     * Color: #60A74B (success from legacy)
     */
    SUCCESS,
    
    /**
     * Boost gradient button - premium actions (boost, premium features)
     * Gradient: #FFD700 to #FF8C00 (boost theme from legacy)
     */
    BOOST
}