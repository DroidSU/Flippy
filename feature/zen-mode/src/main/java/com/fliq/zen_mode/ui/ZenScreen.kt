package com.fliq.zen_mode.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fliq.common.Badge
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
import com.fliq.zen_mode.models.ZenTutorialStep
import com.fliq.zen_mode.ui.components.GameGrid
import com.fliq.zen_mode.ui.components.TutorialHighlight
import com.fliq.zen_mode.ui.components.ZenBackgroundRipple
import com.fliq.zen_mode.ui.components.ZenFloatingScore
import com.fliq.zen_mode.ui.components.ZenGameOverDialog
import com.fliq.zen_mode.ui.components.ZenIconButton
import com.fliq.zen_mode.ui.components.ZenLandscapeStatsPanel
import com.fliq.zen_mode.ui.components.ZenPlayButton
import com.fliq.zen_mode.ui.components.ZenRotationOverlay
import com.fliq.zen_mode.ui.components.ZenRulesDialog
import com.fliq.zen_mode.ui.components.ZenStats
import com.fliq.zen_mode.ui.components.ZenTopBar
import com.fliq.zen_mode.ui.components.ZenTutorialOverlay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

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
    onSkipTutorial: () -> Unit = {},
    showRotationPrompt: Boolean = false,
    onRotationPromptDismissed: (Boolean) -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) {
        if (isLandscape && showRotationPrompt) {
            onRotationPromptDismissed(false)
        }
    }

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
            StarBackground()
            MeshBackground(streak = streak)

            ripples.forEach { ripple ->
                key(ripple.id) {
                    ZenBackgroundRipple(ripple.position) { ripples.remove(ripple) }
                }
            }

            if ((lives == 1) && isPlaying) CriticalVignette()

            if (isPaused) Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gameColors.pauseDim)
                    .zIndex(5f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .blur(if (status == GameStatus.GAME_OVER || showRules || showAdRewardDialog) 16.dp else 0.dp)
            ) {
                if (isLandscape) {
                    ZenContentLandscape(
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
                                highlights[ZenTutorialStep.TILE_INTRO] = TutorialHighlight(pos, size)
                                highlights[ZenTutorialStep.TILE_INTERACT] = TutorialHighlight(pos, size)
                            }
                        },
                        onStatsPositioned = { pos, size ->
                            highlights[ZenTutorialStep.STATS] = TutorialHighlight(pos, size, isCircle = false)
                        },
                        onTopBarPositioned = { pos, size ->
                            highlights[ZenTutorialStep.WELCOME] = TutorialHighlight(pos, size, isCircle = false)
                        },
                        onPlayButtonPositioned = { pos, size ->
                            highlights[ZenTutorialStep.START_GAME] = TutorialHighlight(pos, size, isCircle = false)
                        }
                    )
                } else {
                    ZenContentPortrait(
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
                                highlights[ZenTutorialStep.TILE_INTRO] = TutorialHighlight(pos, size)
                                highlights[ZenTutorialStep.TILE_INTERACT] = TutorialHighlight(pos, size)
                            }
                        },
                        onStatsPositioned = { pos, size ->
                            highlights[ZenTutorialStep.STATS] = TutorialHighlight(pos, size, isCircle = false)
                        },
                        onTopBarPositioned = { pos, size ->
                            highlights[ZenTutorialStep.WELCOME] = TutorialHighlight(pos, size, isCircle = false)
                        },
                        onPlayButtonPositioned = { pos, size ->
                            highlights[ZenTutorialStep.START_GAME] = TutorialHighlight(pos, size, isCircle = false)
                        }
                    )
                }
            }

            // Effects
            activeEffects.toList().forEach { effect ->
                key(effect.id) {
                    when (effect.type) {
                        EffectType.SCORE -> ZenFloatingScore(effect) { activeEffects.remove(effect) }
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

            if (showRotationPrompt && !isLandscape) {
                ZenRotationOverlay(onDismiss = onRotationPromptDismissed)
            }
        }
    }
}

@Composable
fun StarBackground() {
    val configuration = LocalConfiguration.current
    val starCount = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
        val area = configuration.screenWidthDp * configuration.screenHeightDp
        (area / 2500).coerceIn(50, 200)
    }

    val stars = remember(starCount) {
        List(starCount) {
            val x = kotlin.random.Random.nextFloat()
            val y = kotlin.random.Random.nextFloat()
            val starSize = kotlin.random.Random.nextFloat() * 1.5f + 0.5f
            val alpha = kotlin.random.Random.nextFloat() * 0.3f + 0.2f
            Triple(x, y, starSize to alpha)
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (x, y, data) ->
            val (starSize, alpha) = data
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = starSize.dp.toPx(),
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

@Composable
fun ZenContentPortrait(
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
    onTilePositioned: (Int, Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onStatsPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onTopBarPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onPlayButtonPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZenTopBar(
            isPaused = isPaused,
            onBackClick = onBackClick,
            onHelpClick = onHelpClick,
            onPositioned = onTopBarPositioned
        )

        Spacer(modifier = Modifier.weight(0.2f))

        ZenStats(
            score = score,
            lives = lives,
            gameTime = gameTime,
            onPositioned = onStatsPositioned
        )

        Spacer(modifier = Modifier.weight(0.5f))

        GameGrid(
            tiles = tiles,
            onTileTapped = onTileTapped,
            onTilePositioned = onTilePositioned,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ZenPlayButton(
            status = status,
            onAction = { if (isPlaying) onResetGame() else onPlayClick() },
            onPositioned = onPlayButtonPositioned,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ZenContentLandscape(
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
    onTilePositioned: (Int, Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onStatsPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onTopBarPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit,
    onPlayButtonPositioned: (Offset, androidx.compose.ui.geometry.Size) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left Column: Stats Panel and Navigation
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f), // Give the left column a defined portion of the width
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ZenLandscapeStatsPanel(
                score = score,
                lives = lives,
                gameTime = gameTime,
                modifier = Modifier.weight(1f, fill = false).padding(top = 8.dp),
                onPositioned = { pos: Offset, size: androidx.compose.ui.geometry.Size ->
                    onStatsPositioned(pos, size)
                    onTopBarPositioned(pos, size)
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            ) {
                ZenIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBackClick
                )
                ZenIconButton(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    onClick = onHelpClick
                )
            }
        }

        // Center: Game Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            GameGrid(
                tiles = tiles,
                onTileTapped = onTileTapped,
                onTilePositioned = onTilePositioned,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }

        // Right side: Play Button
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.BottomEnd
        ) {
            ZenPlayButton(
                status = status,
                onAction = { if (isPlaying) onResetGame() else onPlayClick() },
                onPositioned = onPlayButtonPositioned,
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ZenScreenLandscapePreview() {
    FliqTheme {
        ZenScreen(
            tiles = List(16) { Tile(it) },
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
            onSkipTutorial = {},
            showRotationPrompt = false,
            onRotationPromptDismissed = {}
        )
    }
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
            onSkipTutorial = {},
            showRotationPrompt = false,
            onRotationPromptDismissed = {}
        )
    }
}
