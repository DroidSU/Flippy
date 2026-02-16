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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sujoy.flippy.core.settings.AppTheme
import com.sujoy.flippy.core.settings.SettingsRepository

@Immutable
data class FlippyGameColors(
    val scorePopup: Color = Color.Unspecified,
    val particleCoin: Color = Color.Unspecified,
    val particleBomb: Color = Color.Unspecified,
    val shockwave: Color = Color.Unspecified,
    val criticalVignette: Color = Color.Unspecified,
    val feverColor1: Color = Color.Unspecified,
    val feverColor2: Color = Color.Unspecified,
    val meshColor1: Color = Color.Unspecified,
    val meshColor2: Color = Color.Unspecified,
    val pausePulse: Color = Color.Unspecified,
    val pauseDim: Color = Color.Unspecified
)

val LocalFlippyGameColors = staticCompositionLocalOf { FlippyGameColors() }

val MaterialTheme.gameColors: FlippyGameColors
    @Composable
    get() = LocalFlippyGameColors.current

private val DarkGameColors = FlippyGameColors(
    scorePopup = NeonYellow,
    particleCoin = NeonYellow,
    particleBomb = NeonRed,
    shockwave = NeonRed.copy(alpha = 0.5f),
    criticalVignette = NeonRed,
    feverColor1 = NeonMagenta,
    feverColor2 = NeonPurple,
    meshColor1 = NeonCyan,
    meshColor2 = NeonPurple,
    pausePulse = NeonYellow,
    pauseDim = Color.Black.copy(alpha = 0.6f)
)

private val LightGameColors = FlippyGameColors(
    scorePopup = GamerAzure,
    particleCoin = GamerAzure,
    particleBomb = GamerRose,
    shockwave = GamerRose.copy(alpha = 0.3f),
    criticalVignette = GamerRose,
    feverColor1 = GamerElectricPurple,
    feverColor2 = GamerAzure,
    meshColor1 = GamerAzure,
    meshColor2 = GamerElectricPurple,
    pausePulse = GamerAzure,
    pauseDim = Color.Black.copy(alpha = 0.1f)
)

// Neon Cyberpunk 2026 Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBlack,
    secondary = NeonYellow,
    onSecondary = CyberBlack,
    tertiary = NeonMagenta,
    background = CyberBlack,
    onBackground = CyberWhite,
    surface = CyberSlate,
    onSurface = CyberWhite,
    surfaceVariant = CyberSlate,
    onSurfaceVariant = CyberWhite.copy(alpha = 0.7f),
    outline = NeonCyan.copy(alpha = 0.5f),
    error = NeonRed,
    onError = CyberBlack
)

// Vibrant Gamer 2026 Light Theme
private val LightColorScheme = lightColorScheme(
    primary = GamerAzure,
    onPrimary = Color.White,
    secondary = GamerElectricPurple,
    onSecondary = Color.White,
    tertiary = GamerRose,
    background = GamerWhite,
    onBackground = GamerDeepText,
    surface = GamerSilver,
    onSurface = GamerDeepText,
    surfaceVariant = GamerWhite,
    onSurfaceVariant = GamerDeepText.copy(alpha = 0.7f),
    outline = GamerAzure.copy(alpha = 0.3f),
    error = GamerRose,
    onError = Color.White
)

@Composable
fun FlippyTheme(
    settingsRepository: SettingsRepository? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val appTheme by settingsRepository?.themeFlow?.collectAsState()
        ?: remember { mutableStateOf(AppTheme.SYSTEM) }

    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val gameColors = if (darkTheme) DarkGameColors else LightGameColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalFlippyGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
