package com.example.chaika.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.ui.graphics.Color

val TextMain = Color(0xFFFFEBDD)       // text_main
val BackgroundDark = Color(0xFF201E28) // bg
val BackgroundLighter = Color(0xFF34323C) // bg_lighter
val DangerRed = Color(0xFFD8472D)
val RzdRed = Color(0xFFE21A1A)
val Search = Color(0xFFF0F5FA)

// Profile colors
val ProfileBackground = Color(0xFF121223)
val ProfileMenuItemBackground = Color(0xFFF6F8FA)

val LightColorScheme = lightColorScheme(
    primary = RzdRed,
    onPrimary = Color.White,
    primaryContainer = RzdRed.copy(alpha = 0.2f),
    onPrimaryContainer = RzdRed,

    secondary = Color(0xFF787878),     // light_gray
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F0F0), // light_gray2
    onSecondaryContainer = Color(0xFF3D3D3D), // dark_gray

    error = DangerRed,
    onError = Color.White,
    errorContainer = DangerRed.copy(alpha = 0.2f),
    onErrorContainer = DangerRed,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    surfaceVariant = Search, // light_gray2
    onSurfaceVariant = Color(0xFF6C6C6C), // hint
)

val DarkColorScheme = darkColorScheme(
    primary = RzdRed,
    onPrimary = Color(0xFF787878),
    primaryContainer = RzdRed.copy(alpha = 0.2f),
    onPrimaryContainer = RzdRed,

    secondary = Color(0xFF787878),     // light_gray
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF464451), // bg_lighter_2
    onSecondaryContainer = Color(0xFFF0F0F0), // light_gray2

    error = DangerRed,
    onError = Color.Black,
    errorContainer = DangerRed.copy(alpha = 0.2f),
    onErrorContainer = DangerRed,

    background = BackgroundDark,       // bg
    onBackground = TextMain,           // text_main

    surface = BackgroundDark,          // bg
    onSurface = TextMain,              // text_main

    surfaceVariant = BackgroundLighter, // bg_lighter
    onSurfaceVariant = Color(0xFF6C6C6C), // hint
)