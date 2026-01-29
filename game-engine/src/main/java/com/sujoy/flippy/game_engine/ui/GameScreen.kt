package com.sujoy.flippy.game_engine.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.Tile

@Composable
fun GameScreen(
    tiles: List<Tile>,
    score: Int,
    lives: Int,
    status: GameStatus,
    onTileTapped: (Int) -> Unit,
    onPlayClick: () -> Unit,
    onResetGame: () -> Unit
) {
    var showGameOverOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(status) {
        if (status == GameStatus.GAME_OVER) {
            showGameOverOverlay = true
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(score = score, lives = lives)
            GameGrid(
                tiles = tiles,
                onTileTapped = onTileTapped,
                modifier = Modifier.weight(1f)
            )

            if (status != GameStatus.PLAYING) {
                PlayButton(
                    status = status,
                    onPlayClick = onPlayClick,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            } else {
                Spacer(modifier = Modifier.weight(0.5f))
            }
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

@Composable
private fun GameHeader(score: Int, lives: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Score",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            repeat(lives.coerceAtLeast(0)) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Life",
                    tint = Color.Red,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun GameGrid(tiles: List<Tile>, onTileTapped: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tiles, key = { it.id }) { tile ->
            GameCard(
                isRevealed = tile.isRevealed,
                type = tile.type,
                onClick = { onTileTapped(tile.id) },
                modifier = Modifier
                    .aspectRatio(1f)
                    .shadow(
                        elevation = if (tile.isRevealed) 2.dp else 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}

@Composable
fun PlayButton(status: GameStatus, onPlayClick: () -> Unit, modifier: Modifier = Modifier) {
    val isVisible = status == GameStatus.READY || status == GameStatus.GAME_OVER
    val buttonElevation by animateDpAsState(if (isVisible) 12.dp else 0.dp, label = "button_elevation")
    val text = if (status == GameStatus.READY) "Start" else "Play Again"

    Button(
        onClick = onPlayClick,
        modifier = modifier
            .size(80.dp)
            .shadow(elevation = buttonElevation, shape = CircleShape),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = text,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
