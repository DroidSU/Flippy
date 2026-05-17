package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.core.theme.White
import com.sujoy.flippy.core.theme.gameColors

@Composable
fun GameStatusOverlay(
    visible: Boolean,
    score: Int,
    onDismiss: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gameColors.pauseDim),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(24.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = gameColors.backgroundGradient.first().copy(alpha = 0.9f),
                    shadowElevation = 40.dp,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 48.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "!",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "GAME OVER",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp
                            ),
                            color = White
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Score Card - Frosted Glass
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 32.dp)
                            ) {
                                Text(
                                    text = "FINAL SCORE",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    ),
                                    color = White.copy(alpha = 0.4f)
                                )
                                Text(
                                    text = "$score",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        OverlayPlayButton(onClick = onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
fun OverlayPlayButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val gameColors = MaterialTheme.gameColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 8.dp else 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.primary
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "TRY AGAIN",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameStatusOverlayPreview() {
    FlippyTheme {
        GameStatusOverlay(
            visible = true,
            score = 10,
            onDismiss = {}
        )
    }
}
