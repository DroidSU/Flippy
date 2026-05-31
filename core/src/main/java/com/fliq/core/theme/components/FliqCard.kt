package com.fliq.core.theme.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.fliq.core.theme.FliqTheme

@Composable
fun FliqCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = FliqTheme.colors.surfaceHighlight.copy(alpha = 0.05f),
    elevation: Dp = FliqTheme.elevation.card,
    contentPadding: Dp = FliqTheme.spacing.cardPadding,
    content: @Composable BoxScope.() -> Unit
) {
    FliqSurface(
        modifier = modifier,
        color = backgroundColor,
        elevation = elevation,
        shape = FliqTheme.shapes.card,
        showBorder = true
    ) {
        BoxScopeWrapper(contentPadding, content)
    }
}

@Composable
private fun BoxScope.BoxScopeWrapper(
    padding: Dp,
    content: @Composable BoxScope.() -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(padding),
        content = content
    )
}
