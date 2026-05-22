package com.fliq.minefield.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.BgDeepDark
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.Gold
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
import com.fliq.game_engine.ui.PlayButtonComponent
import com.fliq.game_engine.ui.SparkleEffect
import com.fliq.minefield.ui.components.MinefieldGameOverDialog
import com.fliq.minefield.ui.components.MinefieldRulesDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MinefieldScreen(
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
    reactionTime: Long = 0,
    accuracy: Float = 0f,
    newBadges: List<Badge> = emptyList(),
    effects: SharedFlow<GameEffect>? = null
) {
    val tilePositions = remember { mutableMapOf<Int, Offset>() }
    val activeEffects = remember { mutableStateListOf<EffectState>() }
    val ripples = remember { mutableStateListOf<RippleState>() }
    val gameColors = MaterialTheme.gameColors

    val isPlaying = status == GameStatus.PLAYING

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gameColors.backgroundGradient))) {
            MeshBackground(streak = streak)

            ripples.forEach { ripple ->
                key(ripple.id) {
                    BackgroundRippleEffect(ripple.position) { ripples.remove(ripple) }
                }
            }

            if (lives == 1 && isPlaying) CriticalVignette()

            if (isPaused) Box(modifier = Modifier.fillMaxSize().background(gameColors.pauseDim).zIndex(5f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .blur(if (status == GameStatus.GAME_OVER || showRules || showAdRewardDialog) 16.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MinefieldTopBar(
                    isPaused = isPaused,
                    onBackClick = onBackClick,
                    onHelpClick = onHelpClick
                )

                MinefieldStats(
                    score = score,
                    lives = lives,
                    gameTime = gameTime
                )

                Spacer(modifier = Modifier.height(24.dp))

                GameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    onTilePositioned = { id, pos -> tilePositions[id] = pos },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Effects
            activeEffects.toList().forEach { effect ->
                key(effect.id) {
                    when (effect.type) {
                        EffectType.SCORE -> FloatingScore(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_COIN -> SparkleEffect(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_BOMB -> BombEffect(effect) { activeEffects.remove(effect) }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                PlayButtonComponent(
                    status = status,
                    onAction = { if (isPlaying) onResetGame() else onPlayClick() }
                )
            }

            if (showRules) MinefieldRulesDialog(onDismiss = onRulesDismissed)
            if (showAdRewardDialog) AdRewardDialog(onWatchAd = onWatchAdClick, onSkip = onSkipAdClick)

            MinefieldGameOverDialog(
                visible = status == GameStatus.GAME_OVER,
                score = score,
                gameTime = gameTime,
                accuracy = accuracy,
                newBadges = newBadges,
                onRetry = onResetGame,
                onBackToDashboard = onBackClick
            )
        }
    }
}

@Composable
fun MinefieldTopBar(
    isPaused: Boolean,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MinefieldIconButton(icon = Icons.Default.ArrowBack, onClick = onBackClick)
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MINEFIELD",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black, 
                    letterSpacing = 1.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(Color.Black.copy(alpha = 0.3f), offset = Offset(0f, 4f), blurRadius = 8f)
                ),
                color = Color.White
            )
            Text(
                text = if (isPaused) "PAUSED" else "WATCH YOUR STEP!",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                color = if (isPaused) Gold else BombRed
            )
        }

        MinefieldIconButton(icon = Icons.AutoMirrored.Default.HelpOutline, onClick = onHelpClick)
    }
}

@Composable
fun MinefieldStats(
    score: Int,
    lives: Int,
    gameTime: Long
) {
    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(88.dp).offset(y = 4.dp).alpha(0.3f),
            shape = ChamferedCornerShape(24.dp),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ChamferedCornerShape(24.dp),
            color = BgSlate.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent, Color.White.copy(alpha = 0.05f))))
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
                        text = "STABILITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        repeat(1) { // Only 1 life visually in stats for Minefield
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (lives > 0) HeartRed else Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.size(20.dp).padding(horizontal = 1.dp).graphicsLayer {
                                    if (lives > 0) {
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
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black, 
                fontFamily = FontFamily.Monospace,
                shadow = androidx.compose.ui.graphics.Shadow(Color.Black.copy(alpha = 0.5f), offset = Offset(0f, 4f))
            ),
            color = Color.White
        )
    }
}

@Composable
fun GameGrid(
    tiles: List<Tile>,
    onTileTapped: (Int, Offset?) -> Unit,
    onTilePositioned: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        MinefieldTile(
                            tile = tile,
                            onClick = { onTileTapped(tile.id, tileCenter) },
                            modifier = Modifier.weight(1f).aspectRatio(1f).onGloballyPositioned { coords ->
                                val center = Offset(coords.positionInRoot().x + coords.size.width / 2, coords.positionInRoot().y + coords.size.height / 2)
                                tileCenter = center
                                onTilePositioned(tile.id, center)
                            }
                        )
                    } else Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MinefieldTile(
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
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")
    val zOffset by animateFloatAsState(if (isPressed) 0f else 6.dp.value, label = "z")

    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer { rotationY = rotation; cameraDistance = 16 * density; translationY = -zOffset }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.fillMaxSize().offset(y = 6.dp),
                    shape = ChamferedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {}
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = ChamferedCornerShape(12.dp),
                    color = BgSlate.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painterResource(id = R.drawable.ic_card_back), null, modifier = Modifier.size(24.dp).alpha(0.05f), tint = Color.White)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = ChamferedCornerShape(12.dp),
                    color = BgDeepDark,
                    border = BorderStroke(2.dp, if (tile.type == CardType.COIN) Gold else BombRed)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (tile.isIconVisible) {
                            Image(
                                painter = painterResource(id = if (tile.type == CardType.COIN) R.drawable.ic_coin else R.drawable.ic_bomb),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MinefieldIconButton(icon: ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier.size(44.dp).graphicsLayer { translationY = if (isPressed) 2f else -2f },
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
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
    Canvas(modifier = Modifier.fillMaxSize().zIndex(0.1f)) {
        drawCircle(color = color.copy(alpha = 0.25f * (1f - progress.value)), radius = size.maxDimension * 0.8f * progress.value, center = position, style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
fun FloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch { scale.animateTo(1.2f, spring(Spring.DampingRatioHighBouncy)); scale.animateTo(1f, tween(200)) }
        launch { offsetY.animateTo(-180f, animationSpec = tween(1000, easing = LinearEasing)) }
        launch { delay(600); alpha.animateTo(0f, animationSpec = tween(400)); onComplete() }
    }
    Text(
        text = effect.text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, shadow = androidx.compose.ui.graphics.Shadow(color = Color.Black.copy(alpha = 0.5f), blurRadius = 8f)),
        color = Gold,
        modifier = Modifier.offset { IntOffset(effect.position.x.roundToInt() - 50, (effect.position.y + offsetY.value).roundToInt() - 50) }.scale(scale.value).alpha(alpha.value)
    )
}
