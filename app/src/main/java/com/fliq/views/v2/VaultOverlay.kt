package com.fliq.views.v2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.components.FliqButton
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqSurface
import com.fliq.core.theme.components.StarBackground
import kotlinx.coroutines.delay

@Composable
fun VaultOverlay(
    stars: Int,
    xpEarned: Int,
    coinsEarned: Int,
    onNextStage: () -> Unit,
    onReplay: () -> Unit,
) {
    var animationPhase by remember { mutableIntStateOf(0) }
    val xpProgress = remember { Animatable(0f) }
    val coinCount = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(500)
        animationPhase = 1 // Show Stars
        delay(1000)
        animationPhase = 2 // Animate XP
        xpProgress.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
        animationPhase = 3 // Animate Coins
        coinCount.animateTo(coinsEarned.toFloat(), tween(1000, easing = LinearOutSlowInEasing))
        animationPhase = 4 // Show Buttons
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        StarBackground()
        FliqCard(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(24.dp),
            elevation = FliqTheme.elevation.overlay
        ) {
            Column(
                modifier = Modifier.padding(FliqTheme.spacing.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (stars > 0) "STAGE CLEARED" else "STAGE FAILED",
                    style = FliqTheme.typography.heading,
                    color = if (stars > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(FliqTheme.spacing.large))

                // 1. Stars Animation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isLit = index < stars
                        val starScale by animateFloatAsState(
                            targetValue = if (animationPhase >= 1 && isLit) 1.2f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "star_scale"
                        )

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .scale(starScale)
                                .graphicsLayer {
                                    alpha = if (animationPhase >= 1 || !isLit) 1f else 0.2f
                                },
                            tint = if (animationPhase >= 1 && isLit) Gold else Color.White.copy(alpha = 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.extraLarge))

                // 2. XP Progress
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "FLUX XP",
                            style = FliqTheme.typography.label,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "+$xpEarned",
                            style = FliqTheme.typography.label,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(FliqTheme.spacing.small))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(xpProgress.value)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.large))

                // 3. Coin Counter
                FliqSurface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.03f),
                    shape = FliqTheme.shapes.medium,
                    showBorder = true
                ) {
                    Row(
                        modifier = Modifier.padding(FliqTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "🪙",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = coinCount.value.toInt().toString(),
                            style = FliqTheme.typography.heading.copy(fontSize = 32.sp),
                            color = Gold
                        )
                        Text(
                            text = " CREDITS",
                            style = FliqTheme.typography.label,
                            color = Gold.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.extraLarge))

                // 4. Action Buttons
                AnimatedVisibility(
                    visible = animationPhase >= 4,
                    enter = fadeIn() + slideInVertically { 20 }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FliqButton(
                            text = "REPLAY",
                            onClick = onReplay,
                            modifier = Modifier.weight(1f),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = Color.White
                        )
                        FliqButton(
                            text = "SHARE",
                            onClick = { /* TODO: Open Share Sheet */ },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                        FliqButton(
                            text = if (stars > 0) "NEXT" else "EXIT",
                            onClick = onNextStage,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
