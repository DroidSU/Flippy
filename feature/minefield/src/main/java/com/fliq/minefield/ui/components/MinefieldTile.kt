package com.fliq.minefield.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fliq.game_engine.R
import com.fliq.game_engine.models.CardType
import com.fliq.game_engine.models.Tile

@Composable
fun MinefieldTile(
    tile: Tile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val rotation by animateFloatAsState(
        targetValue = if (tile.isRevealed) 180f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "rot"
    )
    val scale by animateFloatAsState(
        if (isPressed) 0.92f else 1f,
        spring(Spring.DampingRatioMediumBouncy),
        label = "s"
    )
    val elevation by animateDpAsState(
        if (isPressed) 2.dp else 10.dp,
        spring(Spring.DampingRatioMediumBouncy),
        label = "elev"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 20 * density
                translationY = (2.dp - elevation).toPx()
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shadowElevation = elevation,
                border = BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painterResource(id = R.drawable.ic_card_back),
                        null,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(0.08f),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = elevation
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (tile.isIconVisible) {
                            Image(
                                painter = painterResource(id = if (tile.type == CardType.COIN) R.drawable.ic_coin else R.drawable.ic_bomb),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                                    .graphicsLayer {
                                        translationY = -2.dp.toPx()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
