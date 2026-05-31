package com.fliq.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class FliqShapes(
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(16.dp),
    val large: Shape = RoundedCornerShape(24.dp),
    val extraLarge: Shape = RoundedCornerShape(32.dp),
    
    // Semantic tokens
    val card: Shape = RoundedCornerShape(24.dp),
    val button: Shape = RoundedCornerShape(28.dp),
    val surface: Shape = RoundedCornerShape(24.dp)
)

val LocalFliqShapes = staticCompositionLocalOf { FliqShapes() }
