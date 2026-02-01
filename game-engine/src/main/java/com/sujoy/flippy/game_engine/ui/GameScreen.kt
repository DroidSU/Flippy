package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.game_engine.models.Difficulty
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.Tile

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
    onTileTapped: (Int) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onRulesDismissed: (Boolean) -> Unit,
    onHelpClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    var showGameOverOverlay by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
            MeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .blur(if (showGameOverOverlay || showRules || isMenuVisible || isPaused) 16.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Custom Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { isMenuVisible = true },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }

                    Surface(
                        onClick = onHelpClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Default.HelpOutline, contentDescription = "Help")
                        }
                    }
                }

                GameHeader(score = score, lives = lives, gameTime = gameTime)

                Spacer(modifier = Modifier.height(12.dp))

                DifficultySelector(
                    currentDifficulty = difficulty,
                    onDifficultyChange = onDifficultyChange,
                    enabled = status == GameStatus.READY
                )

                Spacer(modifier = Modifier.height(12.dp))

                GameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (status == GameStatus.READY) {
                    LeaderboardSection(
                        leaderboard = leaderboard,
                        modifier = Modifier.padding(bottom = 100.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Floating Play Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
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

            // Creative Side Menu Overlay
            SideNavigationMenu(
                isVisible = isMenuVisible,
                onDismiss = { isMenuVisible = false },
                onSignOutClick = {
                    isMenuVisible = false
                    onSignOutClick()
                }
            )

            if (showRules) {
                FlippyRulesDialog(onDismiss = onRulesDismissed)
            }

            GameStatusOverlay(
                visible = showGameOverOverlay,
                score = score,
                onDismiss = {
                    showGameOverOverlay = false
                    onResetGame()
                }
            )
        }
    }
}

@Composable
fun SideNavigationMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
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
                    .fillMaxWidth(0.75f),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Text(
                        text = "FLIPPY",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Master your reflexes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    NavigationMenuItem(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        onClick = {}
                    )
                    
                    NavigationMenuItem(
                        icon = Icons.Default.Leaderboard,
                        label = "Leaderboard",
                        onClick = {}
                    )
                    
                    NavigationMenuItem(
                        icon = Icons.Default.Settings,
                        label = "Preferences",
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )

                    NavigationMenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "Sign Out",
                        onClick = onSignOutClick,
                        color = Color(0xFFFF4B4B)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
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
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val color by animateColorAsState(
        targetValue = if (status == GameStatus.PLAYING) Color(0xFFFF4B4B) else MaterialTheme.colorScheme.primary,
        label = "color"
    )

    Surface(
        modifier = modifier
            .size(68.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 4.dp else 12.dp,
                shape = CircleShape,
                ambientColor = color,
                spotColor = color
            ),
        shape = CircleShape,
        color = color,
        onClick = onAction,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (status == GameStatus.PLAYING) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * This composable is used to display the top three scores of the current player
 */
@Composable
private fun LeaderboardSection(
    leaderboard: List<MatchHistory>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Leaderboard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "High Scores",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
        ) {
            if (leaderboard.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches played yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    leaderboard.forEachIndexed { index, match ->
                        LeaderboardItem(index + 1, match)
                        if (index < leaderboard.size - 1 && index < 9) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .alpha(if (enabled) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { diff ->
            val isSelected = currentDifficulty == diff
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable(enabled = enabled) { onDifficultyChange(diff) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.6f
                ),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                        alpha = 0.1f
                    )
                ),
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = diff.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MeshBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(80.dp)
            .alpha(0.3f)
    ) {
        drawCircle(
            color = Color(0xFFFFA900).copy(alpha = 0.4f),
            radius = size.width / 1.5f,
            center = Offset(size.width / 2 + xOffset, size.height / 4)
        )
        drawCircle(
            color = Color(0xFF1976D2).copy(alpha = 0.3f),
            radius = size.width / 2f,
            center = Offset(size.width / 4 - xOffset, size.height / 1.2f)
        )
    }
}

@Composable
private fun GameHeader(score: Int, lives: Int, gameTime: Long) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCORE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stopwatch in the middle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = UtilityMethods.formatTime(gameTime),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Life",
                        tint = if (index < lives) Color(0xFFFF4B4B) else Color.LightGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameGrid(tiles: List<Tile>, onTileTapped: (Int) -> Unit, modifier: Modifier = Modifier) {
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
                        GameCard(
                            isRevealed = tile.isRevealed,
                            type = tile.type,
                            onClick = { onTileTapped(tile.id) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
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
    FlippyTheme {
        GameScreen(
            tiles = List(16) { Tile(it) },
            score = 10,
            lives = 3,
            status = GameStatus.PLAYING,
            difficulty = Difficulty.NORMAL,
            gameTime = 1,
            leaderboard = emptyList(),
            showRules = false,
            isPaused = false,
            onTileTapped = {},
            onPlayClick = {},
            onResetGame = {},
            onDifficultyChange = {},
            onRulesDismissed = {},
            onHelpClick = {},
            onSignOutClick = {}
        )
    }
}
