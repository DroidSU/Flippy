package com.sujoy.flippy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.ui.theme.FlippyTheme
import com.sujoy.flippy.utils.GameStatus
import com.sujoy.flippy.vm.GameViewModel

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippyTheme(dynamicColor = false) { // Use our custom theme
                App(viewModel = gameViewModel)
            }
        }
    }
}

@Composable
private fun App(viewModel: GameViewModel) {
    val tiles by viewModel.tiles
    val score by viewModel.score
    val status by viewModel.status

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
            // Top UI for Score
            ScoreDisplay(score = score)

            // The main game grid
            Box(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(tiles) { tile ->
                        GlassmorphicFlippyCard(
                            isFlipped = tile.isFlipped,
                            onFlip = { viewModel.onTileFlipped(tile.id) },
                            front = { FrontContent(text = "?") },
                            back = {
                                if (tile.isImage) {
                                    ImageContent()
                                } else {
                                    BackContent(value = tile.value)
                                }
                            },
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            }
        }

        // Show game over dialog
        if (status != GameStatus.PLAYING) {
            GameStatusDialog(status = status, onDismiss = { viewModel.resetGame() })
        }
    }
}

@Composable
fun ScoreDisplay(score: Int) {
    Row(
        modifier = Modifier.padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Score: ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "$score",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (score > 10) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun FrontContent(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun BackContent(value: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ImageContent() {
    Image(
        painter = painterResource(id = R.drawable.ic_star),
        contentDescription = "Winning Tile",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun GameStatusDialog(status: GameStatus, onDismiss: () -> Unit) {
    val title = if (status == GameStatus.WON) "You Won!" else "Game Over"
    val message = if (status == GameStatus.WON) "You found the image! Congratulations!" else "You ran out of points. Better luck next time!"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Play Again")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    FlippyTheme { // Use our custom theme
        App(viewModel = GameViewModel())
    }
}
