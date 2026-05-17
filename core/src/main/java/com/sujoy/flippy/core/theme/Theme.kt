package com.sujoy.flippy.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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
    val pauseDim: Color = Color.Unspecified,
    val tileCoin: Color = Color.Unspecified,
    val tileBomb: Color = Color.Unspecified,
    val goldGradient: List<Color> = emptyList(),
    val bombGradient: List<Color> = emptyList(),
    val backgroundGradient: List<Color> = emptyList()
)

val LocalFlippyGameColors = staticCompositionLocalOf { FlippyGameColors() }

val MaterialTheme.gameColors: FlippyGameColors
    @Composable
    get() = LocalFlippyGameColors.current

private val DarkGameColors = FlippyGameColors(
    scorePopup = Gold,
    particleCoin = Gold,
    particleBomb = BombRed,
    shockwave = BombRed.copy(alpha = 0.5f),
    criticalVignette = BombRed,
    feverColor1 = NeonPink,
    feverColor2 = NeonPurple,
    meshColor1 = NeonCyan,
    meshColor2 = NeonPurple,
    pausePulse = Gold,
    pauseDim = Color.Black.copy(alpha = 0.7f),
    tileCoin = Gold,
    tileBomb = BombRed,
    goldGradient = listOf(CoinYellow, CoinOrange),
    bombGradient = listOf(BombRed, BombOrange),
    backgroundGradient = listOf(BgDeepDark, BgMidnight, BgSlate)
)

private val LightGameColors = DarkGameColors // Focus on Dark Mode as per requirement

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = BgDeepDark,
    secondary = Gold,
    onSecondary = BgDeepDark,
    tertiary = NeonPink,
    background = BgDeepDark,
    onBackground = White,
    surface = BgMidnight,
    onSurface = White,
    surfaceVariant = BgSlate,
    onSurfaceVariant = White.copy(alpha = 0.7f),
    outline = NeonCyan.copy(alpha = 0.5f),
    error = BombRed,
    onError = White
)

@Composable
fun FlippyTheme(
    settingsRepository: SettingsRepository? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val appTheme by settingsRepository?.themeFlow?.collectAsState()
        ?: remember { mutableStateOf(AppTheme.DARK) } // Default to Dark

    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    // Always use DarkColorScheme for that premium dark feel unless light is strictly requested
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme 

    val gameColors = DarkGameColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
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
