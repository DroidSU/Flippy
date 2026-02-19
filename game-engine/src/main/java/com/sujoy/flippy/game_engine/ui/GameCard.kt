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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 12.dp,
        label = "elevation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 20 * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (rotation <= 90f) {
            // --- High-End Front Side ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        shadowElevation = elevation.toPx()
                        shape = RoundedCornerShape(24.dp)
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E293B), // TwilightSurface
                                Color(0xFF0F172A)  // TwilightDeep
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                // Futuristic Geometric Pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.4f)
                        .align(Alignment.Center)
                        .background(
                            color = Color(0xFF6366F1).copy(alpha = 0.1f), // ElectricIndigo alpha
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                )
            }
        } else {
            // --- High-End Revealed Side ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { 
                        rotationY = 180f
                        shadowElevation = elevation.toPx()
                    }
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    tonalElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White, Color(0xFFF1F5F9))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (type) {
                            CardType.COIN -> ImageContent(R.drawable.ic_coin, "Credit")
                            CardType.BOMB -> ImageContent(R.drawable.ic_bomb, "Danger")
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
            .padding(16.dp)
            .fillMaxSize()
    )
}
