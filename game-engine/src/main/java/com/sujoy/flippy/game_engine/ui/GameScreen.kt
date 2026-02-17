package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sujoy.flippy.common.Difficulty
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.theme.gameColors
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.game_engine.models.EffectState
import com.sujoy.flippy.game_engine.models.EffectType
import com.sujoy.flippy.game_engine.models.GameEffect
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.ParticleType
import com.sujoy.flippy.game_engine.models.RippleState
import com.sujoy.flippy.game_engine.models.Tile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GameScreen(
    tiles: List<Tile>,
    score: Int,
    lives: Int,
    status: GameStatus,
    difficulty: Difficulty,
    gameTime: Long,
    leaderboard: List<MatchHistory>,
    showRules: Boolean,
    isPaused: Boolean,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onRulesDismissed: (Boolean) -> Unit,
    onHelpClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onProfileIntentClicked: () -> Unit,
    onLeaderboardIntentClicked: () -> Unit,
    onPreferencesIntentClicked: () -> Unit,
    streak: Int = 0,
    reactionTime: Long = 0,
    totalTaps: Int = 0,
    correctTaps: Int = 0,
    maxStreak: Int = 0,
    totalReflexTime: Long = 0,
    effects: SharedFlow<GameEffect>? = null
) {
    var showGameOverOverlay by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val activeEffects = remember { mutableStateListOf<EffectState>() }
    val ripples = remember { mutableStateListOf<RippleState>() }
    val tilePositions = remember { mutableMapOf<Int, Offset>() }

    LaunchedEffect(effects) {
        effects?.collectLatest { effect ->
            when (effect) {
                is GameEffect.ScorePopup -> {
                    tilePositions[effect.tileId]?.let { pos ->
                        activeEffects.add(EffectState(position = pos, type = EffectType.SCORE, text = effect.score))
                    }
                }
                is GameEffect.Particle -> {
                    tilePositions[effect.tileId]?.let { pos ->
                        val type = if (effect.type == ParticleType.COIN) EffectType.PARTICLE_COIN else EffectType.PARTICLE_BOMB
                        activeEffects.add(EffectState(position = pos, type = type))
                    }
                }
                is GameEffect.BackgroundRipple -> {
                    ripples.add(RippleState(position = effect.position))
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(status) {
        if (status == GameStatus.GAME_OVER) {
            showGameOverOverlay = true
        } else if (status == GameStatus.READY) {
            showGameOverOverlay = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MeshBackground(streak = streak)

            ripples.forEach { ripple ->
                key(ripple.id) {
                    BackgroundRippleEffect(ripple.position) { ripples.remove(ripple) }
                }
            }

            if (lives == 1 && status == GameStatus.PLAYING) {
                CriticalVignette()
            }

            if (isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.gameColors.pauseDim)
                        .zIndex(5f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .blur(if (showGameOverOverlay || showRules || isMenuVisible) 20.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glassy Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(icon = Icons.Default.Menu, onClick = { isMenuVisible = true })
                    GlassIconButton(icon = Icons.AutoMirrored.Default.HelpOutline, onClick = onHelpClick)
                }

                GameHeader(
                    score = score,
                    lives = lives,
                    gameTime = gameTime,
                    isPaused = isPaused,
                    reactionTime = reactionTime
                )

                Spacer(modifier = Modifier.height(16.dp))

                DifficultySelector(
                    currentDifficulty = difficulty,
                    onDifficultyChange = onDifficultyChange,
                    enabled = status == GameStatus.READY
                )

                Spacer(modifier = Modifier.height(24.dp))

                GameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    onTilePositioned = { id, pos -> tilePositions[id] = pos },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (status == GameStatus.READY) {
                    LeaderboardSection(
                        leaderboard = leaderboard,
                        modifier = Modifier.padding(bottom = 120.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // Floating Effects
            activeEffects.toList().forEach { effect ->
                key(effect.id) {
                    when (effect.type) {
                        EffectType.SCORE -> FloatingScore(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_COIN -> SparkleEffect(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_BOMB -> BombEffect(effect) { activeEffects.remove(effect) }
                    }
                }
            }

            // Fixed Play Button at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                PlayButtonComponent(
                    status = status,
                    onAction = {
                        if (status == GameStatus.PLAYING) onResetGame() else onPlayClick()
                    }
                )
            }

            SideNavigationMenu(
                isVisible = isMenuVisible,
                onDismiss = { isMenuVisible = false },
                onSignOutClick = {
                    isMenuVisible = false
                    onSignOutClick()
                },
                onProfileIntentClicked = onProfileIntentClicked,
                onLeaderboardIntentClicked = onLeaderboardIntentClicked,
                onPreferencesIntentClicked = onPreferencesIntentClicked
            )

            if (showRules) {
                FlippyRulesDialog(onDismiss = onRulesDismissed)
            }

            GameStatusOverlay(
                visible = showGameOverOverlay,
                score = score,
                totalTaps = totalTaps,
                correctTaps = correctTaps,
                maxStreak = maxStreak,
                totalReflexTime = totalReflexTime,
                gameTime = gameTime,
                onDismiss = {
                    showGameOverOverlay = false
                    onResetGame()
                }
            )
        }
    }
}

@Composable
fun GlassIconButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        modifier = Modifier.size(52.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun BackgroundRippleEffect(position: Offset, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.gameColors.meshColor1

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1000, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.fillMaxSize().zIndex(0.1f)) {
        drawCircle(
            color = color.copy(alpha = 0.3f * (1f - progress.value)),
            radius = size.maxDimension * progress.value,
            center = position,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}

@Composable
fun CriticalVignette() {
    val infiniteTransition = rememberInfiniteTransition(label = "critical")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    0.0f to Color.Transparent,
                    1.0f to MaterialTheme.gameColors.criticalVignette.copy(alpha = alpha),
                    center = Offset.Unspecified
                )
            )
    )
}

@Composable
fun FloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch { offsetY.animateTo(-180f, animationSpec = tween(800, easing = LinearEasing)) }
        launch {
            delay(400)
            alpha.animateTo(0f, animationSpec = tween(400))
            onComplete()
        }
    }

    Text(
        text = effect.text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = 32.sp
        ),
        color = MaterialTheme.gameColors.scorePopup,
        modifier = Modifier
            .offset { IntOffset(effect.position.x.roundToInt() - 50, (effect.position.y + offsetY.value).roundToInt() - 50) }
            .alpha(alpha.value)
    )
}

@Composable
fun SparkleEffect(effect: EffectState, onComplete: () -> Unit) {
    val particles = remember { List(10) { Random.nextFloat() * 360f } }
    val progress = remember { Animatable(0f) }
    val particleColor = MaterialTheme.gameColors.particleCoin

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(500, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset { IntOffset(effect.position.x.roundToInt(), effect.position.y.roundToInt()) }) {
        particles.forEach { angle ->
            val rad = Math.toRadians(angle.toDouble())
            val dist = 120f * progress.value
            val x = (Math.cos(rad) * dist).toFloat()
            val y = (Math.sin(rad) * dist).toFloat()
            drawCircle(
                color = particleColor.copy(alpha = 1f - progress.value),
                radius = 8f * (1f - progress.value),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun BombEffect(effect: EffectState, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val shockwaveColor = MaterialTheme.gameColors.shockwave
    val particleColor = MaterialTheme.gameColors.particleBomb

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset { IntOffset(effect.position.x.roundToInt(), effect.position.y.roundToInt()) }) {
        drawCircle(
            color = shockwaveColor.copy(alpha = shockwaveColor.alpha * (1f - progress.value)),
            radius = 250f * progress.value,
            style = Stroke(width = 12f)
        )
        drawCircle(
            color = particleColor.copy(alpha = 1f - progress.value),
            radius = 60f * (1f - progress.value)
        )
    }
}

@Composable
fun MeshBackground(streak: Int = 0) {
    val isFever = streak >= 10
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )

    val gameColors = MaterialTheme.gameColors
    val color1 by animateColorAsState(targetValue = if (isFever) gameColors.feverColor1 else gameColors.meshColor1, animationSpec = tween(1200), label = "c1")
    val color2 by animateColorAsState(targetValue = if (isFever) gameColors.feverColor2 else gameColors.meshColor2, animationSpec = tween(1200), label = "c2")

    Canvas(modifier = Modifier.fillMaxSize().blur(100.dp).alpha(0.4f)) {
        drawCircle(
            color = color1.copy(alpha = 0.5f),
            radius = size.width / 1.2f,
            center = Offset(size.width / 2 + xOffset, size.height / 3)
        )
        drawCircle(
            color = color2.copy(alpha = 0.4f),
            radius = size.width / 1.5f,
            center = Offset(size.width / 4 - xOffset, size.height / 1.1f)
        )
    }
}

@Composable
private fun SideNavigationMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSignOutClick: () -> Unit,
    onProfileIntentClicked: () -> Unit,
    onLeaderboardIntentClicked: () -> Unit,
    onPreferencesIntentClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(onClick = onDismiss))
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Surface(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8f),
                shape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 12.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        text = "FLIPPY",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, letterSpacing = 6.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "The Reflex Game",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(64.dp))

                    NavigationMenuItem(Icons.Default.Person, "Profile", onProfileIntentClicked)
                    NavigationMenuItem(Icons.Default.Leaderboard, "Leaderboard", onLeaderboardIntentClicked)
                    NavigationMenuItem(Icons.Default.Settings, "Preferences", onPreferencesIntentClicked)

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    NavigationMenuItem(Icons.AutoMirrored.Filled.Logout, "Sign Out", onSignOutClick, color = Color(0xFFFF3366))
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun NavigationMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, color: Color = MaterialTheme.colorScheme.onSurface) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.9f), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
fun PlayButtonComponent(status: GameStatus, onAction: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "scale")
    val color by animateColorAsState(targetValue = if (status == GameStatus.PLAYING) Color(0xFFFF3366) else MaterialTheme.colorScheme.primary, label = "color")

    Surface(
        modifier = modifier.size(76.dp).scale(scale).shadow(elevation = if (isPressed) 6.dp else 16.dp, shape = CircleShape, spotColor = color),
        shape = CircleShape,
        color = color,
        onClick = onAction,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (status == GameStatus.PLAYING) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun LeaderboardSection(leaderboard: List<MatchHistory>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            text = "TOP PERFORMANCES",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            if (leaderboard.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No records found", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                Column(modifier = Modifier.padding(12.dp)) {
                    leaderboard.take(3).forEachIndexed { index, match ->
                        LeaderboardItem(index + 1, match)
                        if (index < leaderboard.size - 1) Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultySelector(currentDifficulty: Difficulty, onDifficultyChange: (Difficulty) -> Unit, enabled: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(if (enabled) 1f else 0.5f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Difficulty.entries.forEach { diff ->
            val isSelected = currentDifficulty == diff
            Surface(
                modifier = Modifier.weight(1f).height(48.dp).clickable(enabled = enabled) { onDifficultyChange(diff) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.05f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(diff.label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold), color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun GameHeader(score: Int, lives: Int, gameTime: Long, isPaused: Boolean = false, reactionTime: Long = 0) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseColor by infiniteTransition.animateColor(initialValue = MaterialTheme.colorScheme.secondary, targetValue = MaterialTheme.gameColors.pausePulse, animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse), label = "color")

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("SCORE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.primary)
                Text("$score", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (reactionTime > 0) {
                    Text("${reactionTime / 1000f}s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = MaterialTheme.gameColors.scorePopup))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp), tint = if (isPaused) pulseColor else MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(6.dp))
                    Text(UtilityMethods.formatTime(gameTime), style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    Icon(Icons.Default.Favorite, null, tint = if (index < lives) Color(0xFFFF3366) else Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun GameGrid(tiles: List<Tile>, onTileTapped: (Int, Offset?) -> Unit, onTilePositioned: (Int, Offset) -> Unit, modifier: Modifier = Modifier) {
    val columns = 4
    val rows = (tiles.size + columns - 1) / columns

    Column(modifier = modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (i in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < tiles.size) {
                        val tile = tiles[index]
                        var tileCenter by remember { mutableStateOf(Offset.Zero) }
                        GameCard(
                            isRevealed = tile.isRevealed,
                            type = tile.type,
                            onClick = { onTileTapped(tile.id, tileCenter) },
                            modifier = Modifier.weight(1f).aspectRatio(1f).onGloballyPositioned { coords ->
                                val center = Offset(coords.positionInRoot().x + coords.size.width / 2, coords.positionInRoot().y + coords.size.height / 2)
                                tileCenter = center
                                onTilePositioned(tile.id, center)
                            }
                        )
                    } else { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
