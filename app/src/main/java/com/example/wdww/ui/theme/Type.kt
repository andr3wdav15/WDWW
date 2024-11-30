/**
 * Typography definitions for WDWW.
 * 
 * This file defines the type scale for the application, following Material Design 3 guidelines.
 * The type scale includes various text styles for different purposes:
 * - Headlines: Large text for key screens and features
 * - Titles: Section headers and important UI elements
 * - Body: Main content text
 * - Labels: Supporting text and UI elements
 *
 * Each style defines:
 * - Font weight: The thickness of the text
 * - Font size: The size of the text in sp (scale-independent pixels)
 * - Line height: The vertical space between lines
 * - Letter spacing: The horizontal space between letters
 */
package com.example.wdww.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material Design 3 Typography scale for WDWW app
 */
val Typography = Typography(
    // Headlines - Used for the largest text elements
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,        // Most prominent weight for main headlines
        fontSize = 34.sp,                    // Largest size for impactful headlines
        lineHeight = 40.sp,                  // Comfortable reading height
        letterSpacing = (-0.5).sp           // Tighter spacing for large text
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,    // Slightly less prominent than large
        fontSize = 30.sp,                    // Medium headline size
        lineHeight = 36.sp,                  // Balanced line height
        letterSpacing = (-0.25).sp          // Slightly tighter spacing
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Medium,      // Standard weight for smaller headlines
        fontSize = 24.sp,                    // Smallest headline size
        lineHeight = 32.sp,                  // Proportional line height
        letterSpacing = 0.sp                 // Normal letter spacing
    ),

    // Titles - Used for section headers and important UI elements
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,    // Strong emphasis for large titles
        fontSize = 22.sp,                    // Prominent size for main titles
        lineHeight = 28.sp,                  // Comfortable reading height
        letterSpacing = 0.sp                 // Standard spacing
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,      // Medium emphasis for subtitles
        fontSize = 16.sp,                    // Standard size for subtitles
        lineHeight = 24.sp,                  // Clear line separation
        letterSpacing = 0.15.sp             // Slightly increased for readability
    ),

    // Body - Used for main content text
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,      // Standard weight for body text
        fontSize = 16.sp,                    // Comfortable reading size
        lineHeight = 24.sp,                  // Generous line height for readability
        letterSpacing = 0.5.sp              // Increased spacing for better legibility
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,      // Standard weight for smaller body text
        fontSize = 14.sp,                    // Smaller size for dense content
        lineHeight = 20.sp,                  // Compact but readable
        letterSpacing = 0.25.sp             // Balanced spacing
    ),

    // Labels - Used for supporting text and UI elements
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,      // Medium emphasis for UI labels
        fontSize = 12.sp,                    // Small size for labels
        lineHeight = 16.sp,                  // Compact height for UI elements
        letterSpacing = 0.5.sp              // Increased spacing for clarity
    )
)