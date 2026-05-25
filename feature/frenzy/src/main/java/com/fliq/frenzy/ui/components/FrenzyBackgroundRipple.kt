package com.fliq.frenzy.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun FrenzyBackgroundRipple(position: Offset, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.colorScheme.primary
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1200, easing = LinearEasing))
        onComplete()
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0.1f)
    ) {
        drawCircle(
            color = color.copy(alpha = 0.25f * (1f - progress.value)),
            radius = size.maxDimension * 0.8f * progress.value,
            center = position,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
