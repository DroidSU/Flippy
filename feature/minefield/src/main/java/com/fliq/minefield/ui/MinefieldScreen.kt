package com.fliq.minefield.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.gameColors
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
import com.fliq.minefield.models.MinefieldTutorialStep
import com.fliq.minefield.ui.components.MinefieldBackgroundRipple
import com.fliq.minefield.ui.components.MinefieldBeatingHeartIcon
import com.fliq.minefield.ui.components.MinefieldFloatingScore
import com.fliq.minefield.ui.components.MinefieldGameGrid
import com.fliq.minefield.ui.components.MinefieldGameOverDialog
import com.fliq.minefield.ui.components.MinefieldIconButton
import com.fliq.minefield.ui.components.MinefieldPlayButton
import com.fliq.minefield.ui.components.MinefieldRotationOverlay
import com.fliq.minefield.ui.components.MinefieldRulesDialog
import com.fliq.minefield.ui.components.MinefieldStatBlock
import com.fliq.minefield.ui.components.MinefieldStats
import com.fliq.minefield.ui.components.MinefieldTopBar
import com.fliq.minefield.ui.components.MinefieldTutorialHighlight
import com.fliq.minefield.ui.components.MinefieldTutorialOverlay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

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
    accuracy: Float = 0f,
    newBadges: List<Badge> = emptyList(),
    effects: SharedFlow<GameEffect>? = null,
    tutorialStep: MinefieldTutorialStep? = null,
    onNextTutorialStep: () -> Unit = {},
    onSkipTutorial: () -> Unit = {},
    showRotationPrompt: Boolean = false,
    onRotationPromptDismissed: (Boolean) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) {
        if (isLandscape && showRotationPrompt) {
            onRotationPromptDismissed(false)
        }
    }

    val tilePositions = remember { mutableMapOf<Int, Offset>() }
    val highlights = remember { mutableStateMapOf<MinefieldTutorialStep, MinefieldTutorialHighlight>() }
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
                    MinefieldBackgroundRipple(ripple.position) { ripples.remove(ripple) }
                }
            }

            if (lives == 1 && isPlaying) CriticalVignette()

            if (isPaused) Box(modifier = Modifier.fillMaxSize().background(gameColors.pauseDim).zIndex(5f))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .blur(if (status == GameStatus.GAME_OVER || showRules || showAdRewardDialog) 16.dp else 0.dp)
            ) {
                if (isLandscape) {
                    MinefieldContentLandscape(
                        tiles = tiles,
                        score = score,
                        lives = lives,
                        status = status,
                        gameTime = gameTime,
                        isPaused = isPaused,
                        isPlaying = isPlaying,
                        onTileTapped = onTileTapped,
                        onPlayClick = onPlayClick,
                        onResetGame = onResetGame,
                        onHelpClick = onHelpClick,
                        onBackClick = onBackClick,
                        onTilePositioned = { id, pos, size ->
                            tilePositions[id] = pos
                            if (id == 5) {
                                highlights[MinefieldTutorialStep.TILE_INTRO] = MinefieldTutorialHighlight(pos, size)
                                highlights[MinefieldTutorialStep.TILE_INTERACT] = MinefieldTutorialHighlight(pos, size)
                            }
                        },
                        onStatsPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.STATS] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        },
                        onTopBarPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.WELCOME] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        },
                        onPlayButtonPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.START_GAME] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        }
                    )
                } else {
                    MinefieldContentPortrait(
                        tiles = tiles,
                        score = score,
                        lives = lives,
                        status = status,
                        gameTime = gameTime,
                        isPaused = isPaused,
                        isPlaying = isPlaying,
                        onTileTapped = onTileTapped,
                        onPlayClick = onPlayClick,
                        onResetGame = onResetGame,
                        onHelpClick = onHelpClick,
                        onBackClick = onBackClick,
                        onTilePositioned = { id, pos, size ->
                            tilePositions[id] = pos
                            if (id == 5) {
                                highlights[MinefieldTutorialStep.TILE_INTRO] = MinefieldTutorialHighlight(pos, size)
                                highlights[MinefieldTutorialStep.TILE_INTERACT] = MinefieldTutorialHighlight(pos, size)
                            }
                        },
                        onStatsPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.STATS] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        },
                        onTopBarPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.WELCOME] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        },
                        onPlayButtonPositioned = { pos, size ->
                            highlights[MinefieldTutorialStep.START_GAME] = MinefieldTutorialHighlight(pos, size, isCircle = false)
                        }
                    )
                }
            }

            activeEffects.toList().forEach { effect ->
                key(effect.id) {
                    when (effect.type) {
                        EffectType.SCORE -> MinefieldFloatingScore(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_COIN -> SparkleEffect(effect) { activeEffects.remove(effect) }
                        EffectType.PARTICLE_BOMB -> BombEffect(effect) { activeEffects.remove(effect) }
                    }
                }
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

            if (tutorialStep != null) {
                MinefieldTutorialOverlay(
                    step = tutorialStep,
                    highlight = highlights[tutorialStep],
                    onNext = onNextTutorialStep,
                    onSkip = onSkipTutorial
                )
            }

            if (showRotationPrompt && !isLandscape) {
                MinefieldRotationOverlay(onDismiss = onRotationPromptDismissed)
            }
        }
    }
}

@Composable
fun MinefieldContentPortrait(
    tiles: List<Tile>,
    score: Int,
    lives: Int,
    status: GameStatus,
    gameTime: Long,
    isPaused: Boolean,
    isPlaying: Boolean,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onHelpClick: () -> Unit,
    onBackClick: () -> Unit,
    onTilePositioned: (Int, Offset, Size) -> Unit,
    onStatsPositioned: (Offset, Size) -> Unit,
    onTopBarPositioned: (Offset, Size) -> Unit,
    onPlayButtonPositioned: (Offset, Size) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MinefieldTopBar(
            isPaused = isPaused,
            onBackClick = onBackClick,
            onHelpClick = onHelpClick,
            onPositioned = onTopBarPositioned
        )

        Spacer(modifier = Modifier.weight(0.2f))

        MinefieldStats(
            score = score,
            lives = lives,
            gameTime = gameTime,
            onPositioned = onStatsPositioned
        )

        Spacer(modifier = Modifier.weight(0.5f))

        MinefieldGameGrid(
            tiles = tiles,
            onTileTapped = onTileTapped,
            onTilePositioned = onTilePositioned,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        MinefieldPlayButton(
            status = status,
            onAction = { if (isPlaying) onResetGame() else onPlayClick() },
            onPositioned = onPlayButtonPositioned,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MinefieldContentLandscape(
    tiles: List<Tile>,
    score: Int,
    lives: Int,
    status: GameStatus,
    gameTime: Long,
    isPaused: Boolean,
    isPlaying: Boolean,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onHelpClick: () -> Unit,
    onBackClick: () -> Unit,
    onTilePositioned: (Int, Offset, Size) -> Unit,
    onStatsPositioned: (Offset, Size) -> Unit,
    onTopBarPositioned: (Offset, Size) -> Unit,
    onPlayButtonPositioned: (Offset, Size) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .onGloballyPositioned { coords ->
                    val center = Offset(
                        coords.positionInRoot().x + coords.size.width / 2,
                        coords.positionInRoot().y + coords.size.height / 2
                    )
                    onStatsPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
                    onTopBarPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
                }
        ) {
            Text(
                text = "MINEFIELD",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
            Text(
                text = if (isPaused) "PAUSED" else "WATCH YOUR STEP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isPaused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MinefieldStatBlock(label = "SCORE", value = score.toString().padStart(3, '0'))
                MinefieldStatBlock(label = "TIME", value = UtilityMethods.formatTime(gameTime))
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "LIVES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Row(modifier = Modifier.padding(top = 4.dp)) {
                repeat(lives.coerceAtLeast(0)) { index ->
                    MinefieldBeatingHeartIcon(
                        isAlive = true,
                        size = 24.dp,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            MinefieldGameGrid(
                tiles = tiles,
                onTileTapped = onTileTapped,
                onTilePositioned = onTilePositioned,
                modifier = Modifier.fillMaxHeight().aspectRatio(1f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp, start = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MinefieldIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack, 
                onClick = onBackClick
            )
            MinefieldIconButton(
                icon = Icons.AutoMirrored.Filled.HelpOutline, 
                onClick = onHelpClick
            )
        }

        MinefieldPlayButton(
            status = status,
            onAction = { if (isPlaying) onResetGame() else onPlayClick() },
            onPositioned = onPlayButtonPositioned,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 12.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun MinefieldScreenLandscapePreview() {
    FliqTheme {
        MinefieldScreen(
            tiles = List(16) { Tile(it) },
            accuracy = 0f,
            onSkipAdClick = {},
            onWatchAdClick = {},
            showAdRewardDialog = false,
            lives = 1,
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
            onSkipTutorial = {},
            showRotationPrompt = false,
            onRotationPromptDismissed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MinefieldScreenPreview() {
    FliqTheme {
        MinefieldScreen(
            tiles = List(16) { Tile(it) },
            accuracy = 0f,
            onSkipAdClick = {},
            onWatchAdClick = {},
            showAdRewardDialog = false,
            lives = 1,
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
            onSkipTutorial = {},
            showRotationPrompt = false,
            onRotationPromptDismissed = {}
        )
    }
}
