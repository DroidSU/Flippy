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
    scorePopup = SoftMint,
    particleCoin = SoftMint,
    particleBomb = Rosewood,
    shockwave = Rosewood.copy(alpha = 0.5f),
    criticalVignette = Rosewood,
    feverColor1 = ElectricIndigo,
    feverColor2 = SoftMint,
    meshColor1 = ElectricIndigo,
    meshColor2 = TwilightElevated,
    pausePulse = ElectricIndigo,
    pauseDim = Color.Black.copy(alpha = 0.5f)
)

private val LightGameColors = FlippyGameColors(
    scorePopup = InkTeal,
    particleCoin = InkTeal,
    particleBomb = InkRose,
    shockwave = InkRose.copy(alpha = 0.3f),
    criticalVignette = InkRose,
    feverColor1 = InkIndigo,
    feverColor2 = InkTeal,
    meshColor1 = InkTeal,
    meshColor2 = InkIndigo,
    pausePulse = InkTeal,
    pauseDim = Color.Black.copy(alpha = 0.1f)
)

// Twilight Slate Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color.White,
    secondary = SoftMint,
    onSecondary = TwilightDeep,
    tertiary = Rosewood,
    background = TwilightDeep,
    onBackground = SlateText,
    surface = TwilightSurface,
    onSurface = SlateText,
    surfaceVariant = TwilightElevated,
    onSurfaceVariant = SlateTextDim,
    outline = ElectricIndigo.copy(alpha = 0.2f),
    error = Rosewood,
    onError = Color.White
)

// Zenith Slate Light Theme
private val LightColorScheme = lightColorScheme(
    primary = InkIndigo,
    onPrimary = Color.White,
    secondary = InkTeal,
    onSecondary = Color.White,
    tertiary = InkRose,
    background = ZenithWhite,
    onBackground = ZenithDeepText,
    surface = ZenithSurface,
    onSurface = ZenithDeepText,
    surfaceVariant = ZenithElevated,
    onSurfaceVariant = ZenithMutedText,
    outline = InkIndigo.copy(alpha = 0.2f),
    error = InkRose,
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
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
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
