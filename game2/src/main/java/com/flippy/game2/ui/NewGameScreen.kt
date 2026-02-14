package com.flippy.game2.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flippy.game2.data.NewGameUiEvent
import com.flippy.game2.data.RippleState
import kotlinx.coroutines.flow.Flow

@Composable
fun NewGameScreen(
    title: String,
    description: String,
    activeTarget: TargetCircle?,
    events: Flow<NewGameUiEvent>,
    onCanvasTap: (Float, Float, Float, Float, Float) -> Unit
) {
    val ripples = remember { mutableStateListOf<RippleState>() }
    val density = LocalDensity.current.density

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is NewGameUiEvent.ShowRipple -> {
                    ripples.add(RippleState(event.x, event.y, event.id))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onCanvasTap(
                        offset.x, 
                        offset.y, 
                        size.width.toFloat(), 
                        size.height.toFloat(), 
                        density
                    )
                }
            }
    ) {
        // Custom Canvas for Ripple Effects and Target Circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Target Circle
            activeTarget?.let { target ->
                val center = Offset(target.xPercent * size.width, target.yPercent * size.height)
                val radiusPx = target.radiusDp.dp.toPx()
                
                // Outer glow/shadow effect
                drawCircle(
                    color = Color(0xFF6366F1).copy(alpha = 0.2f),
                    radius = radiusPx * 1.2f,
                    center = center
                )
                // Inner circle
                drawCircle(
                    color = Color(0xFF6366F1).copy(alpha = 0.4f),
                    radius = radiusPx,
                    center = center
                )
                // Outline
                drawCircle(
                    color = Color(0xFF6366F1),
                    radius = radiusPx,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Draw Ripples
            ripples.forEach { ripple ->
                drawCircle(
                    color = Color.Cyan.copy(alpha = 1f - ripple.animationProgress.value),
                    radius = ripple.animationProgress.value * 250f,
                    center = Offset(ripple.x, ripple.y),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // UI Content
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }

    // Clean up finished ripples
    ripples.forEach { ripple ->
        LaunchedEffect(ripple.id) {
            ripple.animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
            ripples.remove(ripple)
        }
    }
}
