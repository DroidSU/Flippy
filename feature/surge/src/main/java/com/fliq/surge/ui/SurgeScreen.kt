package com.fliq.surge.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.gameColors
import com.fliq.core.util.ChamferedCornerShape
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.Tile
import com.fliq.game_engine.ui.EffectsOverlay
import com.fliq.game_engine.ui.MeshBackground
import com.fliq.game_engine.ui.PlayButtonComponent
import com.fliq.surge.ui.components.FluidProgressBar
import com.fliq.surge.ui.components.SurgeGameGrid
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun SurgeScreen(
    tiles: List<Tile>,
    status: GameStatus,
    reservoirLevel: Float,
    score: Int,
    onTileTapped: (Int, Offset?) -> Unit,
    onPlayClick: () -> Unit,
    onNavigateBack: () -> Unit,
    effects: SharedFlow<GameEffect>? = null
) {
    val gameColors = MaterialTheme.gameColors
    val isPlaying = status == GameStatus.PLAYING

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gameColors.backgroundGradient))
        ) {
            MeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .blur(if (status != GameStatus.PLAYING) 12.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Standard Top Bar
                SurgeTopBar(onBackClick = onNavigateBack)

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                SurgeStatsHeader(score = score, level = reservoirLevel)

                Spacer(modifier = Modifier.height(24.dp))

                // The Fluid Progress Bar (Horizontal)
                FluidProgressBar(
                    progress = reservoirLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(48.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Grid Area
                SurgeGameGrid(
                    tiles = tiles,
                    onTileTapped = onTileTapped,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Play Button at the bottom
                PlayButtonComponent(
                    status = status,
                    onAction = { if (!isPlaying) onPlayClick() },
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            // Overlays
            EffectsOverlay(effects = effects)

            AnimatedVisibility(
                visible = status == GameStatus.READY,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                SurgeInstructionsOverlay(onStart = onPlayClick)
            }

            AnimatedVisibility(
                visible = status == GameStatus.GAME_OVER,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SurgeGameOverOverlay(
                    score = score,
                    won = reservoirLevel >= 1f,
                    onRetry = onPlayClick,
                    onExit = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun SurgeTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SURGE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = NeonCyan
            )
            Text(
                text = "HIGH PRESSURE MODE",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = NeonCyan.copy(alpha = 0.5f)
            )
        }

        Box(modifier = Modifier.size(44.dp))
    }
}

@Composable
private fun SurgeStatsHeader(score: Int, level: Float) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = ChamferedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(label = "SCORE", value = score.toString().padStart(3, '0'))
            StatItem(
                label = "LEVEL",
                value = "${(level * 100).toInt()}%",
                color = if (level > 0.8f) Color.Red else NeonCyan
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            ),
            color = color
        )
    }
}

@Composable
private fun SurgeInstructionsOverlay(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = ChamferedCornerShape(32.dp),
            color = Color.White.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Info, 
                    null, 
                    tint = NeonCyan, 
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "SURGE RULES",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                InstructionRow("Tap the active tiles as fast as you can to fill the bar.")
                InstructionRow("The bar drains constantly. Maintain your speed to win.")
                InstructionRow("Avoid hitting bombs, they will cause a huge drain.")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(
                        "START GAME", 
                        color = Color.Black, 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonCyan)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SurgeGameOverOverlay(
    score: Int,
    won: Boolean,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (won) "SURGE COMPLETED" else "GAME OVER",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = if (won) Color.Green else Color.Red
            )
            
            Text(
                text = "FINAL SCORE: $score",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = if (won) Color.Green else NeonCyan),
                modifier = Modifier.width(200.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("PLAY AGAIN", color = Color.Black, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onExit) {
                Text("EXIT", color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurgeScreenPreview() {
    FliqTheme {
        SurgeScreen(
            tiles = List(16) { Tile(it) },
            status = GameStatus.PLAYING,
            reservoirLevel = 0.4f,
            score = 12,
            onTileTapped = { _, _ -> },
            onPlayClick = {},
            onNavigateBack = {}
        )
    }
}
