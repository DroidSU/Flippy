package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
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
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sujoy.flippy.common.Difficulty
import com.sujoy.flippy.common.UtilityMethods.Companion.formatTime
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.core.theme.gameColors
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.game_engine.R
import com.sujoy.flippy.game_engine.models.CardType
import com.sujoy.flippy.game_engine.models.EffectState
import com.sujoy.flippy.game_engine.models.EffectType
import com.sujoy.flippy.game_engine.models.GameEffect
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.GameStatus.GAME_OVER
import com.sujoy.flippy.game_engine.models.GameStatus.PLAYING
import com.sujoy.flippy.game_engine.models.GameStatus.READY
import com.sujoy.flippy.game_engine.models.ParticleType
import com.sujoy.flippy.game_engine.models.RippleState
import com.sujoy.flippy.game_engine.models.Tile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
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
    onChallengeModeClick: () -> Unit,
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

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(effects) {
        effects?.collectLatest { effect ->
            when (effect) {
                is GameEffect.ScorePopup -> {
                    tilePositions[effect.tileId]?.let { pos ->
                        activeEffects.add(
                            EffectState(
                                position = pos,
                                type = EffectType.SCORE,
                                text = effect.score
                            )
                        )
                    }
                }

                is GameEffect.Particle -> {
                    tilePositions[effect.tileId]?.let { pos ->
                        val type =
                            if (effect.type == ParticleType.COIN) EffectType.PARTICLE_COIN else EffectType.PARTICLE_BOMB
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
        if (status == GAME_OVER) {
            showGameOverOverlay = true
        } else if (status == READY) {
            showGameOverOverlay = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(backgroundColor, surfaceColor)
                    )
                )
        ) {
            FluidBackground(streak = streak, isChallenge = false)

            ripples.forEach { ripple ->
                key(ripple.id) {
                    BackgroundRippleEffect(ripple.position) { ripples.remove(ripple) }
                }
            }

            if (lives == 1 && status == PLAYING) {
                CriticalVignette()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .blur(if (showGameOverOverlay || showRules || isMenuVisible) 25.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderNavigation(
                    onMenuClick = { isMenuVisible = true },
                    onHelpClick = onHelpClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatsDashboard(
                    score = score,
                    lives = lives,
                    gameTime = gameTime,
                    reactionTime = reactionTime,
                    isPaused = isPaused
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if(status == READY) {
                            ChallengeModeTrigger(
                                enabled = status == READY,
                                onClick = onChallengeModeClick
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        NeoDifficultySelector(
                            currentDifficulty = difficulty,
                            onDifficultyChange = onDifficultyChange,
                            enabled = status == READY
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                ImmersiveGameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    onTilePositioned = { id, pos -> tilePositions[id] = pos }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
            ) {
                activeEffects.toList().forEach { effect ->
                    key(effect.id) {
                        when (effect.type) {
                            EffectType.SCORE -> FloatingScore(effect) { activeEffects.remove(effect) }
                            EffectType.PARTICLE_COIN -> SparkleEffect(effect) {
                                activeEffects.remove(
                                    effect
                                )
                            }

                            EffectType.PARTICLE_BOMB -> BombEffect(effect) {
                                activeEffects.remove(
                                    effect
                                )
                            }
                        }
                    }
                }
            }

            MainActionButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp + innerPadding.calculateBottomPadding()),
                status = status,
                onAction = {
                    if (status == PLAYING) onResetGame() else onPlayClick()
                }
            )

            SideNavigationMenu(
                isVisible = isMenuVisible,
                onDismiss = { isMenuVisible = false },
                onSignOutClick = {
                    isMenuVisible = false
                    onSignOutClick()
                },
                onProfileIntentClicked = onProfileIntentClicked,
                onLeaderboardIntentClicked = onLeaderboardIntentClicked,
                onPreferencesIntentClicked = onPreferencesIntentClicked,
                onChallengeModeClick = {
                    isMenuVisible = false
                    onChallengeModeClick()
                }
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
fun ChallengeModeTrigger(enabled: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "glow"
    )
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(if (enabled) glowScale else 1f)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = tertiaryColor.copy(alpha = 0.1f),
        border = BorderStroke(2.dp, tertiaryColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Row(verticalAlignment = CenterVertically) {
                Icon(
                    Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = tertiaryColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "CHALLENGE PROTOCOL",
                        style = typography.titleMedium.copy(
                            fontWeight = Black,
                            letterSpacing = 2.sp
                        ),
                        color = tertiaryColor
                    )
                    Text(
                        "High Stakes • Triple Rewards",
                        style = typography.labelSmall,
                        color = tertiaryColor.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(
                Default.PlayArrow,
                contentDescription = null,
                tint = tertiaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FluidBackground(streak: Int, isChallenge: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "fluid")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                if (isChallenge) 12000 else 20000,
                easing = LinearEasing
            )
        ),
        label = "phase"
    )

    val gameColors = MaterialTheme.gameColors
    val color1 = if (isChallenge) gameColors.criticalVignette.copy(alpha = 0.12f) else gameColors.meshColor1.copy(alpha = 0.12f)
    val color2 = gameColors.feverColor2.copy(alpha = 0.08f)
    val feverColor = gameColors.particleBomb.copy(alpha = 0.2f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val path = Path()
        val segments = 10
        val segmentWidth = width / segments

        for (i in 0..segments) {
            val x = i * segmentWidth
            val yOffset = sin(Math.toRadians((phase + i * 36).toDouble())).toFloat() * 50f
            if (i == 0) path.moveTo(x, height * 0.65f + yOffset)
            else path.lineTo(x, height * 0.65f + yOffset)
        }
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    if (streak >= 10) feverColor else color1,
                    Color.Transparent
                )
            )
        )

        repeat(3) { i ->
            val orbPhase = phase + i * 120
            val orbX =
                width / 2 + cos(Math.toRadians(orbPhase.toDouble())).toFloat() * (width * 0.35f)
            val orbY =
                height / 2 + sin(Math.toRadians((orbPhase * 0.7).toDouble())).toFloat() * (height * 0.25f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color2, Color.Transparent),
                    center = Offset(orbX, orbY),
                    radius = 450f
                ),
                radius = 450f,
                center = Offset(orbX, orbY)
            )
        }
    }
}

@Composable
fun HeaderNavigation(onMenuClick: () -> Unit, onHelpClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        NeoGlassButton(icon = Default.Menu, onClick = onMenuClick)
        Text(
            text = "FLIPPY",
            style = typography.headlineMedium.copy(
                fontWeight = Black,
                letterSpacing = 8.sp,
                fontFamily = Monospace
            ),
            color = primaryColor
        )
        NeoGlassButton(icon = Icons.AutoMirrored.Default.HelpOutline, onClick = onHelpClick)
    }
}

@Composable
fun NeoGlassButton(icon: ImageVector, onClick: () -> Unit) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = onSurfaceColor.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, onSurfaceColor.copy(alpha = 0.1f)),
        modifier = Modifier.size(52.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = onSurfaceColor)
        }
    }
}

@Composable
fun StatsDashboard(
    score: Int,
    lives: Int,
    gameTime: Long,
    reactionTime: Long,
    isPaused: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(32.dp),
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = SpaceEvenly
        ) {
            StatItem(
                label = stringResource(R.string.score),
                value = score.toString(),
                color = colorScheme.primary
            )
            VerticalDivider()
            StatItem(
                label = "DURATION",
                value = formatTime(gameTime),
                color = if (isPaused) Color(0xFFFACC15) else colorScheme.secondary,
                subValue = if (reactionTime > 0) "${reactionTime / 1000f}s" else null
            )
            VerticalDivider()
            StatItem(
                label = stringResource(R.string.lives),
                value = lives.toString(),
                color = colorScheme.tertiary,
                icon = Default.Favorite
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector? = null,
    subValue: String? = null
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = typography.labelSmall.copy(letterSpacing = 2.sp),
            color = onSurfaceColor.copy(alpha = 0.4f)
        )
        Row(verticalAlignment = CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = value,
                style = typography.titleLarge.copy(
                    fontWeight = Black,
                    fontFamily = Monospace,
                    fontSize = 22.sp
                ),
                color = color
            )
        }
        if (subValue != null) {
            Text(
                subValue,
                style = typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = ExtraBold
                ),
                color = color.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun VerticalDivider() {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(50.dp)
            .background(onSurfaceColor.copy(alpha = 0.1f))
    )
}

@Composable
fun NeoDifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    enabled: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Difficulty.entries.forEach { diff ->
            val isSelected = currentDifficulty == diff
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clickable(enabled = enabled) { onDifficultyChange(diff) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) colorScheme.secondary else Color.Transparent,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color.Transparent else colorScheme.onSurface.copy(alpha = 0.1f)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = diff.label,
                        style = typography.labelMedium.copy(fontWeight = ExtraBold),
                        color = if (isSelected) colorScheme.onSecondary else colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ImmersiveGameGrid(
    tiles: List<Tile>,
    onTileTapped: (Int, Offset?) -> Unit,
    onTilePositioned: (Int, Offset) -> Unit
) {
    val columns = 4
    val rows = (tiles.size + columns - 1) / columns

    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        for (i in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < tiles.size) {
                        val tile = tiles[index]
                        var tileCenter by remember { mutableStateOf(Offset.Zero) }

                        GameCard(
                            isRevealed = tile.isRevealed,
                            type = tile.type,
                            onClick = { onTileTapped(tile.id, tileCenter) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .onGloballyPositioned { coords ->
                                    val center = Offset(
                                        coords.positionInRoot().x + coords.size.width / 2,
                                        coords.positionInRoot().y + coords.size.height / 2
                                    )
                                    tileCenter = center
                                    onTilePositioned(tile.id, center)
                                }
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MainActionButton(
    modifier: Modifier,
    status: GameStatus,
    onAction: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, label = "scale")

    val colorScheme = MaterialTheme.colorScheme
    val color = if (status == PLAYING) colorScheme.tertiary else colorScheme.primary
    val glowColor = color.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(scale)
            .shadow(28.dp, CircleShape, spotColor = glowColor)
            .clip(CircleShape)
            .background(color)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onAction
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = status,
            transitionSpec = {
                (scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeOut())
            },
            label = "action_icon"
        ) { targetStatus ->
            Icon(
                imageVector = if (targetStatus == PLAYING) Default.Stop else Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun BackgroundRippleEffect(position: Offset, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1600, easing = LinearEasing))
        onComplete()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0.1f)
    ) {
        drawCircle(
            color = color.copy(alpha = 0.18f * (1f - progress.value)),
            radius = size.maxDimension * 0.85f * progress.value,
            center = position,
            style = Stroke(width = 1.dp.toPx())
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

    val vignetteColor = MaterialTheme.gameColors.criticalVignette

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    0.0f to Color.Transparent,
                    1.0f to vignetteColor.copy(alpha = alpha),
                    center = Offset.Unspecified
                )
            )
    )
}

@Composable
fun FloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scoreColor = MaterialTheme.gameColors.scorePopup

    LaunchedEffect(Unit) {
        launch { offsetY.animateTo(-240f, animationSpec = tween(1100, easing = LinearEasing)) }
        launch {
            delay(700)
            alpha.animateTo(0f, animationSpec = tween(400))
            onComplete()
        }
    }

    Text(
        text = effect.text,
        style = typography.displaySmall.copy(
            fontWeight = Black,
            letterSpacing = 3.sp
        ),
        color = scoreColor,
        modifier = Modifier
            .offset {
                IntOffset(
                    effect.position.x.roundToInt() - 65,
                    (effect.position.y + offsetY.value).roundToInt() - 65
                )
            }
            .alpha(alpha.value)
    )
}

@Composable
fun SparkleEffect(effect: EffectState, onComplete: () -> Unit) {
    val particles = remember { List(18) { Random.nextFloat() * 360f } }
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.gameColors.particleCoin

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(900, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset {
        IntOffset(
            effect.position.x.roundToInt(),
            effect.position.y.roundToInt()
        )
    }) {
        particles.forEach { angle ->
            val rad = Math.toRadians(angle.toDouble())
            val dist = 180f * progress.value
            val x = (cos(rad) * dist).toFloat()
            val y = (sin(rad) * dist).toFloat()
            drawCircle(
                color = color.copy(alpha = 1f - progress.value),
                radius = 8f * (1f - progress.value),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun BombEffect(effect: EffectState, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.gameColors.particleBomb
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(900, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset {
        IntOffset(
            effect.position.x.roundToInt(),
            effect.position.y.roundToInt()
        )
    }) {
        drawCircle(
            color = color.copy(alpha = 0.65f * (1f - progress.value)),
            radius = 380f * progress.value,
            style = Stroke(width = 12f)
        )
        drawCircle(
            color = onSurfaceColor.copy(alpha = 1f - progress.value),
            radius = 120f * (1f - progress.value)
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
    onPreferencesIntentClicked: () -> Unit,
    onChallengeModeClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onDismiss)
            )
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.85f),
                shape = RoundedCornerShape(topEnd = 44.dp, bottomEnd = 44.dp),
                color = colorScheme.surface,
                tonalElevation = 28.dp,
                border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(36.dp)
                ) {
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        text = "FLIPPY",
                        style = typography.displaySmall.copy(
                            fontWeight = Black,
                            letterSpacing = 10.sp
                        ),
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(64.dp))

                    NavigationMenuItem(Default.Person, "Identity", onProfileIntentClicked)
                    NavigationMenuItem(
                        Default.Leaderboard,
                        "Hall of Fame",
                        onLeaderboardIntentClicked
                    )
                    NavigationMenuItem(
                        Default.Settings,
                        "Core Systems",
                        onPreferencesIntentClicked
                    )
                    NavigationMenuItem(
                        Default.LocalFireDepartment,
                        "Challenge Protocol",
                        onChallengeModeClick,
                        color = colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.04f))
                    NavigationMenuItem(
                        Icons.AutoMirrored.Filled.Logout,
                        "Decommission",
                        onSignOutClick,
                        color = colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun NavigationMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(22.dp), verticalAlignment = CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = color.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = label,
                style = typography.titleMedium.copy(fontWeight = ExtraBold),
                color = color
            )
        }
    }
}

@Composable
private fun ImmersiveLeaderboardSection(
    leaderboard: List<MatchHistory>,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.top_performances),
            style = typography.labelLarge.copy(
                letterSpacing = 4.sp,
                fontWeight = Black
            ),
            color = colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.06f))
        ) {
            if (leaderboard.isEmpty()) {
                Box(modifier = Modifier.padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("No transmissions intercepted", color = colorScheme.onSurface.copy(alpha = 0.25f))
                }
            } else {
                Column(modifier = Modifier.padding(20.dp)) {
                    leaderboard.take(3).forEachIndexed { index, match ->
                        ImmersiveRankItem(match, index)
                        if (index < 2 && index < leaderboard.size - 1) Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ImmersiveRankItem(match: MatchHistory, index: Int) {
    val colorScheme = MaterialTheme.colorScheme
    val rankColor = when (index) {
        0 -> Color(0xFFFACC15)
        1 -> Color(0xFF94A3B8)
        else -> Color(0xFFB45309)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(rankColor.copy(alpha = 0.25f), CircleShape)
                .border(1.5.dp, rankColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (index + 1).toString(),
                style = typography.labelLarge.copy(fontWeight = Black),
                color = rankColor
            )
        }
        Spacer(Modifier.width(20.dp))
        Text(
            text = match.username,
            style = typography.bodyLarge.copy(fontWeight = ExtraBold),
            color = colorScheme.onSurface
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = match.score.toString(),
            style = typography.titleLarge.copy(
                fontWeight = Black,
                fontFamily = Monospace
            ),
            color = colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    FlippyTheme {
        GameScreen(
            tiles = List(16) { Tile(it, CardType.COIN) },
            score = 2450,
            lives = 3,
            status = READY,
            difficulty = Difficulty.NORMAL,
            gameTime = 60000,
            leaderboard = emptyList(),
            showRules = false,
            isPaused = false,
            onTileTapped = { _, _ -> },
            onPlayClick = {},
            onResetGame = {},
            onDifficultyChange = {},
            onChallengeModeClick = {},
            onRulesDismissed = {},
            onHelpClick = {},
            onSignOutClick = {},
            onProfileIntentClicked = {},
            onLeaderboardIntentClicked = {},
            onPreferencesIntentClicked = {}
        )
    }
}
