package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    val offset by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        label = "offset"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (rotation <= 90f) {
            // Front Side (Face down)
            Box(modifier = Modifier.fillMaxSize()) {
                // Shadow/Depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // Main Card body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = offset)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    // Pattern or Logo on the back
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.4f)
                            .align(Alignment.Center)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        } else {
            // Back Side (Face up - Revealed)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                // Shadow/Depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = offset),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (type) {
                            CardType.COIN -> ImageContent(R.drawable.ic_coin, "Coin")
                            CardType.BOMB -> ImageContent(R.drawable.ic_bomb, "Bomb")
                            else -> {}
                        }
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
            .padding(12.dp)
            .fillMaxSize()
    )
}
