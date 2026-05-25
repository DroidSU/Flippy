package com.fliq.minefield.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fliq.core.theme.HeartRed

@Composable
fun MinefieldBeatingHeartIcon(
    isAlive: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_beat")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAlive) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isAlive) 0.6f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (isAlive) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = HeartRed.copy(alpha = glowAlpha),
                modifier = Modifier
                    .size(size)
                    .scale(scale * 1.4f)
            )
        }

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = if (isAlive) HeartRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier
                .size(size)
                .scale(if (isAlive) scale else 1f)
                .graphicsLayer {
                    if (isAlive) {
                        shadowElevation = 8f
                    }
                }
        )
    }
}
