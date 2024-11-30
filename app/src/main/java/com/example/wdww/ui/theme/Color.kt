/**
 * Color definitions for WDWW.
 * Provides a complete color scheme for both light and dark themes.
 *
 * The color scheme follows Material Design 3 guidelines with:
 * - Primary colors for main UI elements
 * - Secondary colors for less prominent components
 * - Tertiary colors for contrasting accents
 * - Error colors for error states
 * - Surface and background colors for different UI layers
 * - On-colors for content that appears on top of their respective surfaces
 *
 * Color naming convention:
 * - md_theme_[light/dark]_[primary/secondary/tertiary/error/background/surface/surfaceVariant/outline]
 * - Each color has a corresponding "on" color for content
 */
package com.example.wdww.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Light theme colors
 */
// Primary colors
val md_theme_light_primary = Color(0xFF6B5C4D)         // Main brand color
val md_theme_light_onPrimary = Color(0xFFFFFFFF)       // Content on primary
val md_theme_light_primaryContainer = Color(0xFFF4DFCD) // Container with primary theme
val md_theme_light_onPrimaryContainer = Color(0xFF241A0E) // Content on primary container

// Secondary colors
val md_theme_light_secondary = Color(0xFF635D59)         // Less prominent components
val md_theme_light_onSecondary = Color(0xFFFFFFFF)       // Content on secondary
val md_theme_light_secondaryContainer = Color(0xFFEAE1DB) // Container with secondary theme
val md_theme_light_onSecondaryContainer = Color(0xFF1F1B17) // Content on secondary container

// Tertiary colors
val md_theme_light_tertiary = Color(0xFF5E5F58)         // Accent color
val md_theme_light_onTertiary = Color(0xFFFFFFFF)       // Content on tertiary
val md_theme_light_tertiaryContainer = Color(0xFFE3E3DA) // Container with tertiary theme
val md_theme_light_onTertiaryContainer = Color(0xFF1B1C17) // Content on tertiary container

// Error colors
val md_theme_light_error = Color(0xFFBA1A1A)           // Error states
val md_theme_light_errorContainer = Color(0xFFFFDAD6)   // Container with error theme
val md_theme_light_onError = Color(0xFFFFFFFF)         // Content on error
val md_theme_light_onErrorContainer = Color(0xFF410002) // Content on error container

// Background and surface colors
val md_theme_light_background = Color(0xFFF5F0EE)      // Main background
val md_theme_light_onBackground = Color(0xFF1D1B1A)    // Content on background
val md_theme_light_surface = Color(0xFFFFFBFF)         // Surface elements
val md_theme_light_onSurface = Color(0xFF1D1B1A)       // Content on surface
val md_theme_light_surfaceVariant = Color(0xFFE7E1DE)  // Alternative surface
val md_theme_light_onSurfaceVariant = Color(0xFF494644) // Content on surface variant
val md_theme_light_outline = Color(0xFF7A7674)         // Outline elements

/**
 * Dark theme colors
 */
// Primary colors
val md_theme_dark_primary = Color(0xFFD7C3B1)         // Main brand color
val md_theme_dark_onPrimary = Color(0xFF3A2E22)       // Content on primary
val md_theme_dark_primaryContainer = Color(0xFF524437) // Container with primary theme
val md_theme_dark_onPrimaryContainer = Color(0xFFF4DFCD) // Content on primary container

// Secondary colors
val md_theme_dark_secondary = Color(0xFFCDC5BF)         // Less prominent components
val md_theme_dark_onSecondary = Color(0xFF34302C)       // Content on secondary
val md_theme_dark_secondaryContainer = Color(0xFF4A4642) // Container with secondary theme
val md_theme_dark_onSecondaryContainer = Color(0xFFEAE1DB) // Content on secondary container

// Tertiary colors
val md_theme_dark_tertiary = Color(0xFFC7C7BE)         // Accent color
val md_theme_dark_onTertiary = Color(0xFF30312B)       // Content on tertiary
val md_theme_dark_tertiaryContainer = Color(0xFF464741) // Container with tertiary theme
val md_theme_dark_onTertiaryContainer = Color(0xFFE3E3DA) // Content on tertiary container

// Error colors
val md_theme_dark_error = Color(0xFFFFB4AB)           // Error states
val md_theme_dark_errorContainer = Color(0xFF93000A)   // Container with error theme
val md_theme_dark_onError = Color(0xFF690005)         // Content on error
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6) // Content on error container

// Background and surface colors
val md_theme_dark_background = Color(0xFF1D1B1A)      // Main background
val md_theme_dark_onBackground = Color(0xFFE7E1DE)    // Content on background
val md_theme_dark_surface = Color(0xFF1D1B1A)         // Surface elements
val md_theme_dark_onSurface = Color(0xFFE7E1DE)       // Content on surface
val md_theme_dark_surfaceVariant = Color(0xFF494644)  // Alternative surface
val md_theme_dark_onSurfaceVariant = Color(0xFFCBC4C0) // Content on surface variant
val md_theme_dark_outline = Color(0xFF948F8B)         // Outline elements