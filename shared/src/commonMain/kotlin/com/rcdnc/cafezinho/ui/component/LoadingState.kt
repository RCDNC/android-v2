package com.rcdnc.cafezinho.ui.component

/**
 * Loading states for components and screens
 * Used across the app for consistent loading UX
 */
enum class LoadingState {
    /**
     * Initial state - no loading, no data, no error
     */
    IDLE,
    
    /**
     * Loading in progress - show spinner/skeleton
     */
    LOADING,
    
    /**
     * Content loaded successfully - show data
     */
    SUCCESS,
    
    /**
     * Error occurred during loading - show error state
     */
    ERROR,
    
    /**
     * Refreshing existing data - show refresh indicator
     */
    REFRESHING
}

/**
 * Generic result wrapper for loading operations
 */
sealed class LoadingResult<out T> {
    object Loading : LoadingResult<Nothing>()
    data class Success<T>(val data: T) : LoadingResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : LoadingResult<Nothing>()
}