package com.fliq.auth.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.ui.MeshBackground
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class CalibrationState {
    IDLE, WAITING, ACTIVE, RESULT, FINISHED
}

@Composable
fun ReflexCalibrationScreen(
    onCalibrationComplete: (Long) -> Unit
) {
    var currentState by remember { mutableStateOf(CalibrationState.IDLE) }
    var currentTrial by remember { mutableIntStateOf(0) }
    val trials = remember { mutableStateListOf<Long>() }
    var startTime by remember { mutableLongStateOf(0L) }
    var lastResult by remember { mutableLongStateOf(0L) }

    val gameColors = MaterialTheme.gameColors
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val standbyAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "alpha"
    )

    val targetScale by animateFloatAsState(
        targetValue = if (currentState == CalibrationState.ACTIVE) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "scale"
    )

    val targetColor by animateColorAsState(
        targetValue = when (currentState) {
            CalibrationState.ACTIVE -> NeonCyan
            CalibrationState.RESULT -> Color.White
            else -> BgSlate.copy(alpha = 0.5f)
        },
        animationSpec = tween(if (currentState == CalibrationState.ACTIVE) 50 else 300),
        label = "color"
    )

    // Trial Logic
    LaunchedEffect(currentState) {
        if (currentState == CalibrationState.WAITING) {
            delay(Random.nextLong(1500, 4000))
            startTime = System.currentTimeMillis()
            currentState = CalibrationState.ACTIVE
        } else if (currentState == CalibrationState.RESULT) {
            delay(1000)
            if (currentTrial < 3) {
                currentState = CalibrationState.WAITING
            } else {
                currentState = CalibrationState.FINISHED
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient))
    ) {
        MeshBackground()

        // Scanline Animation (IDLE Only)
        if (currentState == CalibrationState.IDLE) {
            val scanTransition = rememberInfiniteTransition(label = "scan")
            val scanY by scanTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Restart),
                label = "scanY"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = size.height * scanY }
                    .height(2.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, NeonCyan.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header System
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "REFLEX CALIBRATION",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .background(
                            if (currentState == CalibrationState.ACTIVE) NeonCyan.copy(alpha = 0.2f)
                            else Color.White.copy(alpha = 0.05f),
                            MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (currentState) {
                            CalibrationState.IDLE -> "READY TO START"
                            CalibrationState.WAITING -> "GET READY..."
                            CalibrationState.ACTIVE -> "TAP NOW!"
                            CalibrationState.RESULT -> "RESULT: ${lastResult}ms"
                            CalibrationState.FINISHED -> "CALIBRATION SAVED"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = if (currentState == CalibrationState.ACTIVE) NeonCyan else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (currentState == CalibrationState.IDLE) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "HOW TO PLAY",
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = NeonCyan
                        )
                        Text(
                            text = "Calibrate your reaction time to ensure high-speed performance and fair gameplay.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 20.sp
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        InstructionRow(number = "01", text = "Tap START to begin the calibration.")
                        InstructionRow(number = "02", text = "Focus on the central hexagon.")
                        InstructionRow(number = "03", text = "Tap as soon as it flashes Cyan.")
                        InstructionRow(number = "04", text = "Repeat 3 times to get your average.", isLast = true)
                    }
                }
            }

            if (currentState != CalibrationState.IDLE) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TEST PROGRESS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            TrialPip(
                                isCompleted = index < currentTrial,
                                isActive = index == currentTrial && currentState != CalibrationState.IDLE
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // The Target
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = targetScale
                        scaleY = targetScale
                    },
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(40.dp)
                        .alpha(if (currentState == CalibrationState.ACTIVE) 0.8f else standbyAlpha * 0.4f)
                        .background(targetColor, HexagonShape)
                )

                // Main Target
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(HexagonShape)
                        .background(
                            Brush.radialGradient(
                                listOf(targetColor.copy(alpha = 0.9f), targetColor.copy(alpha = 0.4f))
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = currentState == CalibrationState.ACTIVE || currentState == CalibrationState.IDLE
                        ) {
                            if (currentState == CalibrationState.IDLE) {
                                currentState = CalibrationState.WAITING
                            } else if (currentState == CalibrationState.ACTIVE) {
                                val reaction = System.currentTimeMillis() - startTime
                                trials.add(reaction)
                                lastResult = reaction
                                currentTrial++
                                currentState = CalibrationState.RESULT
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (currentState == CalibrationState.IDLE) {
                        Text(
                            text = "START",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Pulsing Ring (Standby/IDLE)
                if (currentState == CalibrationState.WAITING || currentState == CalibrationState.IDLE) {
                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by pulseTransition.animateFloat(
                        initialValue = if (currentState == CalibrationState.IDLE) 0.9f else 0.8f,
                        targetValue = if (currentState == CalibrationState.IDLE) 1.1f else 1.2f,
                        animationSpec = infiniteRepeatable(
                            tween(if (currentState == CalibrationState.IDLE) 1200 else 1500), 
                            RepeatMode.Restart
                        ),
                        label = "p"
                    )
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            tween(if (currentState == CalibrationState.IDLE) 1200 else 1500), 
                            RepeatMode.Restart
                        ),
                        label = "a"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawPath(
                            path = getHexagonPath(size, pulseScale),
                            color = NeonCyan.copy(alpha = pulseAlpha),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(20f, 10f), 0f
                                )
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.2f))

            // Result Action
            if (currentState == CalibrationState.FINISHED) {
                val average = trials.average().toLong()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AVERAGE REFLEX",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${average}ms",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ConfirmKineticButton(
                        onClick = { onCalibrationComplete(average) },
                        isLoading = false,
                        enabled = true
                    )
                }
            } else if (currentState != CalibrationState.IDLE) {
                Text(
                    text = "STAY FOCUSED",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 4.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InstructionRow(number: String, text: String, isLast: Boolean = false) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.height(60.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(HexagonShape)
                    .background(NeonCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Black
                )
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .weight(1f)
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TrialPip(isCompleted: Boolean, isActive: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isCompleted) NeonCyan else if (isActive) Color.White else Color.White.copy(alpha = 0.1f),
        label = "color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.3f else 1.0f,
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(18.dp)
            .scale(scale)
            .clip(HexagonShape)
            .background(color)
            .padding(2.dp)
            .background(if (isActive) Color.Black else Color.Transparent, HexagonShape)
    )
}

val HexagonShape = GenericShape { size, _ ->
    val path = getHexagonPath(size)
    addPath(path)
}

fun getHexagonPath(size: Size, scale: Float = 1f): Path {
    val path = Path()
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = (size.width / 2) * scale
    
    for (i in 0..5) {
        val angle = Math.toRadians(i * 60.0 - 30.0)
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Preview(showBackground = true)
@Composable
fun ReflexCalibrationScreenPreview() {
    FliqTheme {
        ReflexCalibrationScreen(
            onCalibrationComplete = {_ ->}
        )
    }
}