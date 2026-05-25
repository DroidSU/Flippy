package com.fliq.minefield.ui.components

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.minefield.models.MinefieldTutorialStep
import kotlin.math.roundToInt

data class MinefieldTutorialHighlight(
    val center: Offset,
    val size: Size,
    val isCircle: Boolean = true
)

@Composable
fun MinefieldTutorialOverlay(
    step: MinefieldTutorialStep,
    highlight: MinefieldTutorialHighlight?,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    val spotlightAlpha by animateFloatAsState(targetValue = 0.8f, label = "alpha")
    
    val animCenterX by animateFloatAsState(targetValue = highlight?.center?.x ?: 0f, animationSpec = tween(500), label = "x")
    val animCenterY by animateFloatAsState(targetValue = highlight?.center?.y ?: 0f, animationSpec = tween(500), label = "y")
    val animWidth by animateFloatAsState(targetValue = highlight?.size?.width ?: 0f, animationSpec = tween(500), label = "w")
    val animHeight by animateFloatAsState(targetValue = highlight?.size?.height ?: 0f, animationSpec = tween(500), label = "h")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
            .then(
                if (step != MinefieldTutorialStep.TILE_INTERACT) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNext
                    )
                } else Modifier
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
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

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 20.dp)
        ) {
            Text("SKIP TUTORIAL", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelMedium)
        }

        val tooltipYOffset = if (animCenterY > (with(density) { 400.dp.toPx() })) {
            (animCenterY - animHeight / 2 - with(density) { 180.dp.toPx() })
        } else {
            (animCenterY + animHeight / 2 + with(density) { 40.dp.toPx() })
        }

        MinefieldTutorialTooltip(
            step = step,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, tooltipYOffset.roundToInt()) }
                .padding(horizontal = 40.dp)
        )
    }
}

@Composable
fun MinefieldTutorialTooltip(
    step: MinefieldTutorialStep,
    modifier: Modifier = Modifier
) {
    val title = when (step) {
        MinefieldTutorialStep.WELCOME -> "WELCOME TO MINEFIELD"
        MinefieldTutorialStep.STATS -> "ZERO TOLERANCE"
        MinefieldTutorialStep.TILE_INTRO -> "HIDDEN TREASURES"
        MinefieldTutorialStep.TILE_INTERACT -> "GIVE IT A TRY"
        MinefieldTutorialStep.START_GAME -> "READY TO BEGIN?"
    }

    val description = when (step) {
        MinefieldTutorialStep.WELCOME -> "A high-stakes mode where one wrong move ends everything. Focus is key."
        MinefieldTutorialStep.STATS -> "You have exactly one life. Missing a coin or hitting a bomb ends the game instantly."
        MinefieldTutorialStep.TILE_INTRO -> "Coins give score. Bombs are everywhere. Be absolutely sure before you tap!"
        MinefieldTutorialStep.TILE_INTERACT -> "Tap the highlighted tile now to experience your first match!"
        MinefieldTutorialStep.START_GAME -> "Tapping PLAY begins the session. Take a deep breath!"
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
            
            if (step != MinefieldTutorialStep.TILE_INTERACT) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "TAP TO CONTINUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
