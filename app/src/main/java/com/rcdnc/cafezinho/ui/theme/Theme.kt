package com.rcdnc.cafezinho.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CafezinhoDarkColorScheme = darkColorScheme(
    primary = CafezinhoPrimary,
    onPrimary = CafezinhoOnPrimary,
    secondary = CafezinhoMatch,
    onSecondary = CafezinhoOnPrimary,
    tertiary = CafezinhoSuperLike,
    onTertiary = CafezinhoOnPrimary,
    background = CafezinhoBackgroundDark,
    onBackground = CafezinhoOnSurfaceDark,
    surface = CafezinhoSurfaceDark,
    onSurface = CafezinhoOnSurfaceDark,
    surfaceVariant = CafezinhoSurfaceVariantDark,
    onSurfaceVariant = CafezinhoOnSurfaceVariantDark,
    outline = CafezinhoOutlineDark,
    outlineVariant = CafezinhoOutlineVariantDark,
    error = CafezinhoError,
    onError = CafezinhoOnError
)

private val CafezinhoLightColorScheme = lightColorScheme(
    primary = CafezinhoPrimary,
    onPrimary = CafezinhoOnPrimary,
    secondary = CafezinhoMatch,
    onSecondary = CafezinhoOnPrimary,
    tertiary = CafezinhoSuperLike,
    onTertiary = CafezinhoOnPrimary,
    background = CafezinhoBackground,
    onBackground = CafezinhoOnSurface,
    surface = CafezinhoSurface,
    onSurface = CafezinhoOnSurface,
    surfaceVariant = CafezinhoSurfaceVariant,
    onSurfaceVariant = CafezinhoOnSurfaceVariant,
    outline = CafezinhoOutline,
    outlineVariant = CafezinhoOutlineVariant,
    error = CafezinhoError,
    onError = CafezinhoOnError
)

@Composable
fun CafezinhoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> CafezinhoDarkColorScheme
        else -> CafezinhoLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CafezinhoTypographySystem,
        shapes = CafezinhoShapes,
        content = content
    )
}