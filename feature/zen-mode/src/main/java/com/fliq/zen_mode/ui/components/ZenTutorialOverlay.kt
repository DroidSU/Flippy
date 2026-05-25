package com.fliq.zen_mode.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.zen_mode.models.ZenTutorialStep
import kotlin.math.roundToInt

data class TutorialHighlight(
    val center: Offset,
    val size: Size,
    val isCircle: Boolean = true
)

@Composable
fun ZenTutorialOverlay(
    step: ZenTutorialStep,
    highlight: TutorialHighlight?,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    val spotlightAlpha by animateFloatAsState(targetValue = 0.8f, label = "alpha")
    
    // Animation for spotlight position and size
    val animCenterX by animateFloatAsState(targetValue = highlight?.center?.x ?: 0f, animationSpec = tween(500), label = "x")
    val animCenterY by animateFloatAsState(targetValue = highlight?.center?.y ?: 0f, animationSpec = tween(500), label = "y")
    val animWidth by animateFloatAsState(targetValue = highlight?.size?.width ?: 0f, animationSpec = tween(500), label = "w")
    val animHeight by animateFloatAsState(targetValue = highlight?.size?.height ?: 0f, animationSpec = tween(500), label = "h")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f) // Required for BlendMode.Clear to work on some devices
            .then(
                if (step != ZenTutorialStep.TILE_INTERACT) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNext
                    )
                } else Modifier
            )
    ) {
        // Scrim with spotlight hole
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                addRect(Rect(0f, 0f, size.width, size.height))
            }
            
            drawRect(color = Color.Black.copy(alpha = spotlightAlpha))
            
            if (highlight != null) {
                if (highlight.isCircle) {
                    drawCircle(
                        color = Color.Transparent,
                        radius = (animWidth.coerceAtLeast(animHeight) / 2) + 20f,
                        center = Offset(animCenterX, animCenterY),
                        blendMode = BlendMode.Clear
                    )
                } else {
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(animCenterX - animWidth / 2 - 10f, animCenterY - animHeight / 2 - 10f),
                        size = Size(animWidth + 20f, animHeight + 20f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }

        // Skip Button
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 20.dp)
        ) {
            Text("SKIP TUTORIAL", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelMedium)
        }

        // Tooltip instructions
        val tooltipYOffset = if (animCenterY > (with(density) { 400.dp.toPx() })) {
            // Highlight is in lower half, show tooltip above
            (animCenterY - animHeight / 2 - with(density) { 180.dp.toPx() })
        } else {
            // Highlight is in upper half, show tooltip below
            (animCenterY + animHeight / 2 + with(density) { 40.dp.toPx() })
        }

        TutorialTooltip(
            step = step,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, tooltipYOffset.roundToInt()) }
                .padding(horizontal = 40.dp)
        )
    }
}

@Composable
fun TutorialTooltip(
    step: ZenTutorialStep,
    modifier: Modifier = Modifier
) {
    val title = when (step) {
        ZenTutorialStep.WELCOME -> "WELCOME TO ZEN"
        ZenTutorialStep.STATS -> "WATCH YOUR SCORE"
        ZenTutorialStep.TILE_INTRO -> "HIDDEN TREASURES"
        ZenTutorialStep.TILE_INTERACT -> "GIVE IT A TRY"
        ZenTutorialStep.START_GAME -> "READY TO BEGIN?"
    }

    val description = when (step) {
        ZenTutorialStep.WELCOME -> "A mode of pure focus. No timers, no pressure. Just you and the tiles."
        ZenTutorialStep.STATS -> "Your score and remaining lives are tracked here."
        ZenTutorialStep.TILE_INTRO -> "Once a tile is revealed, tap on coins to increase your score. Avoid bombs at all costs - they deplete your lives."
        ZenTutorialStep.TILE_INTERACT -> "Tap the highlighted tile now to experience your first match!"
        ZenTutorialStep.START_GAME -> "Tapping PLAY begins the session. Take a deep breath and stay calm."
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            if (step != ZenTutorialStep.TILE_INTERACT) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "TAP TO CONTINUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.graphicsLayer {
                        // Subtle pulse could be added here
                    }
                )
            }
        }
    }
}
