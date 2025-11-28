package com.sujoy.flippy.ui.theme

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

// Playful color scheme for the Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = FlippyOrange,         // Bright, clickable elements
    onPrimary = DarkText,           // Text on top of primary elements
    secondary = FlippyBlue,         // Secondary actions or accents
    onSecondary = BrightText,       // Text on top of secondary elements
    background = NightSky,          // Dark background for less eye strain
    onBackground = BrightText,      // Default text color
    surface = MoonRock,             // Cards and elevated surfaces
    onSurface = BrightText,         // Text on cards
    surfaceVariant = MoonRock,
    onSurfaceVariant = BrightText,
    outline = FlippyBlue,
    error = PlayfulPink,            // Using pink for errors for a softer feel
    onError = BrightText
)

// Playful color scheme for the Light Theme
private val LightColorScheme = lightColorScheme(
    primary = FlippyOrange,         // Bright, clickable elements
    onPrimary = LightCloud,         // Text on top of primary elements
    secondary = FlippyBlue,         // Secondary actions or accents
    onSecondary = LightCloud,       // Text on top of secondary elements
    background = SunnyDay,          // Warm, friendly background
    onBackground = DarkText,        // Default text color
    surface = LightCloud,           // Cards and elevated surfaces
    onSurface = DarkText,           // Text on cards
    surfaceVariant = SunnyDay,
    onSurfaceVariant = DarkText,
    outline = FlippyBlue,
    error = PlayfulPink,
    onError = LightCloud
)

@Composable
fun FlippyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to always use our custom playful theme
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
            window.statusBarColor = colorScheme.background.toArgb() // Use background for seamless look
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
