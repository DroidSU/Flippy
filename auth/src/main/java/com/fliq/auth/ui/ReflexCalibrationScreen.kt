package com.fliq.auth.ui

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.auth.viewmodel.CalibrationState
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.components.FliqSurface
import kotlin.random.Random

@Composable
fun ReflexCalibrationScreen(
    currentState: CalibrationState,
    currentTrial: Int,
    totalTrials: Int,
    lastOffset: Long,
    trials: List<Long>,
    averageOffset: Long,
    onStartCalibration: () -> Unit,
    onRecordTrial: (Long) -> Unit,
    onCalibrationComplete: (Long) -> Unit,
    onRetake: () -> Unit,
    onDismiss: () -> Unit = {},
    showCancelButton: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Dynamic sizes for landscape optimization
    val headerFontSize = (configuration.screenHeightDp * 0.06f).coerceIn(18f, 28f).sp
    val instructionSpacing = (configuration.screenHeightDp * 0.025f).coerceIn(if (isLandscape) 6f else 8f, 18f).dp

    val loopDuration = 1000L

    val isPreview = LocalInspectionMode.current

    // Tone Generator for Metronome (Using reflection to avoid ClassNotFoundException in Previews)
    val toneGenerator = remember {
        if (isPreview) null else {
            try {
                val clazz = Class.forName("android.media.ToneGenerator")
                val constructor = clazz.getConstructor(Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                constructor.newInstance(android.media.AudioManager.STREAM_MUSIC, 60)
            } catch (t: Throwable) {
                null
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            toneGenerator?.let { tg ->
                try {
                    tg.javaClass.getMethod("release").invoke(tg)
                } catch (t: Throwable) {
                    // Ignore
                }
            }
        }
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
        if (progress in 0.5f..0.6f && SystemClock.uptimeMillis() - lastBeepTime > 500) {
            if (currentState == CalibrationState.ACTIVE) {
                toneGenerator?.let { tg ->
                    try {
                        // 24 is ToneGenerator.TONE_PROP_BEEP
                        tg.javaClass.getMethod("startTone", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                            .invoke(tg, 24, 40)
                    } catch (t: Throwable) {
                        // Ignore
                    }
                }
            }
            lastBeepTime = SystemClock.uptimeMillis()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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

                        onRecordTrial(normalizedDiff)
                    }
                }
            }
    ) {
        // Deep Space Background
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isLandscape) 32.dp else 48.dp,
                    vertical = if (isLandscape) 16.dp else 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "REFLEX SYNC",
                    style = FliqTheme.typography.label.copy(
                        fontSize = 12.sp,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ACCURACY SETUP",
                    style = FliqTheme.typography.heading.copy(fontSize = headerFontSize),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(50.dp, 2.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.weight(if (isLandscape) 0.3f else 1f))

            // Main Dynamic Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isLandscape) 8f else 4f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentState,
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                    label = "state_transition",
                    modifier = Modifier.fillMaxSize()
                ) { state ->
                    when (state) {
                        CalibrationState.IDLE -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = if (isLandscape) 16.dp else 32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 32.dp else 48.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left: Mini Instructions
                                    InstructionPanel(
                                        modifier = Modifier
                                            .weight(if (isLandscape) 1.2f else 1.5f)
                                            .padding(vertical = if (isLandscape) 4.dp else 8.dp),
                                        spacing = instructionSpacing
                                    )

                                // Right: Actions
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
                                ) {
                                    Text(
                                        text = "Sync your device hardware for perfect precision.",
                                        style = FliqTheme.typography.body.copy(fontSize = 14.sp),
                                        color = Color.White.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ConfirmTechnicalButton(
                                        text = "START SYNC",
                                        onClick = onStartCalibration,
                                        isLoading = false,
                                        enabled = true
                                    )

                                    if (showCancelButton) {
                                        androidx.compose.material3.TextButton(onClick = onDismiss) {
                                            Text(
                                                "CANCEL",
                                                color = Color.White.copy(alpha = 0.3f),
                                                style = FliqTheme.typography.label.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        CalibrationState.ACTIVE -> CalibrationTrack(progress, infiniteTransition)
                        CalibrationState.FINISHED -> ResultCard(trials, averageOffset, onCalibrationComplete, onRetake)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(if (isLandscape) 0.3f else 1f))

            // Active Stats
            Box(modifier = Modifier.height(if (isLandscape) 40.dp else 60.dp), contentAlignment = Alignment.BottomCenter) {
                if (currentState == CalibrationState.ACTIVE) {
                    TrialProgressIndicator(currentTrial, totalTrials, lastOffset)
                }
            }
        }
    }
}

@Composable
fun InstructionPanel(modifier: Modifier = Modifier, spacing: Dp = 12.dp) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    FliqSurface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.03f),
        showBorder = true,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = if (isLandscape) 20.dp else 24.dp,
                    vertical = if (isLandscape) 16.dp else 20.dp
                )
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Column {
                Text(
                    text = "SETUP GUIDE",
                    style = FliqTheme.typography.label.copy(
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp, 1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }

            InstructionItem(
                icon = Icons.Default.GraphicEq,
                title = "Listen to Rhythm",
                desc = "Steady metronome beats will play."
            )

            InstructionItem(
                icon = Icons.Default.RadioButtonChecked,
                title = "Watch Pulse",
                desc = "Focus on the primary center line."
            )

            InstructionItem(
                icon = Icons.Default.Timer,
                title = "Tap on Beat",
                desc = "Tap exactly when bar hits center."
            )
        }
    }
}

@Composable
fun InstructionItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = title,
                style = FliqTheme.typography.body.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = Color.White
            )
            Text(
                text = desc,
                style = FliqTheme.typography.body.copy(fontSize = 12.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun CalibrationTrack(
    progress: Float,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val density = androidx.compose.ui.platform.LocalDensity.current
    val barGlowRadius = with(density) { 30.dp.toPx() }
    val trailLength = with(density) { 150.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        // Track Base
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)) {
            val trackHeight = 2.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = trackHeight,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
            )
        }

        // Target Zone (Center)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .background(primaryColor, CircleShape)
        )

        // Pulsing Target Glow
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "pulse"
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.radialGradient(
                        listOf(primaryColor.copy(alpha = pulseAlpha), Color.Transparent)
                    )
                )
        )

        // The Sliding Bar
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)) {
            val barWidth = 6.dp.toPx()
            val x = size.width * progress

            // Energy Trail
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, primaryColor.copy(alpha = 0.3f)),
                    startX = x - trailLength,
                    endX = x
                ),
                topLeft = Offset(x - trailLength, size.height / 2 - 2.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(trailLength, 4.dp.toPx())
            )

            // Bar
            drawRect(
                color = Color.White,
                topLeft = Offset(x - barWidth / 2, size.height / 2 - 50.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(barWidth, 100.dp.toPx())
            )

            // Bar Glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(x, size.height / 2),
                    radius = barGlowRadius
                ),
                center = Offset(x, size.height / 2),
                radius = barGlowRadius
            )
        }
    }
}

@Composable
fun ResultCard(
    trials: List<Long>,
    average: Long,
    onComplete: (Long) -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    FliqSurface(
        modifier = modifier
            .fillMaxWidth(if (isLandscape) 0.85f else 0.7f)
            .padding(vertical = if (isLandscape) 8.dp else 16.dp),
        shape = RoundedCornerShape(if (isLandscape) 20.dp else 28.dp),
        color = Color.White.copy(alpha = 0.05f),
        showBorder = true,
        elevation = 20.dp
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Left side: Status
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )

                    Text(
                        text = "SETUP COMPLETE",
                        style = FliqTheme.typography.heading.copy(fontSize = 18.sp),
                        color = Color.White
                    )

                    Text(
                        text = "Latency Neutralized. Perfect fairness achieved.",
                        style = FliqTheme.typography.body.copy(fontSize = 12.sp),
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }

                // Right side: Result and Action
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SYSTEM OFFSET",
                            style = FliqTheme.typography.label.copy(
                                fontSize = 10.sp,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${average}ms",
                            style = FliqTheme.typography.heading.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.White
                        )
                    }

                    ConfirmTechnicalButton(
                        text = "SAVE SETUP",
                        onClick = { onComplete(average) },
                        isLoading = false,
                        enabled = true
                    )

                    androidx.compose.material3.TextButton(
                        onClick = onRetake,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "RE-TAKE TEST",
                            style = FliqTheme.typography.label.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )

                    Text(
                        text = "SETUP COMPLETE",
                        style = FliqTheme.typography.heading.copy(fontSize = 20.sp),
                        color = Color.White
                    )

                    Text(
                        text = "Latency Neutralized. Perfect fairness achieved.",
                        style = FliqTheme.typography.body.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SYSTEM OFFSET",
                            style = FliqTheme.typography.label.copy(
                                fontSize = 10.sp,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${average}ms",
                            style = FliqTheme.typography.heading.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.White
                        )
                    }

                    ConfirmTechnicalButton(
                        text = "SAVE SETUP",
                        onClick = { onComplete(average) },
                        isLoading = false,
                        enabled = true
                    )

                    androidx.compose.material3.TextButton(
                        onClick = onRetake,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "RE-TAKE TEST",
                            style = FliqTheme.typography.label.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrialProgressIndicator(current: Int, total: Int, lastOffset: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(total) { index ->
                val isActive = index < current
                Box(
                    modifier = Modifier
                        .size(height = 4.dp, width = 24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.1f)
                        )
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (lastOffset != 0L) "LATENCY: ${lastOffset}ms" else "AWAITING SYNC...",
                style = FliqTheme.typography.label.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                color = Color.White.copy(alpha = 0.4f)
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

    // Pre-generate stars so they don't flicker/regenerate every frame
    val stars = remember {
        List(80) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.3f + 0.1f)
        }
    }

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
        stars.forEach { (x, y, alpha) ->
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 1.5f,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

@Composable
fun ConfirmTechnicalButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val scale by animateFloatAsState(
        if (isPressed) 0.96f else 1f,
        spring(Spring.DampingRatioMediumBouncy),
        label = "s"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isLandscape) 48.dp else 56.dp)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .background(
                if (enabled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
        } else {
            Text(
                text = text.uppercase(),
                style = FliqTheme.typography.label.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                ),
                color = if (enabled) Color.Black else Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=360dp,orientation=landscape")
@Composable
fun ReflexCalibrationScreenPreview() {
    FliqTheme {
        ReflexCalibrationScreen(
            currentState = CalibrationState.IDLE,
            currentTrial = 0,
            totalTrials = 10,
            lastOffset = 0L,
            trials = emptyList(),
            averageOffset = 0L,
            onStartCalibration = {},
            onRecordTrial = {},
            onCalibrationComplete = { _ -> },
            onRetake = {}
        )
    }
}
