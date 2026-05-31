package com.fliq.views.v2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.core.theme.CoreRed
import com.fliq.core.theme.ElectricCyan
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.components.FliqButton
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqSurface
import com.fliq.game_engine.models.v2.Boost
import com.fliq.game_engine.models.v2.GameEffectV2
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.models.v2.GameStateV2
import com.fliq.game_engine.models.v2.GoalType
import com.fliq.game_engine.models.v2.TileState
import com.fliq.game_engine.models.v2.TileType
import com.fliq.game_engine.models.v2.TileV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun GameScreenV2(
    gameState: GameStateV2,
    tiles: List<TileV2>,
    score: Int,
    combo: Int,
    lives: Int,
    progress: Float,
    currentStage: GameStage?,
    selectedBoost: Boost?,
    effects: Flow<GameEffectV2>,
    onTileTapped: (Int) -> Unit,
    onTileEntered: (Int) -> Unit,
    onPauseClick: () -> Unit,
    onBoostSelect: (Boost) -> Unit,
    onStartStage: () -> Unit,
    onLoadStage: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val tileBounds = remember { mutableStateMapOf<Int, Rect>() }
    val activeParticles = remember { mutableStateListOf<Pair<Offset, Color>>() }

    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is GameEffectV2.Success -> {
                    tileBounds[effect.tileId]?.center?.let { center ->
                        activeParticles.add(Pair(center, ElectricCyan))
                    }
                }
                is GameEffectV2.Explosion -> {
                    tileBounds[effect.tileId]?.center?.let { center ->
                        activeParticles.add(Pair(center, CoreRed))
                    }
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(gameState) {
                if (gameState == GameStateV2.ACTION || gameState == GameStateV2.FEVER) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val pos = change.position
                            tileBounds.forEach { (id, rect) ->
                                if (rect.contains(pos)) {
                                    onTileEntered(id)
                                }
                            }
                        }
                    )
                }
            }
    ) {
        // Fever Mode Glow
        if (gameState == GameStateV2.FEVER) {
            val feverColor by rememberInfiniteTransition(label = "fever").animateColor(
                initialValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                targetValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                label = "color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(8.dp, feverColor, RectangleShape)
            )
        }

        // 1. Unified HUD
        GameHUD(
            score = score,
            combo = combo,
            lives = lives,
            progress = progress,
            onPauseClick = onPauseClick
        )

        // 2. The Game Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            GameGrid(
                tiles = tiles,
                onTileClick = onTileTapped,
                onTileBoundsCalculated = { id, rect -> tileBounds[id] = rect }
            )
        }

        // 3. Overlay System
        AnimatedVisibility(
            visible = gameState == GameStateV2.BRIEFING,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BriefingOverlay(
                stage = currentStage,
                selectedBoost = selectedBoost,
                onBoostSelect = onBoostSelect,
                onStart = onStartStage
            )
        }

        AnimatedVisibility(
            visible = gameState == GameStateV2.COUNTDOWN,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CountdownOverlay()
        }

        // 4. Vault (Results) Overlay
        var resultData by remember { mutableStateOf<GameEffectV2.VaultOpened?>(null) }
        
        LaunchedEffect(effects) {
            effects.collect { effect ->
                if (effect is GameEffectV2.VaultOpened) {
                    resultData = effect
                }
            }
        }

        if (gameState == GameStateV2.VAULT && resultData != null) {
            VaultOverlay(
                stars = resultData!!.stars,
                xpEarned = resultData!!.xp,
                coinsEarned = resultData!!.coins,
                onNextStage = onBackClick,
                onReplay = { 
                    resultData = null
                    onLoadStage(currentStage?.id ?: "w1_s1")
                }
            )
        }

        // 5. Particle Effects
        activeParticles.forEach { particle ->
            key(particle.first) {
                ParticleEffect(
                    position = particle.first,
                    color = particle.second,
                    onFinished = { activeParticles.remove(particle) }
                )
            }
        }
    }
}

@Composable
private fun GameHUD(
    score: Int,
    combo: Int,
    lives: Int,
    progress: Float,
    onPauseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Combo Meter
            Column {
                Text(
                    "COMBO",
                    style = FliqTheme.typography.label,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Text(
                    "x$combo",
                    style = FliqTheme.typography.heading.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Score
            Text(
                score.toString().padStart(6, '0'),
                style = FliqTheme.typography.scoreDisplay.copy(fontSize = 32.sp),
                color = Color.White
            )

            // Hearts
            Row(
                modifier = Modifier.clickable(onClick = onPauseClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Text(
                        if (index < lives) "❤" else "♡",
                        color = if (index < lives) Color.Red else Color.White.copy(alpha = 0.3f),
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(4.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.White.copy(alpha = 0.1f),
        )
    }
}

@Composable
private fun GameGrid(
    tiles: List<TileV2>,
    onTileClick: (Int) -> Unit,
    onTileBoundsCalculated: (Int, Rect) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .aspectRatio(1f),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tiles) { tile ->
            TileItem(
                tile = tile, 
                onClick = { onTileClick(tile.id) },
                onBoundsCalculated = { onTileBoundsCalculated(tile.id, it) }
            )
        }
    }
}

@Composable
private fun TileItem(
    tile: TileV2,
    onClick: () -> Unit,
    onBoundsCalculated: (Rect) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when (tile.state) {
            TileState.ACTIVE -> 1f
            TileState.POPPED -> 1.15f
            else -> 0.9f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tile_scale"
    )

    val opacity by animateFloatAsState(
        targetValue = if (tile.state == TileState.ACTIVE || tile.state == TileState.POPPED) 1f else 0.4f,
        label = "tile_opacity"
    )

    val isBomb = tile.type == TileType.BOMB
    val accentColor = if (isBomb) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val containerColor = if (tile.state == TileState.ACTIVE) {
        accentColor.copy(alpha = 0.15f)
    } else {
        Color.White.copy(alpha = 0.05f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .alpha(opacity)
            .onGloballyPositioned { onBoundsCalculated(it.boundsInParent()) }
            .graphicsLayer {
                shadowElevation = if (tile.state == TileState.ACTIVE) 20f else 0f
                shape = RoundedCornerShape(24.dp)
                clip = true
            }
            .background(containerColor)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        if (tile.state == TileState.ACTIVE) accentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = tile.state == TileState.ACTIVE, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (tile.state == TileState.ACTIVE) {
            Box(
                modifier = Modifier
                    .size(if (isBomb) 32.dp else 24.dp)
                    .graphicsLayer {
                        shadowElevation = 30f
                    }
                    .background(accentColor, CircleShape)
            )
            
            val infiniteTransition = rememberInfiniteTransition(label = "tile_glow")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                label = "pulse"
            )
            
            Box(
                modifier = Modifier
                    .size(if (isBomb) 32.dp else 24.dp)
                    .scale(pulseScale)
                    .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape)
            )
        }

        if (tile.state == TileState.POPPED) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(accentColor.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun BriefingOverlay(
    stage: GameStage?,
    selectedBoost: Boost?,
    onBoostSelect: (Boost) -> Unit,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .zIndex(10f),
        contentAlignment = Alignment.Center
    ) {
        FliqCard(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(24.dp),
            elevation = FliqTheme.elevation.overlay
        ) {
            Column(
                modifier = Modifier.padding(FliqTheme.spacing.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STAGE ${stage?.stageNumber ?: 0}",
                    style = FliqTheme.typography.label,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stage?.title?.uppercase() ?: "",
                    style = FliqTheme.typography.heading,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(FliqTheme.spacing.large))
                
                FliqSurface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    showBorder = true
                ) {
                    Text(
                        text = stage?.classType?.name ?: "",
                        style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.large))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stage?.goals?.forEach { goal ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = if (goal.starIndex == 1) Color.White else Gold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (goal.type) {
                                    GoalType.CLEAR -> "Finish the level"
                                    GoalType.ACCURACY -> "Accuracy > ${(goal.targetValue * 100).toInt()}%"
                                    GoalType.COMBO -> "Combo > ${goal.targetValue.toInt()}x"
                                    GoalType.TIME -> "Finish in < ${goal.targetValue.toInt()}s"
                                },
                                style = FliqTheme.typography.body.copy(fontSize = 14.sp),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.large))

                Text(
                    "SELECT BOOST (OPTIONAL)",
                    style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(FliqTheme.spacing.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Boost.entries.forEach { boost ->
                        val isSelected = selectedBoost == boost
                        FliqSurface(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clickable { onBoostSelect(boost) },
                            shape = FliqTheme.shapes.medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                            showBorder = isSelected
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = boost.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    boost.title.uppercase(),
                                    style = FliqTheme.typography.label.copy(fontSize = 8.sp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.extraLarge))

                FliqButton(
                    text = "START ACTION",
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CountdownOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(15f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "START",
            style = FliqTheme.typography.heading.copy(fontSize = 64.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenV2Preview() {
    FliqTheme {
        GameScreenV2(
            gameState = GameStateV2.ACTION,
            tiles = List(16) { TileV2(it, TileType.COIN, TileState.ACTIVE) },
            score = 1250,
            combo = 5,
            lives = 3,
            progress = 0.4f,
            currentStage = null,
            selectedBoost = null,
            effects = flowOf(),
            onTileTapped = {},
            onTileEntered = {},
            onPauseClick = {},
            onBoostSelect = {},
            onStartStage = {},
            onLoadStage = {},
            onBackClick = {}
        )
    }
}
