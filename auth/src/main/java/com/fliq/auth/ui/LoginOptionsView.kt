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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.auth.R
import com.fliq.core.theme.gameColors
import com.fliq.core.util.ChamferedCornerShape
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoginOptionsView(
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient)),
        contentAlignment = Alignment.Center
    ) {
        // Static mesh background
        Box(modifier = Modifier.fillMaxSize().alpha(0.05f)) {
            val meshColor = MaterialTheme.colorScheme.primary
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 40.dp.toPx()
                for (x in 0..size.width.toInt() step step.toInt()) {
                    drawLine(meshColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1.dp.toPx())
                }
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawLine(meshColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1.dp.toPx())
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            GeometricHero()
            Spacer(modifier = Modifier.height(48.dp))
            BrandingHeader()
            Spacer(modifier = Modifier.height(80.dp))
            GoogleKineticButton(
                onClick = onGoogleSignIn,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun GeometricHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "rot"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(160.dp)
            .scale(scale)
            .graphicsLayer { rotationZ = rotation }
    ) {
        Icon(
            imageVector = Icons.Default.Hexagon,
            contentDescription = null,
            modifier = Modifier.size(160.dp).alpha(0.1f),
            tint = MaterialTheme.colorScheme.primary
        )
        Icon(
            imageVector = Icons.Default.ChangeHistory,
            contentDescription = null,
            modifier = Modifier.size(100.dp).alpha(0.2f).graphicsLayer { rotationZ = -rotation * 2 },
            tint = MaterialTheme.colorScheme.tertiary
        )
        Surface(
            modifier = Modifier.size(72.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent))),
            shadowElevation = 32.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = com.fliq.core.R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    alpha = 0.95f
                )
            }
        }
    }
}

@Composable
fun BrandingHeader() {
    var displayTitle by remember { mutableStateOf("") }
    val fullTitle = "FLIQ"

    LaunchedEffect(Unit) {
        delay(500)
        fullTitle.forEach { char ->
            delay(Random.nextLong(50, 150))
            displayTitle += char
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = displayTitle,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 72.sp,
                letterSpacing = 4.sp,
                shadow = Shadow(Color.Black.copy(alpha = 0.5f), offset = Offset(0f, 8.dp.value), blurRadius = 16f)
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ARCADE REFLEX CHALLENGE",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun GoogleKineticButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "scale")
    val zOffset by animateFloatAsState(if (isPressed) 0f else 6.dp.value, label = "z")

    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(56.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .alpha(0.4f),
            shape = ChamferedCornerShape(16.dp),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = -zOffset },
            shape = ChamferedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(
                1.5.dp, 
                Brush.linearGradient(listOf(Color.White, Color(0xFFE2E8F0)))
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF0F172A),
                        strokeWidth = 3.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "SIGN IN WITH GOOGLE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}
