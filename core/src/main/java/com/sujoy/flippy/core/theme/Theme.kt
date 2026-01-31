package com.sujoy.flippy.core.theme

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

// Modern 3D color scheme for the Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = PrismIndigo,
    onPrimary = GhostSlate,
    secondary = PrismAmber,
    onSecondary = DeepSpace,
    background = DeepSpace,
    onBackground = GhostSlate,
    surface = DeepSlate,
    onSurface = GhostSlate,
    surfaceVariant = DeepSlate,
    onSurfaceVariant = SlateLight,
    outline = PrismIndigoDeep,
    error = PrismRose,
    onError = GhostSlate
)

// Modern 3D color scheme for the Light Theme
private val LightColorScheme = lightColorScheme(
    primary = PrismIndigo,
    onPrimary = PureWhite,
    secondary = PrismAmber,
    onSecondary = SlateDark,
    background = GhostWhite,
    onBackground = SlateDark,
    surface = PureWhite,
    onSurface = SlateDark,
    surfaceVariant = GhostWhite,
    onSurfaceVariant = SlateMedium,
    outline = PrismIndigoLight,
    error = PrismRose,
    onError = PureWhite
)

@Composable
fun FlippyTheme(
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
            val context = view.context
            if (context is Activity) {
                val window = context.window
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
