package com.fliq.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.ui.MeshBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val gameColors = MaterialTheme.gameColors
    
    // Animation States
    val shardPos = remember { Animatable(1.2f) } // 1: Corners, 0: Center
    val wireframeAlpha = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.5f) }
    val glowAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textY = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        // Step 1: Wireframe appears
        launch { wireframeAlpha.animateTo(0.3f, tween(250)) }
        
        // Step 2: Shards fly in and snap
        delay(50)
        shardPos.animateTo(
            targetValue = 0f, 
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        
        // Step 3: Boot up - Logo fills, glow bursts
        launch { wireframeAlpha.animateTo(0f, tween(150)) }
        launch { logoAlpha.animateTo(1f, tween(200)) }
        launch { logoScale.animateTo(1.1f, spring(Spring.DampingRatioHighBouncy)); logoScale.animateTo(1f, tween(150)) }
        launch { glowAlpha.animateTo(0.7f, tween(150)); glowAlpha.animateTo(0f, tween(500)) }
        
        // Step 4: Text reveal
        delay(150)
        launch { textAlpha.animateTo(1f, tween(300)) }
        launch { textY.animateTo(0f, spring(Spring.DampingRatioLowBouncy)) }
        
        delay(600) // Fast overall duration ~1.4s
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient)),
        contentAlignment = Alignment.Center
    ) {
        MeshBackground(streak = 0)

        // The Forge Area
        Box(modifier = Modifier.size(height = 240.dp, width = 320.dp), contentAlignment = Alignment.Center) {
            
            // 1. Ghostly Wireframe (Rectangular to match full logo)
            Surface(
                modifier = Modifier.size(width = 220.dp, height = 90.dp).alpha(wireframeAlpha.value),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {}

            // 2. Flying Shards (Adjusted for wider target)
            val shardSize = 22.dp
            val distH = 180f
            val distV = 100f
            
            if (shardPos.value > 0.01f) {
                // Top Left Shard
                ShardPiece(
                    modifier = Modifier.offset { 
                        IntOffset(
                            (-distH * shardPos.value).roundToInt(), 
                            (-distV * shardPos.value).roundToInt()
                        ) 
                    },
                    size = shardSize,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Top Right Shard
                ShardPiece(
                    modifier = Modifier.offset { 
                        IntOffset(
                            (distH * shardPos.value).roundToInt(), 
                            (-distV * shardPos.value).roundToInt()
                        ) 
                    },
                    size = shardSize,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                // Bottom Left Shard
                ShardPiece(
                    modifier = Modifier.offset { 
                        IntOffset(
                            (-distH * shardPos.value).roundToInt(), 
                            (distV * shardPos.value).roundToInt()
                        ) 
                    },
                    size = shardSize,
                    color = Gold
                )
                
                // Bottom Right Shard
                ShardPiece(
                    modifier = Modifier.offset { 
                        IntOffset(
                            (distH * shardPos.value).roundToInt(), 
                            (distV * shardPos.value).roundToInt()
                        ) 
                    },
                    size = shardSize,
                    color = Color(0xFFF43F5E)
                )
            }

            // 3. Booted Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow
                Box(
                    modifier = Modifier
                        .size(width = 300.dp, height = 140.dp)
                        .blur(45.dp)
                        .alpha(glowAlpha.value)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                )

                Image(
                    painter = painterResource(id = com.fliq.core.R.drawable.logo_full_transparent),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .graphicsLayer {
                            // subtle movement
                            translationY = (1f - logoAlpha.value) * 10f
                        }
                )
            }
        }

        // Logo is now the full branding, so we remove the extra text reveal
    }
}

@Composable
private fun ShardPiece(modifier: Modifier, size: androidx.compose.ui.unit.Dp, color: Color) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size / 3),
        color = color.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {}
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    FliqTheme {
        SplashScreen(onTimeout = {})
    }
}
