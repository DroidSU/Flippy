package com.fliq.auth.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.auth.R
import com.fliq.core.theme.FliqTheme
import kotlin.random.Random

@Composable
fun LoginOptionsView(
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    // Calculate responsive sizes
    val animSize = (screenHeight * 0.35f).coerceIn(120.dp, 200.dp)
    val outerPadding = (screenHeight * 0.08f).coerceIn(16.dp, 48.dp)
    val energyPathColor = FliqTheme.colors.energyPath

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AuthBackground()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Branding Area
            BrandingHero(modifier = Modifier.weight(1.2f))
            
            Spacer(modifier = Modifier.width(outerPadding))
            
            // Login Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .drawBehind {
                        // Technical Decorative Brackets
                        val bracketSize = with(density) { 20.dp.toPx() }
                        val strokeWidth = with(density) { 2.dp.toPx() }
                        val color = energyPathColor.copy(alpha = 0.2f)
                        
                        // Top Left
                        drawLine(color, Offset(0f, 0f), Offset(bracketSize, 0f), strokeWidth)
                        drawLine(color, Offset(0f, 0f), Offset(0f, bracketSize), strokeWidth)
                        
                        // Bottom Right
                        drawLine(color, Offset(size.width, size.height), Offset(size.width - bracketSize, size.height), strokeWidth)
                        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - bracketSize), strokeWidth)
                    }
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Responsive Sync Animation
                LoginSyncAnimation(modifier = Modifier.size(animSize))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AUTHENTICATION",
                    style = FliqTheme.typography.label.copy(
                        fontSize = 13.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Sign in to save progress",
                    style = FliqTheme.typography.body.copy(fontSize = 13.sp),
                    color = Color.White.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                GoogleTechnicalButton(
                    onClick = onGoogleSignIn,
                    isLoading = isLoading
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "By continuing, you agree to our Terms",
                    style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.2f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BrandingHero(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Soft atmospheric glow - No hard edges
            Box(
                modifier = Modifier
                    .size(width = 320.dp, height = 180.dp)
                    .blur(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            
            Image(
                painter = painterResource(id = com.fliq.core.R.drawable.logo_full_transparent),
                contentDescription = "Fliq Logo",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .graphicsLayer { 
                        shadowElevation = 0f // Removed to avoid "square" shadow boxes
                        alpha = 0.95f
                    },
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "REFLEX ARCADE SYSTEM",
            style = FliqTheme.typography.label.copy(
                fontSize = 13.sp,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun GoogleTechnicalButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f, 
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), 
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .alpha(if (isLoading) 0.7f else 1f)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color.White, Color(0xFFE2E8F0))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.Black,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "CONTINUE WITH GOOGLE",
                    style = FliqTheme.typography.label.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        letterSpacing = 0.5.sp,
                        fontSize = 14.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun LoginSyncAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Outer Glow
        Box(
            modifier = Modifier
                .size(140.dp)
                .blur(40.dp)
                .alpha(0.3f * pulse)
                .background(primaryColor, CircleShape)
        )

        Canvas(modifier = Modifier.size(200.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            
            // 1. Rotating Dashed Ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.2f),
                radius = size.width * 0.45f,
                center = center,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )
            )

            // 2. Main High-Tech Ring
            withTransform({
                rotate(rotation, center)
            }) {
                val arcSize = Size(size.width * 0.8f, size.height * 0.8f)
                val arcTopLeft = Offset(size.width * 0.1f, size.height * 0.1f)
                
                drawArc(
                    color = primaryColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = primaryColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            // 3. Counter-Rotating Inner Ring
            withTransform({
                rotate(-rotation * 1.5f, center)
            }) {
                val innerArcSize = Size(size.width * 0.6f, size.height * 0.6f)
                val innerArcTopLeft = Offset(size.width * 0.2f, size.height * 0.2f)
                
                drawArc(
                    color = primaryColor.copy(alpha = 0.5f),
                    startAngle = 45f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = innerArcTopLeft,
                    size = innerArcSize,
                    style = Stroke(width = 2f, cap = StrokeCap.Round)
                )
            }

            // 4. Center Identity Hub
            drawCircle(
                color = primaryColor.copy(alpha = 0.1f * pulse),
                radius = size.width * 0.15f,
                center = center
            )
            drawCircle(
                color = primaryColor,
                radius = 4f * pulse,
                center = center
            )
        }
    }
}

@Composable
private fun AuthBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "auth_bg")
    
    val animAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(80) {
            val randomX = Random.nextFloat() * size.width
            val randomY = Random.nextFloat() * size.height
            val randomAlpha = Random.nextFloat() * 0.3f + 0.1f
            drawCircle(
                color = Color.White.copy(alpha = randomAlpha),
                radius = Random.nextFloat() * 1.5f,
                center = Offset(randomX, randomY)
            )
        }
    }

    repeat(25) {
        val config = LocalConfiguration.current
        FloatingAuthParticle(config)
    }
}

@Composable
private fun FloatingAuthParticle(config: android.content.res.Configuration) {
    val infiniteTransition = rememberInfiniteTransition(label = "auth_particle")
    
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
fun LoginOptionsPreview() {
    FliqTheme {
        LoginOptionsView(
            isLoading = false,
            onGoogleSignIn = {}
        )
    }
}
