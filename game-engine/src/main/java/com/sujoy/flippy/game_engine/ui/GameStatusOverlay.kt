package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.core.theme.gameColors
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val particleSize: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun GameStatusOverlay(
    visible: Boolean,
    score: Int,
    totalTaps: Int,
    correctTaps: Int,
    maxStreak: Int,
    totalReflexTime: Long,
    gameTime: Long,
    onDismiss: () -> Unit
) {
    val avgReflex = if (correctTaps > 0) totalReflexTime / correctTaps else 0L
    val accuracy = if (totalTaps > 0) (correctTaps.toFloat() / totalTaps * 100).toInt() else 0

    val colorScheme = MaterialTheme.colorScheme
    val gameColors = MaterialTheme.gameColors

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            if (visible) {
                ConfettiEffect()
            }

            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(20.dp),
                    shape = RoundedCornerShape(48.dp),
                    color = colorScheme.surface,
                    border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "PHASE COMPLETE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 6.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 84.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "TOTAL SCORE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                ),
                                color = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Futuristic Grid
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                NeoStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "REFLEX",
                                    value = "${avgReflex}ms",
                                    icon = Icons.Default.Bolt,
                                    color = gameColors.particleCoin // Using SoftMint/DeepMint analogue
                                )
                                NeoStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "STREAK",
                                    value = "$maxStreak",
                                    icon = Icons.Default.Timeline,
                                    color = gameColors.particleBomb // Using Rosewood/VividRose analogue
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                NeoStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "ACCURACY",
                                    value = "$accuracy%",
                                    icon = Icons.Default.AdsClick,
                                    color = Color(0xFFFACC15) // Gold remains gold usually
                                )
                                NeoStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "TIME",
                                    value = UtilityMethods.formatTime(gameTime),
                                    icon = Icons.Default.Timer,
                                    color = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        TerminalPlayButton(onClick = onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
fun NeoStatItem(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = onSurfaceColor.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, onSurfaceColor.copy(alpha = 0.05f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = onSurfaceColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = onSurfaceColor.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ConfettiEffect() {
    val confettiCount = 50
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    
    val colorScheme = MaterialTheme.colorScheme
    val gameColors = MaterialTheme.gameColors
    
    val particles = remember(colorScheme, gameColors) {
        List(confettiCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1.5f,
                speed = Random.nextFloat() * 0.03f + 0.01f,
                particleSize = Random.nextFloat() * 18f + 8f,
                color = listOf(
                    colorScheme.primary, 
                    gameColors.particleCoin, 
                    gameColors.particleBomb, 
                    Color(0xFFFACC15)
                ).random(),
                rotationSpeed = Random.nextFloat() * 6f
            )
        }
    }

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "confetti_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p: ConfettiParticle ->
            val currentY = ((p.y + (progress * p.speed * 120)) % 1.5f) * size.height
            val currentX = p.x * size.width
            
            if (currentY in 0f..size.height) {
                rotate(degrees = progress * 360 * p.rotationSpeed, pivot = Offset(currentX, currentY)) {
                    drawRect(
                        color = p.color.copy(alpha = 0.8f),
                        topLeft = Offset(currentX, currentY),
                        size = Size(p.particleSize, p.particleSize / 2.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalPlayButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1f, label = "button_scale")

    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        color = colorScheme.primary,
        onClick = onClick,
        interactionSource = interactionSource,
        shadowElevation = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Refresh, null, tint = colorScheme.onPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Replay Game",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black, 
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = colorScheme.onPrimary
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
            score = 4250,
            totalTaps = 100,
            correctTaps = 92,
            maxStreak = 24,
            totalReflexTime = 22000,
            gameTime = 120000,
            onDismiss = {}
        )
    }
}
