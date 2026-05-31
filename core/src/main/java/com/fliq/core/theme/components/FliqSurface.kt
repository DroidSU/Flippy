package com.fliq.core.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fliq.core.theme.FliqTheme

@Composable
fun FliqSurface(
    modifier: Modifier = Modifier,
    shape: Shape = FliqTheme.shapes.surface,
    color: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = FliqTheme.elevation.surface,
    showBorder: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape)
            .clip(shape)
            .background(color)
            .then(
                if (showBorder) Modifier.border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            FliqTheme.colors.surfaceHighlight,
                            Color.Transparent
                        )
                    ),
                    shape = shape
                ) else Modifier
            ),
        content = content
    )
}
