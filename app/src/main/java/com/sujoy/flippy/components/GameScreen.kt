package com.sujoy.flippy.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.flippy.R
import com.sujoy.flippy.models.CardType
import com.sujoy.flippy.models.Tile
import com.sujoy.flippy.repositories.game.SoundRepository
import com.sujoy.flippy.ui.theme.FlippyTheme
import com.sujoy.flippy.utils.GameStatus
import com.sujoy.flippy.utils.SoundPlayer
import com.sujoy.flippy.vm.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel) {

    val tiles by viewModel.tiles.collectAsState()
    val score by viewModel.score.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val status by viewModel.status.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(score = score, lives = lives)
            GameGrid(
                tiles = tiles,
                onTileTapped = { viewModel.onTileTapped(it) },
                modifier = Modifier.weight(1f)
            )
        }

        if (status != GameStatus.PLAYING) {
            GameStatusOverlay(status = status, onPlayAgain = { viewModel.startGame() })
        }
    }
}

@Composable
fun GameHeader(score: Int, lives: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Score Display
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Score: ",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        // Lives Display
        Row(verticalAlignment = Alignment.CenterVertically) {
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
fun GameGrid(tiles: List<Tile>, onTileTapped: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tiles, key = { it.id }) { tile ->
            GameCard(
                isRevealed = tile.isRevealed,
                type = tile.type,
                onClick = { onTileTapped(tile.id) },
                modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}

@Composable
fun GameCard(
    isRevealed: Boolean,
    type: CardType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        label = "rotation"
    )

    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 8 * density
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Front of the card (visible when rotation is 0-90)
            if (rotation <= 90f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            } else { // Back of the card (visible when rotation is 90-180)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        } // Counter-rotate to show content correctly
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    when (type) {
                        CardType.COIN -> ImageContent(R.drawable.ic_coin, "Coin")
                        CardType.BOMB -> ImageContent(R.drawable.ic_bomb, "Bomb")
                        CardType.HIDDEN -> { /* Nothing to show */
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageContent(drawableId: Int, description: String) {
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = description,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    )
}

@Composable
private fun GameStatusOverlay(status: GameStatus, onPlayAgain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = false, onClick = {}), // Block background clicks
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 48.dp, vertical = 32.dp)
        ) {
            val title = if (status == GameStatus.GAME_OVER) "Game Over" else "Tap Fast"
            val buttonText = if (status == GameStatus.READY) "Start" else "Play Again"

            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onPlayAgain,
                contentPadding = PaddingValues(horizontal = 40.dp, vertical = 16.dp)
            ) {
                Text(text = buttonText, fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    // Single instance of SoundRepository for all sounds
    lateinit var soundRepository: SoundRepository

    FlippyTheme {
        val context = LocalContext.current
        val soundPlayer = SoundPlayer(context)
        GameScreen(
            viewModel = GameViewModel(soundRepository)
        )
    }
}
