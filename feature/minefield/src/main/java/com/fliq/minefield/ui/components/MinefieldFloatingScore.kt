package com.fliq.minefield.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.fliq.game_engine.models.EffectState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MinefieldFloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1.2f, spring(Spring.DampingRatioHighBouncy)); scale.animateTo(
                1f,
                tween(200)
            )
        }
        launch { offsetY.animateTo(-180f, animationSpec = tween(1000, easing = LinearEasing)) }
        launch { delay(600); alpha.animateTo(0f, animationSpec = tween(400)); onComplete() }
    }
    Text(
        text = effect.text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            shadow = androidx.compose.ui.graphics.Shadow(
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
                offset = Offset(0f, 6f),
                blurRadius = 12f
            )
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .offset {
                IntOffset(
                    effect.position.x.roundToInt() - 50,
                    (effect.position.y + offsetY.value).roundToInt() - 50
                )
            }
            .scale(scale.value)
            .alpha(alpha.value)
    )
}
