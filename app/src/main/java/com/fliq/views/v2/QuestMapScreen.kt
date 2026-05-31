package com.fliq.views.v2

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.components.FliqSurface
import com.fliq.core.theme.components.FliqTopBar
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.v2.StageManager

@Composable
fun QuestMapScreen(
    currentStageId: String,
    stageProgress: Map<String, Int>, // stageId -> stars
    xp: Int,
    coins: Int,
    onStageClick: (GameStage) -> Unit,
    onBackClick: () -> Unit,
) {
    val stages = StageManager.world1
    
    val infiniteTransition = rememberInfiniteTransition(label = "wire_glow")
    val wirePulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            QuestMapTopBar(xp, coins, onBackClick)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Draw the "Neon Wire" background
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = wirePulse), Color.Transparent)
                        )
                    )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(64.dp)
            ) {
                items(stages) { stage ->
                    val stars = stageProgress[stage.id] ?: 0
                    val index = stages.indexOf(stage)
                    val isUnlocked = (stage.stageNumber == 1) || (index > 0 && (stageProgress[stages[index - 1].id] ?: 0) > 0)
                    
                    StageNode(
                        stage = stage,
                        stars = stars,
                        isUnlocked = isUnlocked,
                        isCurrent = stage.id == currentStageId,
                        onClick = { if (isUnlocked) onStageClick(stage) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestMapTopBar(
    xp: Int,
    coins: Int,
    onBackClick: () -> Unit
) {
    FliqTopBar(
        title = "SHADOW DISTRICT",
        onBackClick = onBackClick,
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                // XP Chip
                FliqSurface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    showBorder = true
                ) {
                    Text(
                        text = "${xp} XP",
                        style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Coins Chip
                FliqSurface(
                    shape = CircleShape,
                    color = Gold.copy(alpha = 0.1f),
                    showBorder = true
                ) {
                    Text(
                        text = "🪙 $coins",
                        style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Gold
                    )
                }
            }
        }
    )
}

@Composable
private fun StageNode(
    stage: GameStage,
    stars: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = isUnlocked, onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (isCurrent) 80.dp else 64.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
        ) {
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            } else {
                Text(
                    stage.stageNumber.toString(),
                    style = FliqTheme.typography.heading.copy(fontSize = 24.sp),
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            // Glow effect for current stage
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                )
            }
        }

        if (isUnlocked) {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                repeat(3) { index ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (index < stars) Color.Yellow else Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
        
        Text(
            stage.title.uppercase(),
            style = FliqTheme.typography.label.copy(fontSize = 10.sp),
            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
