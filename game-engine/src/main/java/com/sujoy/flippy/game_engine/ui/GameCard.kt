package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.core.theme.gameColors
import com.sujoy.flippy.game_engine.R
import com.sujoy.flippy.game_engine.models.CardType

@Composable
fun GameCard(
    isRevealed: Boolean,
    type: CardType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val gameColors = MaterialTheme.gameColors

    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16 * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (rotation <= 90f) {
            // Front Side (Face down) - Frosted Glass Look
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .blur(if (isPressed) 2.dp else 0.dp) // Subtle effect on press
            ) {
                // Border/Highlight
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Minimal pattern
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.3f)
                                .graphicsLayer { alpha = 0.1f }
                        ) {
                             Image(
                                painter = painterResource(id = R.drawable.ic_card_back), // Using brand mark as pattern
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        } else {
            // Back Side (Face up - Revealed)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    border = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            if (type == CardType.COIN) gameColors.goldGradient else gameColors.bombGradient
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = if (type == CardType.COIN) {
                                        listOf(gameColors.tileCoin.copy(alpha = 0.2f), Color.Transparent)
                                    } else {
                                        listOf(gameColors.tileBomb.copy(alpha = 0.2f), Color.Transparent)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ImageContent(
                            drawableId = if (type == CardType.COIN) R.drawable.ic_coin else R.drawable.ic_bomb,
                            description = type.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageContent(drawableId: Int, description: String) {
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = description,
        modifier = Modifier
            .padding(14.dp)
            .fillMaxSize()
    )
}
