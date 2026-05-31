package com.fliq.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class FliqSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,
    val giant: Dp = 64.dp,
    
    // Semantic tokens
    val screenPadding: Dp = 24.dp,
    val cardPadding: Dp = 20.dp,
    val elementSpacing: Dp = 12.dp
)

val LocalFliqSpacing = staticCompositionLocalOf { FliqSpacing() }
