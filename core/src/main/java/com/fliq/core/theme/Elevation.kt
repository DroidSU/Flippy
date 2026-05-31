package com.fliq.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class FliqElevation(
    val none: Dp = 0.dp,
    val low: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val high: Dp = 16.dp,
    
    // Semantic tokens
    val card: Dp = 8.dp,
    val surface: Dp = 4.dp,
    val overlay: Dp = 24.dp
)

val LocalFliqElevation = staticCompositionLocalOf { FliqElevation() }
