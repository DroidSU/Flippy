package com.sujoy.flippy

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.ui.theme.FlippyTheme

@Composable
fun GlassmorphicFlippyCard(
    // It now takes its state from the outside (ViewModel)
    isFlipped: Boolean,
    onFlip: () -> Unit, // And reports back when it's clicked
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animation for the flip
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        // A slightly bouncier spring for the flip
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 120f),
        label = "flipRotation"
    )

    // Animations for the press and lift effect
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 32.dp else 12.dp,
        // A more responsive spring for the lift
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "elevation"
    )
    val rotationX by animateFloatAsState(
        targetValue = if (isPressed) -15f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "pressRotationX"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "pressScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.rotationX = rotationX
                this.rotationY = rotation
                cameraDistance = 12 * density
            }
            .neonGlow(
                color = MaterialTheme.colorScheme.primary, // Use the primary accent color from the theme
                elevation = elevation,
                // Pass the corner radius to the glow
                cornerRadius = 24.dp // Updated to match the clip shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null // Disable ripple to use our custom feedback
            ) { onFlip() }
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation < 90f) {
                front()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    contentAlignment = Alignment.Center
                ) {
                    back()
                }
            }
        }
    }
}


fun Modifier.neonGlow(color: Color, elevation: Dp, cornerRadius: Dp): Modifier = this.drawBehind {
    val paint = Paint()
    val frameworkPaint = paint.asFrameworkPaint()
    // Increased alpha for a more visible glow/shadow
    val transparentColor = color.copy(alpha = 0.4f).toArgb()

    if (elevation > 0.dp) {
        frameworkPaint.color = transparentColor
        frameworkPaint.maskFilter = (BlurMaskFilter(
            // Use a larger blur radius for a softer, more spread-out shadow
            elevation.toPx() * 1.5f,
            BlurMaskFilter.Blur.NORMAL
        ))
        drawIntoCanvas {
            it.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                // Use the passed corner radius
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = paint
            )
        }
    }
}

// Updated Preview to use the new theme colors
@Preview(name = "Dark Theme Preview", showBackground = true)
@Composable
private fun GlassmorphicFlippyCardDarkPreview() {
    FlippyTheme(darkTheme = true) {
        // Use a Column against the theme's background to mimic the real UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Card Preview (Dark)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            GlassmorphicFlippyCard(
                isFlipped = false,
                onFlip = {},
                front = { CardContent(text = "Front") },
                back = { CardContent(text = "Back") },
                modifier = Modifier
                    .width(150.dp)
                    .aspectRatio(1f)
            )
        }
    }
}

@Preview(name = "Light Theme Preview", showBackground = true)
@Composable
private fun GlassmorphicFlippyCardLightPreview() {
    FlippyTheme(darkTheme = false) {
        // Use a Column against the theme's background to mimic the real UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Card Preview (Light)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            GlassmorphicFlippyCard(
                isFlipped = false,
                onFlip = {},
                front = { CardContent(text = "Front") },
                back = { CardContent(text = "Back") },
                modifier = Modifier
                    .width(150.dp)
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
private fun CardContent(text: String) {
    // The Card content now uses theme colors
    Card(
        modifier = Modifier.fillMaxSize(),
        // The background of the card is the theme's surface color
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface // Text color adapts to the surface
            )
        }
    }
}
