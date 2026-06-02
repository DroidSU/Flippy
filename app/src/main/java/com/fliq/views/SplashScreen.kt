package com.fliq.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.core.theme.FliqTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    // Animation States
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.95f) }

    // Dynamic sizes based on screen height
    val logoSize = (screenHeight * 0.5f).coerceIn(160.dp, 280.dp)
    val glowSize = logoSize * 1.25f

    LaunchedEffect(Unit) {
        // Step 1: Fade in and scale up logo
        launch {
            logoAlpha.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        
        // Step 2: Atmospheric glow burst
        delay(400)
        launch {
            glowAlpha.animateTo(0.6f, tween(800))
        }
        launch {
            contentScale.animateTo(1f, tween(1500, easing = LinearEasing))
        }
        
        delay(2000) // Total duration ~3s for a premium feel
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Background (Deep Space)
        SplashBackground()

        // 2. Animated Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(contentScale.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Background Radial Glow
                Box(
                    modifier = Modifier
                        .size(glowSize)
                        .blur(60.dp)
                        .alpha(glowAlpha.value)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )

                // Logo
                Image(
                    painter = painterResource(id = com.fliq.core.R.drawable.logo_full_transparent),
                    contentDescription = null,
                    modifier = Modifier
                        .size(logoSize)
                        .alpha(logoAlpha.value)
                        .scale(logoScale.value)
                        .graphicsLayer {
                            // Subtle shadow
                            shadowElevation = 20f
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tagline
            Text(
                text = "NEON ARCADE SYSTEM",
                style = FliqTheme.typography.label.copy(
                    fontSize = (screenHeight.value * 0.035f).sp,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = logoAlpha.value * 0.7f),
                modifier = Modifier.graphicsLayer { translationY = (1f - logoAlpha.value) * 20f }
            )
        }
    }
}

@Composable
private fun SplashBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")
    
    val animAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Sophisticated Deep Space Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFF1E293B).copy(alpha = 0.4f * animAlpha),
                        0.6f to Color(0xFF0F172A),
                        1.0f to Color(0xFF020617),
                        center = Offset.Zero,
                        radius = 2500f
                    )
                )
        )
    }

    // Static Stars
    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(120) {
            val randomX = Random.nextFloat() * size.width
            val randomY = Random.nextFloat() * size.height
            val randomAlpha = Random.nextFloat() * 0.4f + 0.1f
            drawCircle(
                color = Color.White.copy(alpha = randomAlpha),
                radius = Random.nextFloat() * 1.5f,
                center = Offset(randomX, randomY)
            )
        }
    }

    // Space Particles
    repeat(40) {
        FloatingSplashParticle()
    }
}

@Composable
private fun FloatingSplashParticle() {
    val config = LocalConfiguration.current
    val infiniteTransition = rememberInfiniteTransition(label = "splash_particle")
    
    val x = remember { Random.nextFloat() }
    val y = remember { Random.nextFloat() }
    val size = remember { Random.nextFloat() * 3f + 1f }.dp
    val duration = remember { Random.nextInt(15000, 30000) }

    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1200f,
        animationSpec = infiniteRepeatable(tween(duration, easing = LinearEasing), RepeatMode.Restart),
        label = "y"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(Random.nextInt(2000, 5000)), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .offset(
                x = (x * config.screenWidthDp.toFloat()).dp, 
                y = (y * config.screenHeightDp.toFloat()).dp + animY.dp
            )
            .size(size)
            .alpha(alpha)
            .background(Color.White, CircleShape)
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun SplashScreenPreview() {
    FliqTheme {
        SplashScreen(onTimeout = {})
    }
}
