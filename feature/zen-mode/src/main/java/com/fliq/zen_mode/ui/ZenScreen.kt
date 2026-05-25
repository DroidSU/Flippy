package com.fliq.zen_mode.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.HeartRed
import com.fliq.core.theme.gameColors
import com.fliq.core.util.ChamferedCornerShape
import com.fliq.game_engine.R
import com.fliq.game_engine.models.CardType
import com.fliq.game_engine.models.EffectState
import com.fliq.game_engine.models.EffectType
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.ParticleType
import com.fliq.game_engine.models.RippleState
import com.fliq.game_engine.models.Tile
import com.fliq.game_engine.ui.AdRewardDialog
import com.fliq.game_engine.ui.BombEffect
import com.fliq.game_engine.ui.CriticalVignette
import com.fliq.game_engine.ui.MeshBackground
import com.fliq.game_engine.ui.SparkleEffect
import com.fliq.zen_mode.models.ZenTutorialStep
import com.fliq.zen_mode.ui.components.TutorialHighlight
import com.fliq.zen_mode.ui.components.ZenGameOverDialog
import com.fliq.zen_mode.ui.components.ZenRulesDialog
import com.fliq.zen_mode.ui.components.ZenTutorialOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ZenScreen(
    tiles: List<Tile>,
    score: Int,
    lives: Int,
    status: GameStatus,
    gameTime: Long,
    showRules: Boolean,
    showAdRewardDialog: Boolean,
    isPaused: Boolean,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onRulesDismissed: (Boolean) -> Unit,
    onWatchAdClick: () -> Unit,
    onSkipAdClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBackClick: () -> Unit,
    streak: Int = 0,
    accuracy: Float = 0f,
    newBadges: List<Badge> = emptyList(),
    effects: SharedFlow<GameEffect>? = null,
    tutorialStep: ZenTutorialStep? = null,
    onNextTutorialStep: () -> Unit = {},
    onSkipTutorial: () -> Unit = {}
) {
    val tilePositions = remember { mutableMapOf<Int, Offset>() }
    val highlights = remember { mutableStateMapOf<ZenTutorialStep, TutorialHighlight>() }
    val activeEffects = remember { mutableStateListOf<EffectState>() }
    val ripples = remember { mutableStateListOf<RippleState>() }
    val gameColors = MaterialTheme.gameColors

    val isPlaying = status == GameStatus.PLAYING

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gameColors.backgroundGradient))
        ) {
            MeshBackground(streak = streak)

            ripples.forEach { ripple ->
                key(ripple.id) {
                    BackgroundRippleEffect(ripple.position) { ripples.remove(ripple) }
                }
            }

            if ((lives == 1) && isPlaying) CriticalVignette()

            if (isPaused) Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gameColors.pauseDim)
                    .zIndex(5f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .blur(if (status == GameStatus.GAME_OVER || showRules || showAdRewardDialog) 16.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ZenTopBar(
                    isPaused = isPaused,
                    onBackClick = onBackClick,
                    onHelpClick = onHelpClick,
                    onPositioned = { pos, size ->
                        highlights[ZenTutorialStep.WELCOME] = TutorialHighlight(pos, size, isCircle = false)
                    }
                )

                Spacer(modifier = Modifier.weight(0.2f))

                ZenStats(
                    score = score,
                    lives = lives,
                    gameTime = gameTime,
                    onPositioned = { pos, size ->
                        highlights[ZenTutorialStep.STATS] = TutorialHighlight(pos, size, isCircle = false)
                    }
                )

                Spacer(modifier = Modifier.weight(0.5f))

                GameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    onTilePositioned = { id, pos, size ->
                        tilePositions[id] = pos
                        if (id == 5) { // Tutorial Tile
                            highlights[ZenTutorialStep.TILE_INTRO] = TutorialHighlight(pos, size)
                            highlights[ZenTutorialStep.TILE_INTERACT] = TutorialHighlight(pos, size)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                ZenPlayButton(
                    status = status,
                    onAction = { if (isPlaying) onResetGame() else onPlayClick() },
                    onPositioned = { pos, size ->
                        highlights[ZenTutorialStep.START_GAME] = TutorialHighlight(pos, size, isCircle = false)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // Effects
            activeEffects.toList().forEach { effect ->
                key(effect.id) {
                    when (effect.type) {
                        EffectType.SCORE -> FloatingScore(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_COIN -> SparkleEffect(effect) {
                            activeEffects.remove(
                                effect
                            )
                        }

                        EffectType.PARTICLE_BOMB -> BombEffect(effect) { activeEffects.remove(effect) }
                    }
                }
            }

            if (showRules) ZenRulesDialog(onDismiss = onRulesDismissed)
            if (showAdRewardDialog) AdRewardDialog(
                onWatchAd = onWatchAdClick,
                onSkip = onSkipAdClick
            )

            ZenGameOverDialog(
                visible = status == GameStatus.GAME_OVER,
                score = score,
                gameTime = gameTime,
                accuracy = accuracy,
                newBadges = newBadges,
                onRetry = onResetGame,
                onBackToDashboard = onBackClick
            )

            if (tutorialStep != null) {
                ZenTutorialOverlay(
                    step = tutorialStep,
                    highlight = highlights[tutorialStep],
                    onNext = onNextTutorialStep,
                    onSkip = onSkipTutorial
                )
            }
        }
    }
}

@Composable
fun ZenTopBar(
    isPaused: Boolean,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ZenIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
        ) {
            Text(
                text = "ZEN MODE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f
                    )
                ),
                color = Color.White
            )
            Text(
                text = if (isPaused) "PAUSED" else "STAY CALM",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = if (isPaused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        }

        ZenIconButton(icon = Icons.AutoMirrored.Default.HelpOutline, onClick = onHelpClick)
    }
}

@Composable
fun ZenStats(
    score: Int,
    lives: Int,
    gameTime: Long,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ChamferedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            shadowElevation = 8.dp,
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBlock(label = "SCORE", value = score.toString().padStart(3, '0'))
                StatBlock(label = "TIME", value = UtilityMethods.formatTime(gameTime))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LIVES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (index < lives) HeartRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(horizontal = 1.dp)
                                    .graphicsLayer {
                                        if (index < lives) {
                                            shadowElevation = 8f
                                            translationY = -2f
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBlock(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                shadow = androidx.compose.ui.graphics.Shadow(
                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                    offset = Offset(0f, 4f)
                )
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun GameGrid(
    tiles: List<Tile>,
    onTileTapped: (Int, Offset?) -> Unit,
    onTilePositioned: (Int, Offset, Size) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 4
    val rows = (tiles.size + columns - 1) / columns

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        for (i in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < tiles.size) {
                        val tile = tiles[index]
                        var tileCenter by remember { mutableStateOf(Offset.Zero) }
                        ZenTile(
                            tile = tile,
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
                                    onTilePositioned(
                                        tile.id,
                                        center,
                                        Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                                    )
                                }
                        )
                    } else Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ZenTile(
    tile: Tile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val rotation by animateFloatAsState(
        targetValue = if (tile.isRevealed) 180f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "rot"
    )
    val scale by animateFloatAsState(
        if (isPressed) 0.92f else 1f,
        spring(Spring.DampingRatioMediumBouncy),
        label = "s"
    )
    val elevation by animateDpAsState(
        if (isPressed) 2.dp else 10.dp,
        spring(Spring.DampingRatioMediumBouncy),
        label = "elev"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 20 * density
                translationY = (2.dp - elevation).toPx() // Visual lift based on elevation
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shadowElevation = elevation,
                border = BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painterResource(id = R.drawable.ic_card_back),
                        null,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(0.08f),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = elevation
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (tile.isIconVisible) {
                            Image(
                                painter = painterResource(id = if (tile.type == CardType.COIN) R.drawable.ic_coin else R.drawable.ic_bomb),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                                    .graphicsLayer {
                                        translationY = -2.dp.toPx()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZenPlayButton(
    status: GameStatus,
    onAction: () -> Unit,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val isPlaying = status == GameStatus.PLAYING
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    val buttonColor = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .scale(scale)
            .padding(horizontal = 64.dp)
            .height(60.dp)
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
            .background(
                Brush.verticalGradient(
                    listOf(buttonColor, buttonColor.copy(alpha = 0.7f))
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onAction
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = (1f - pulseAlpha) * 0.15f))
            )
        }

        Text(
            text = if (isPlaying) "STOP SESSION" else "START GAME",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ZenIconButton(icon: ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.05f),
        modifier = Modifier
            .size(46.dp)
            .graphicsLayer { translationY = if (isPressed) 2f else -2f },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        shadowElevation = if (isPressed) 2.dp else 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun BackgroundRippleEffect(position: Offset, onComplete: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val color = MaterialTheme.colorScheme.primary
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1200, easing = LinearEasing))
        onComplete()
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0.1f)
    ) {
        drawCircle(
            color = color.copy(alpha = 0.25f * (1f - progress.value)),
            radius = size.maxDimension * 0.8f * progress.value,
            center = position,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun FloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1.2f, spring(Spring.DampingRatioHighBouncy)); scale.animateTo(
            1f,
            tween(200)
        )
        }
        launch { offsetY.animateTo(-180f, animationSpec = tween(1000, easing = LinearEasing)) }
        launch { delay(600); alpha.animateTo(0f, animationSpec = tween(400)); onComplete() }
    }
    Text(
        text = effect.text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            shadow = androidx.compose.ui.graphics.Shadow(
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
                offset = Offset(0f, 6f),
                blurRadius = 12f
            )
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .offset {
                IntOffset(
                    effect.position.x.roundToInt() - 50,
                    (effect.position.y + offsetY.value).roundToInt() - 50
                )
            }
            .scale(scale.value)
            .alpha(alpha.value)
    )
}

@Preview(showBackground = true)
@Composable
fun ZenScreenPreview() {
    FliqTheme {
        ZenScreen(
            tiles = listOf(
                Tile(0),
                Tile(1),
                Tile(2),
                Tile(3),
                Tile(4),
                Tile(5),
                Tile(6),
                Tile(7),
                Tile(8),
                Tile(9),
                Tile(10),
                Tile(11),
                Tile(12),
                Tile(13),
                Tile(14),
                Tile(15)
            ),
            accuracy = 0f,
            onSkipAdClick = {},
            onWatchAdClick = {},
            showAdRewardDialog = false,
            lives = 3,
            score = 100,
            status = GameStatus.READY,
            streak = 3,
            gameTime = 100L,
            isPaused = false,
            effects = null,
            newBadges = emptyList(),
            showRules = false,
            onBackClick = {},
            onHelpClick = {},
            onPlayClick = {},
            onResetGame = {},
            onTileTapped = { _, _ -> },
            onRulesDismissed = {},
            tutorialStep = null,
            onNextTutorialStep = {},
            onSkipTutorial = {}
        )
    }
}
