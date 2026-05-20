package com.fliq.game_engine.ui

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
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.HeartRed
import com.fliq.core.theme.gameColors
import com.fliq.database.MatchHistory
import com.fliq.game_engine.models.Challenge
import com.fliq.game_engine.models.EffectState
import com.fliq.game_engine.models.EffectType
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.ParticleType
import com.fliq.game_engine.models.RippleState
import com.fliq.game_engine.models.Tile
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
    currentChallenge: Challenge,
    gameTime: Long,
    leaderboard: List<MatchHistory>,
    showRules: Boolean,
    showAdRewardDialog: Boolean,
    isPaused: Boolean,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onChallengeChange: (Challenge) -> Unit,
    onRulesDismissed: (Boolean) -> Unit,
    onWatchAdClick: () -> Unit,
    onSkipAdClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onProfileIntentClicked: () -> Unit,
    onLeaderboardIntentClicked: () -> Unit,
    onAchievementsIntentClicked: () -> Unit,
    onPreferencesIntentClicked: () -> Unit,
    streak: Int = 0,
    reactionTime: Long = 0,
    accuracy: Float = 0f,
    newBadges: List<Badge> = emptyList(),
    effects: SharedFlow<GameEffect>? = null
) {
    var showGameOverOverlay by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val activeEffects = remember { mutableStateListOf<EffectState>() }
    val ripples = remember { mutableStateListOf<RippleState>() }
    val tilePositions = remember { mutableMapOf<Int, Offset>() }

    val gameColors = MaterialTheme.gameColors

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
        if (status == GameStatus.GAME_OVER) {
            showGameOverOverlay = true
        } else if (status == GameStatus.READY) {
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
                .background(Brush.verticalGradient(gameColors.backgroundGradient))
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
                        .background(gameColors.pauseDim)
                        .zIndex(5f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState, enabled = status != GameStatus.PLAYING)
                    .blur(if (showGameOverOverlay || showRules || showAdRewardDialog || isMenuVisible) 16.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Custom Top Bar - Frosted Glass
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(icon = Icons.Default.Menu, onClick = { isMenuVisible = true })
                    
                    Text(
                        text = "FLIPPY",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))
                        )
                    )

                    IconButton(icon = Icons.AutoMirrored.Default.HelpOutline, onClick = onHelpClick)
                }

                GameHeader(
                    score = score,
                    lives = lives,
                    gameTime = gameTime,
                    isPaused = isPaused,
                    reactionTime = reactionTime
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = status == GameStatus.READY,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut()
                ) {
                    ChallengeSelector(
                        selectedChallenge = currentChallenge,
                        onChallengeChange = onChallengeChange
                    )
                }

                Spacer(modifier = Modifier.height(if (status == GameStatus.PLAYING) 24.dp else 0.dp))

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

            // Overlay for effects
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                PlayButtonComponent(
                    status = status,
                    onAction = {
                        if (status == GameStatus.PLAYING) {
                            onResetGame()
                        } else {
                            onPlayClick()
                        }
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
                onAchievementsIntentClicked = onAchievementsIntentClicked,
                onPreferencesIntentClicked = onPreferencesIntentClicked
            )

            if (showRules) {
                FlippyRulesDialog(onDismiss = onRulesDismissed)
            }

            if (showAdRewardDialog) {
                AdRewardDialog(
                    onWatchAd = onWatchAdClick,
                    onSkip = onSkipAdClick
                )
            }

            GameStatusOverlay(
                visible = showGameOverOverlay,
                score = score,
                gameTime = gameTime,
                accuracy = accuracy,
                newBadges = newBadges,
                onDismiss = {
                    showGameOverOverlay = false
                    onResetGame()
                }
            )
        }
    }
}

@Composable
private fun IconButton(icon: ImageVector, onClick: () -> Unit) {
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }
    val bgColor = if (isLightTheme) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bgColor,
        modifier = Modifier.size(44.dp).shadow(if (isLightTheme) 4.dp else 0.dp, CircleShape),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
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

    Canvas(modifier = Modifier
        .fillMaxSize()
        .zIndex(0.1f)) {
        drawCircle(
            color = color.copy(alpha = 0.25f * (1f - progress.value)),
            radius = size.maxDimension * 0.8f * progress.value,
            center = position,
            style = Stroke(width = 2.dp.toPx())
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
                )
            )
    )
}

@Composable
fun FloatingScore(effect: EffectState, onComplete: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1.2f, spring(Spring.DampingRatioHighBouncy))
            scale.animateTo(1f, tween(200))
        }
        launch {
            offsetY.animateTo(-180f, animationSpec = tween(1000, easing = LinearEasing))
        }
        launch {
            delay(600)
            alpha.animateTo(0f, animationSpec = tween(400))
            onComplete()
        }
    }

    Text(
        text = effect.text,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            shadow = shadow(color = Color.Black.copy(alpha = 0.5f), blurRadius = 8f)
        ),
        color = MaterialTheme.gameColors.scorePopup,
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

private fun shadow(color: Color, blurRadius: Float) = androidx.compose.ui.graphics.Shadow(color, blurRadius = blurRadius)

@Composable
fun SparkleEffect(effect: EffectState, onComplete: () -> Unit) {
    val particles = remember { List(12) { Random.nextFloat() * 360f } }
    val progress = remember { Animatable(0f) }
    val particleColor = MaterialTheme.gameColors.particleCoin

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
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
            val dist = 140f * progress.value
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
        progress.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = Modifier.offset {
        IntOffset(
            effect.position.x.roundToInt(),
            effect.position.y.roundToInt()
        )
    }) {
        // Shockwave
        drawCircle(
            color = shockwaveColor.copy(alpha = shockwaveColor.alpha * (1f - progress.value)),
            radius = 250f * progress.value,
            style = Stroke(width = 8f)
        )
        // Fire/Smoke
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

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "y"
    )

    val gameColors = MaterialTheme.gameColors

    val color1 by animateColorAsState(
        targetValue = if (isFever) gameColors.feverColor1 else gameColors.meshColor1,
        animationSpec = tween(1500), label = "c1"
    )
    val color2 by animateColorAsState(
        targetValue = if (isFever) gameColors.feverColor2 else gameColors.meshColor2,
        animationSpec = tween(1500), label = "c2"
    )

    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(100.dp)
            .alpha(if (isLightTheme) 0.6f else 0.4f)
    ) {
        drawCircle(
            color = color1.copy(alpha = 0.5f),
            radius = size.width,
            center = Offset(size.width / 2 + xOffset, size.height / 3 + yOffset)
        )
        drawCircle(
            color = color2.copy(alpha = 0.4f),
            radius = size.width * 0.8f,
            center = Offset(size.width / 4 - xOffset, size.height / 1.5f - yOffset)
        )
    }
    
    // Floating Particles
    repeat(15) {
         FloatingParticle()
    }
}

@Composable
fun FloatingParticle() {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    val x = remember { Random.nextFloat() }
    val y = remember { Random.nextFloat() }
    val size = remember { Random.nextFloat() * 4 + 2 }
    val duration = remember { Random.nextInt(4000, 8000) }
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(duration / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    
    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(tween(duration, easing = LinearEasing), RepeatMode.Restart),
        label = "y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = (y * 1000).dp, start = (x * 400).dp)
    ) {
        Box(
            modifier = Modifier
                .offset(y = animY.dp)
                .size(size.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha), CircleShape)
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
    onAchievementsIntentClicked: () -> Unit,
    onPreferencesIntentClicked: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .zIndex(10f)) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
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
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp),
                color = MaterialTheme.gameColors.backgroundGradient.first().copy(alpha = 0.95f),
                tonalElevation = 16.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Spacer(modifier = Modifier.height(64.dp))

                    Text(
                        text = "FLIPPY",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 6.sp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MASTER YOUR REFLEXES",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(64.dp))

                    NavigationMenuItem(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        onClick = onProfileIntentClicked
                    )

                    NavigationMenuItem(
                        icon = Icons.Default.Leaderboard,
                        label = "Leaderboard",
                        onClick = onLeaderboardIntentClicked
                    )

                    NavigationMenuItem(
                        icon = Icons.Default.EmojiEvents,
                        label = "Achievements",
                        onClick = onAchievementsIntentClicked
                    )

                    NavigationMenuItem(
                        icon = Icons.Default.Settings,
                        label = "Preferences",
                        onClick = onPreferencesIntentClicked
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    NavigationMenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "Sign Out",
                        onClick = onSignOutClick,
                        color = BombRed
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
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = color
            )
        }
    }
}

@Composable
fun PlayButtonComponent(
    status: GameStatus,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val isPlaying = status == GameStatus.PLAYING

    val gradient = if (isPlaying) {
        Brush.linearGradient(listOf(BombRed, Color(0xFFE11D48)))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.scale(scale)) {
        if (!isPlaying) {
            // Pulsing background for CTA
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(pulseScale)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = if (isPlaying) BombRed else MaterialTheme.colorScheme.primary
                )
                .background(brush = gradient, shape = CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onAction
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isPlaying) Color.White else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun LeaderboardSection(
    leaderboard: List<MatchHistory>,
    modifier: Modifier = Modifier
) {
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }
    val bgColor = if (isLightTheme) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Leaderboard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "TOP SCORES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth().shadow(if (isLightTheme) 8.dp else 0.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = bgColor,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            if (leaderboard.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start playing to see rankings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leaderboard.forEachIndexed { index, match ->
                        LeaderboardItem(index + 1, match)
                        if (index < leaderboard.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeSelector(
    selectedChallenge: Challenge,
    onChallengeChange: (Challenge) -> Unit
) {
    val challenges = Challenge.entries
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "SELECT CHALLENGE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            challenges.forEach { challenge ->
                val isSelected = selectedChallenge == challenge
                val targetColor = if (isSelected) MaterialTheme.colorScheme.primary 
                                  else if (isLightTheme) Color.White.copy(alpha = 0.9f)
                                  else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isLightTheme && !isSelected) 4.dp else 0.dp, RoundedCornerShape(20.dp))
                        .clickable { onChallengeChange(challenge) },
                    shape = RoundedCornerShape(20.dp),
                    color = targetColor,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                             Icon(
                                imageVector = when(challenge) {
                                    Challenge.SPEED_RUN -> Icons.Default.Timer
                                    Challenge.MIRAGE -> Icons.Default.Person // Placeholder
                                    Challenge.MINEFIELD -> Icons.Default.Favorite // Placeholder
                                    Challenge.BLACKOUT -> Icons.Default.Menu // Placeholder
                                    Challenge.FRENZY -> Icons.Default.EmojiEvents // Placeholder
                                },
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = challenge.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = challenge.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameHeader(
    score: Int,
    lives: Int,
    gameTime: Long,
    isPaused: Boolean = false,
    reactionTime: Long = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pause_pulse")
    val gameColors = MaterialTheme.gameColors
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { red < 0.5f && green < 0.5f && blue < 0.5f }

    val pulseColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.secondary,
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "color"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(if (isLightTheme) 12.dp else 0.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        color = if (isLightTheme) Color.White.copy(alpha = 0.95f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCORE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(
                    visible = reactionTime > 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = gameColors.scorePopup.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "${reactionTime / 1000f}s",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = gameColors.scorePopup
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isPaused) pulseColor else MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = UtilityMethods.formatTime(gameTime),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = if (isPaused) pulseColor else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Life",
                        tint = if (index < lives) HeartRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameGrid(
    tiles: List<Tile>,
    onTileTapped: (Int, Offset?) -> Unit,
    onTilePositioned: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 4
    val rows = (tiles.size + columns - 1) / columns

    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                            isIconVisible = tile.isIconVisible,
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
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    FliqTheme {
        GameScreen(
            tiles = List(16) { Tile(it) },
            score = 10,
            lives = 3,
            status = GameStatus.PLAYING,
            currentChallenge = Challenge.SPEED_RUN,
            gameTime = 1,
            leaderboard = emptyList(),
            showRules = false,
            isPaused = false,
            onTileTapped = { _, _ -> },
            onPlayClick = {},
            onResetGame = {},
            onChallengeChange = {},
            onRulesDismissed = {},
            onHelpClick = {},
            onSignOutClick = {},
            onProfileIntentClicked = {},
            onLeaderboardIntentClicked = {},
            onAchievementsIntentClicked = {},
            onPreferencesIntentClicked = {},
            showAdRewardDialog = false,
            onSkipAdClick = {},
            onWatchAdClick = {}
        )
    }
}
