package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        label = "rotation"
    )

    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 8 * density
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(MaterialTheme.colorScheme.surface),
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
