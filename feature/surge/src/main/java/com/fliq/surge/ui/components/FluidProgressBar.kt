package com.fliq.surge.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.fliq.core.theme.NeonCyan
import com.fliq.core.util.ChamferedCornerShape
import kotlin.math.sin

@Composable
fun FluidProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "fluid_level"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "fluid_wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Box(
        modifier = modifier
            .clip(ChamferedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                ),
                shape = ChamferedCornerShape(12.dp)
            )
            .padding(2.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val fillWidth = width * animatedProgress

            clipRect {
                // Background Layer (Slightly darker)
                val pathBack = Path().apply {
                    moveTo(0f, height)
                    for (x in 0..fillWidth.toInt() step 4) {
                        val relX = x.toFloat() / (width * 0.2f) 
                        val wave = sin(relX.toDouble() + waveOffset).toFloat()
                        lineTo(x.toFloat(), height * 0.5f + wave * 6f)
                    }
                    lineTo(fillWidth, height)
                    close()
                }
                drawPath(
                    path = pathBack,
                    brush = Brush.verticalGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.3f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Foreground Layer
                val pathFront = Path().apply {
                    moveTo(0f, height)
                    for (x in 0..fillWidth.toInt() step 4) {
                        val relX = x.toFloat() / (width * 0.15f)
                        val wave = sin(relX.toDouble() - waveOffset * 1.5).toFloat()
                        lineTo(x.toFloat(), height * 0.4f + wave * 8f)
                    }
                    lineTo(fillWidth, height)
                    close()
                }
                
                drawPath(
                    path = pathFront,
                    brush = Brush.verticalGradient(
                        colors = listOf(NeonCyan, NeonCyan.copy(alpha = 0.5f)),
                        startY = 0f,
                        endY = height
                    )
                )

                // Surface Glow
                drawPath(
                    path = pathFront,
                    color = Color.White.copy(alpha = 0.4f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Glass reflection overlay
            drawRoundRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.White.copy(alpha = 0.1f),
                    0.4f to Color.Transparent,
                    1.0f to Color.Transparent
                ),
                size = size,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
    }
}
