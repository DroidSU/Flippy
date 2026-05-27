package com.fliq.auth.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.ui.MeshBackground

enum class CalibrationState {
    IDLE, ACTIVE, FINISHED
}

@Composable
fun ReflexCalibrationScreen(
    onCalibrationComplete: (Long) -> Unit,
    onDismiss: () -> Unit = {},
    showCancelButton: Boolean = true
) {
    var currentState by remember { mutableStateOf(CalibrationState.IDLE) }
    var currentTrial by remember { mutableIntStateOf(0) }
    val trials = remember { mutableStateListOf<Long>() }
    var lastOffset by remember { mutableLongStateOf(0L) }
    
    val gameColors = MaterialTheme.gameColors
    val totalTrials = 10
    val loopDuration = 1000L

    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    
    // Tone Generator for Metronome (Disabled in Preview)
    val toneGenerator = remember { 
        if (isPreview) null else ToneGenerator(AudioManager.STREAM_MUSIC, 60)
    }
    DisposableEffect(Unit) {
        onDispose { toneGenerator?.release() }
    }

    // Animation for the sliding bar
    val infiniteTransition = rememberInfiniteTransition(label = "calibration")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(loopDuration.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    var lastBeepTime by remember { mutableLongStateOf(0L) }
    LaunchedEffect(progress) {
        // We trigger a beep when the bar is at the center (0.5)
        if (progress in 0.5f..<0.6f && SystemClock.uptimeMillis() - lastBeepTime > 500) {
            if (currentState == CalibrationState.ACTIVE) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
            }
            lastBeepTime = SystemClock.uptimeMillis()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient))
            .pointerInput(currentState) {
                if (currentState == CalibrationState.ACTIVE) {
                    detectTapGestures {
                        val tapTime = SystemClock.uptimeMillis()
                        val cycleStartTime = tapTime - (progress * loopDuration).toLong()
                        val targetTime = cycleStartTime + (loopDuration / 2)
                        val diff = tapTime - targetTime
                        
                        val normalizedDiff = when {
                            diff > 500 -> diff - 1000
                            diff < -500 -> diff + 1000
                            else -> diff
                        }

                        trials.add(normalizedDiff)
                        lastOffset = normalizedDiff
                        currentTrial++

                        if (currentTrial >= totalTrials) {
                            currentState = CalibrationState.FINISHED
                        }
                    }
                }
            }
    ) {
        MeshBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header System
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = NeonCyan.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "NEURAL SYNC ENGINE",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = NeonCyan
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "HARDWARE CALIBRATION",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Main Dynamic Content
            AnimatedContent(
                targetState = currentState,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                label = "state_transition"
            ) { state ->
                when (state) {
                    CalibrationState.IDLE -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            InstructionCard()
                            Spacer(modifier = Modifier.height(42.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (showCancelButton) {
                                    androidx.compose.material3.TextButton(onClick = onDismiss) {
                                        Text(
                                            "CANCEL",
                                            color = Color.White.copy(alpha = 0.5f),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                                ConfirmKineticButton(
                                    text = "START CALIBRATION",
                                    onClick = { currentState = CalibrationState.ACTIVE },
                                    isLoading = false,
                                    enabled = true
                                )
                            }
                        }
                    }
                    CalibrationState.ACTIVE -> CalibrationTrack(progress, infiniteTransition)
                    CalibrationState.FINISHED -> ResultCard(trials, onCalibrationComplete)
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Active Stats
            if (currentState == CalibrationState.ACTIVE) {
                TrialProgressIndicator(currentTrial, totalTrials, lastOffset)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InstructionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Follow these steps to ensure perfect touch synchronization:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        InstructionItem(
            icon = Icons.Default.GraphicEq,
            title = "Listen to the Rhythm",
            desc = "A metronome will play a steady beat. Use speakers or wired headphones for best results."
        )

        InstructionItem(
            icon = Icons.Default.RadioButtonChecked,
            title = "Watch the Pulse",
            desc = "A white bar will slide across the screen. Focus on the Cyan center line."
        )

        InstructionItem(
            icon = Icons.Default.Timer,
            title = "Tap on the Beat",
            desc = "Tap exactly when the white bar hits the center line. We will take 10 samples."
        )
    }
}

@Composable
fun InstructionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CalibrationTrack(progress: Float, infiniteTransition: androidx.compose.animation.core.InfiniteTransition) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Track Base
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val trackHeight = 2.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = trackHeight,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Target Zone (Center)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(NeonCyan, CircleShape)
            )
        }
        
        // Pulsing Target Glow
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "pulse"
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(NeonCyan.copy(alpha = pulseAlpha), Color.Transparent)
                    )
                )
        )

        // The Sliding Bar
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val barWidth = 8.dp.toPx()
            val x = size.width * progress
            
            // Neon Trail
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.4f)),
                    startX = x - 120.dp.toPx(),
                    endX = x
                ),
                topLeft = Offset(x - 120.dp.toPx(), size.height / 2 - 2.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(120.dp.toPx(), 4.dp.toPx())
            )

            // Bar
            drawRect(
                color = Color.White,
                topLeft = Offset(x - barWidth / 2, size.height / 2 - 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(barWidth, 80.dp.toPx())
            )
            
            // Bar Glow
            drawRect(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(x, size.height / 2),
                    radius = 20.dp.toPx()
                ),
                topLeft = Offset(x - 20.dp.toPx(), size.height / 2 - 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 80.dp.toPx())
            )
        }
    }
}

@Composable
fun ResultCard(trials: List<Long>, onComplete: (Long) -> Unit) {
    val average = trials.filter { it in -200..300 }.average().toLong().coerceAtLeast(0L)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.RadioButtonChecked,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = "CALIBRATION COMPLETE",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Your device latency has been measured and neutralized. The game engine will now account for this delay to ensure perfect fairness.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SYSTEM OFFSET",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan
            )
            Text(
                text = "${average}ms",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ConfirmKineticButton(
            text = "SAVE & CONTINUE",
            onClick = { onComplete(average) },
            isLoading = false,
            enabled = true
        )
    }
}

@Composable
fun TrialProgressIndicator(current: Int, total: Int, lastOffset: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(total) { index ->
                Box(
                    modifier = Modifier
                        .size(height = 6.dp, width = 20.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < current) NeonCyan 
                            else Color.White.copy(alpha = 0.1f)
                        )
                )
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(14.dp))
            Text(
                text = if (lastOffset != 0L) "Last Sync: ${lastOffset}ms" else "Waiting for input...",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ConfirmKineticButton(
    text: String = "CONFIRM",
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean
) {
    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        modifier = Modifier.wrapContentSize(align = Alignment.Center)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 30.dp, vertical = 15.dp)) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
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
