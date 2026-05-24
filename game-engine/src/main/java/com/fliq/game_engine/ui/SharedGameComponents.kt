package com.fliq.game_engine.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.models.EffectState
import com.fliq.game_engine.models.GameStatus
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun CriticalVignette() {
    val infiniteTransition = rememberInfiniteTransition(label = "critical")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    0.0f to Color.Transparent,
                    1.0f to MaterialTheme.gameColors.criticalVignette.copy(alpha = alpha),
                )
            )
    )
}

@Composable
fun SparkleEffect(effect: EffectState, onComplete: () -> Unit) {
    val particles = remember { List(12) { Random.nextFloat() * 360f } }
    val progress = remember { Animatable(0f) }
    val particleColor = MaterialTheme.gameColors.particleCoin

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset {
        IntOffset(
            effect.position.x.roundToInt(),
            effect.position.y.roundToInt()
        )
    }) {
        particles.forEach { angle ->
            val rad = Math.toRadians(angle.toDouble())
            val dist = 140f * progress.value
            val x = (Math.cos(rad) * dist).toFloat()
            val y = (Math.sin(rad) * dist).toFloat()

            drawCircle(
                color = particleColor.copy(alpha = 1f - progress.value),
                radius = 8f * (1f - progress.value),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun BombEffect(effect: EffectState, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val shockwaveColor = MaterialTheme.gameColors.shockwave
    val particleColor = MaterialTheme.gameColors.particleBomb

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset {
        IntOffset(
            effect.position.x.roundToInt(),
            effect.position.y.roundToInt()
        )
    }) {
        // Shockwave
        drawCircle(
            color = shockwaveColor.copy(alpha = shockwaveColor.alpha * (1f - progress.value)),
            radius = 250f * progress.value,
            style = Stroke(width = 8f)
        )
        // Fire/Smoke
        drawCircle(
            color = particleColor.copy(alpha = 1f - progress.value),
            radius = 60f * (1f - progress.value)
        )
    }
}

@Composable
fun MeshBackground(streak: Int = 0) {
    val isFever = streak >= 10
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "y"
    )

    val gameColors = MaterialTheme.gameColors

    val color1 by animateColorAsState(
        targetValue = if (isFever) gameColors.feverColor1 else gameColors.meshColor1,
        animationSpec = tween(1500), label = "c1"
    )
    val color2 by animateColorAsState(
        targetValue = if (isFever) gameColors.feverColor2 else gameColors.meshColor2,
        animationSpec = tween(1500), label = "c2"
    )

    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(100.dp)
            .alpha(if (isLightTheme) 0.6f else 0.4f)
    ) {
        drawCircle(
            color = color1.copy(alpha = 0.5f),
            radius = size.width,
            center = Offset(size.width / 2 + xOffset, size.height / 3 + yOffset)
        )
        drawCircle(
            color = color2.copy(alpha = 0.4f),
            radius = size.width * 0.8f,
            center = Offset(size.width / 4 - xOffset, size.height / 1.5f - yOffset)
        )
    }
    
    // Floating Particles
    repeat(15) {
         FloatingParticle()
    }
}

@Composable
fun FloatingParticle() {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    val x = remember { Random.nextFloat() }
    val y = remember { Random.nextFloat() }
    val size = remember { Random.nextFloat() * 4 + 2 }
    val duration = remember { Random.nextInt(4000, 8000) }
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(duration / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    
    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(tween(duration, easing = LinearEasing), RepeatMode.Restart),
        label = "y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = (y * 1000).dp, start = (x * 400).dp)
    ) {
        Box(
            modifier = Modifier
                .offset(y = animY.dp)
                .size(size.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha), CircleShape)
        )
    }
}

@Composable
fun PlayButtonComponent(
    status: GameStatus,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val isPlaying = status == GameStatus.PLAYING

    val gradient = if (isPlaying) {
        Brush.linearGradient(listOf(BombRed, MaterialTheme.colorScheme.error))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.scale(scale)) {
        if (!isPlaying) {
            // Pulsing background for CTA
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(pulseScale)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = if (isPlaying) BombRed else MaterialTheme.colorScheme.primary
                )
                .background(brush = gradient, shape = CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onAction
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isPlaying) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
