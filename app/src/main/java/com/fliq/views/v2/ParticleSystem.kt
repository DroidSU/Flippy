package com.fliq.views.v2

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ParticleEffect(
    position: Offset,
    color: Color,
    onFinished: () -> Unit
) {
    val particles = remember { List(15) { ParticleData() } }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val angleRad = Math.toRadians(p.angle.toDouble())
            val dist = 150f * progress.value * p.speed
            val x = position.x + (cos(angleRad) * dist).toFloat()
            val y = position.y + (sin(angleRad) * dist).toFloat()

            drawCircle(
                color = color.copy(alpha = 1f - progress.value),
                radius = 6f * (1f - progress.value),
                center = Offset(x, y)
            )
        }
    }
}

class ParticleData {
    val angle = Random.nextFloat() * 360f
    val speed = Random.nextFloat() * 1.5f + 0.5f
}
