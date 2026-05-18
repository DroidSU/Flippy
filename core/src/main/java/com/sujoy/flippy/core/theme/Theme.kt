package com.sujoy.flippy.core.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sujoy.flippy.core.settings.SettingsRepository

@Immutable
data class FliqGameColors(
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

val LocalFliqGameColors = staticCompositionLocalOf { FliqGameColors() }

val MaterialTheme.gameColors: FliqGameColors
    @Composable
    get() = LocalFliqGameColors.current

private val DarkGameColors = FliqGameColors(
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

private val LightGameColors = FliqGameColors(
    scorePopup = Gold,
    particleCoin = Gold,
    particleBomb = BombRed,
    shockwave = BombRed.copy(alpha = 0.5f),
    criticalVignette = BombRed,
    feverColor1 = NeonPink,
    feverColor2 = NeonPurple,
    meshColor1 = ElectricBlue,
    meshColor2 = NeonCyan,
    pausePulse = Gold,
    pauseDim = Color.Black.copy(alpha = 0.4f),
    tileCoin = Gold,
    tileBomb = BombRed,
    goldGradient = listOf(CoinYellow, CoinOrange),
    bombGradient = listOf(BombRed, BombOrange),
    backgroundGradient = listOf(LightBgAccent, LightBgMain, LightBgSurface)
)

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

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = White,
    secondary = Gold,
    onSecondary = LightBgMain,
    tertiary = NeonPurple,
    background = LightBgMain,
    onBackground = LightTextPrimary,
    surface = LightBgSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBgSlate,
    onSurfaceVariant = LightTextSecondary,
    outline = ElectricBlue.copy(alpha = 0.3f),
    error = BombRed,
    onError = White
)

@Composable
fun FliqTheme(
    settingsRepository: SettingsRepository? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    /* Keeping appTheme for future use, though currently forced to Dark
    val appTheme by settingsRepository?.themeFlow?.collectAsState()
        ?: remember { mutableStateOf(AppTheme.DARK) } 
    */

    // Force Dark mode only as per requirement
    val darkTheme = true
    /*
    val appThemeValue = settingsRepository?.getAppTheme() ?: AppTheme.DARK
    val darkTheme = when (appThemeValue) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    */

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme 
    val gameColors = if (darkTheme) DarkGameColors else LightGameColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalFliqGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
