package com.example.wdww.ui.theme

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VibrantRed,
    onPrimary = PureWhite,
    primaryContainer = DeepRed,
    onPrimaryContainer = PureWhite,
    
    secondary = TextGray,
    onSecondary = AppBlack,
    secondaryContainer = SurfaceLight,
    onSecondaryContainer = PureWhite,
    
    background = AppBlack,
    onBackground = PureWhite,
    
    surface = SurfaceBlack,
    onSurface = PureWhite,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextGray,
    
    error = ErrorRed,
    onError = PureWhite,
    
    outline = SubtleGray
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantRed,
    onPrimary = PureWhite,
    primaryContainer = VibrantRed,      // Changed to VibrantRed for better visibility
    onPrimaryContainer = PureWhite,
    
    secondary = DarkText,
    onSecondary = PureWhite,
    secondaryContainer = VibrantRed,    // Changed to VibrantRed for badges
    onSecondaryContainer = PureWhite,   // White text on red badges
    
    background = LightBackground,
    onBackground = DarkText,
    
    surface = LightSurface,
    onSurface = DarkText,
    surfaceVariant = LightBackground,
    onSurfaceVariant = SecondaryText,
    
    error = ErrorRed,
    onError = PureWhite,
    
    outline = SecondaryText
)

@Composable
fun WDWWTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make the system bars match our theme
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}