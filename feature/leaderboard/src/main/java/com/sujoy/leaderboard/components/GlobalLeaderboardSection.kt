package com.sujoy.leaderboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.LeaderboardModel
import com.sujoy.flippy.core.R

@Composable
fun GlobalLeaderboardSection(
    uiState: AppUIState,
    leaderboard: List<LeaderboardModel>
) {
    var selectedDifficultyIndex by remember { mutableIntStateOf(1) }
    val difficulties = listOf("EASY", "NORMAL", "HARD")

    Column(modifier = Modifier.fillMaxSize()) {
        DifficultySelector(
            difficulties = difficulties,
            selectedIndex = selectedDifficultyIndex,
            onDifficultySelected = { selectedDifficultyIndex = it }
        )

        when (uiState) {
            is AppUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is AppUIState.Success -> {
                if (leaderboard.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No one has played in this category yet. Be the first to top the charts!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            PodiumSection(scores = leaderboard.take(3))
                        }

                        if (leaderboard.size > 3) {
                            itemsIndexed(leaderboard.drop(3)) { index, data ->
                                LeaderboardItem(
                                    rank = index + 4,
                                    username = data.username.ifEmpty { data.playerId },
                                    score = data.score,
                                    avatarId = (index % 8) + 1
                                )
                            }
                        }
                    }
                }
            }
            is AppUIState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load leaderboard: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun DifficultySelector(
    difficulties: List<String>,
    selectedIndex: Int,
    onDifficultySelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            difficulties.forEachIndexed { index, difficulty ->
                val isSelected = selectedIndex == index
                val backgroundColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "bg"
                )
                val contentColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "content"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundColor)
                        .clickable { onDifficultySelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = difficulty,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumSection(scores: List<LeaderboardModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        if (scores.size >= 2) {
            PodiumItem(
                data = scores[1],
                rank = 2,
                height = 140.dp,
                color = Color(0xFFC0C0C0),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 1st Place
        if (scores.isNotEmpty()) {
            PodiumItem(
                data = scores[0],
                rank = 1,
                height = 180.dp,
                color = Color(0xFFFFD700),
                modifier = Modifier.weight(1.2f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3rd Place
        if (scores.size >= 3) {
            PodiumItem(
                data = scores[2],
                rank = 3,
                height = 120.dp,
                color = Color(0xFFCD7F32),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PodiumItem(
    data: LeaderboardModel,
    rank: Int,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val avatarId = when(rank) {
            1 -> 1
            2 -> 2
            else -> 3
        }
        val avatarRes = getAvatarResource(avatarId)
        
        Box(contentAlignment = Alignment.TopCenter) {
            Surface(
                modifier = Modifier
                    .size(if (rank == 1) 72.dp else 60.dp)
                    .padding(4.dp),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(2.dp, color),
                color = MaterialTheme.colorScheme.surface
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(32.dp))
                    }
                }
            }
            
            Surface(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomCenter),
                shape = CircleShape,
                color = color
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data.username.ifEmpty { data.playerId },
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Text(
            text = data.score.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            tonalElevation = (4 - rank).dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

private fun getAvatarResource(id: Int): Int? {
    return when (id) {
        1 -> R.drawable.user_avatar_1
        2 -> R.drawable.user_avatar_2
        3 -> R.drawable.user_avatar_3
        4 -> R.drawable.user_avatar_4
        5 -> R.drawable.user_avatar_5
        6 -> R.drawable.user_avatar_6
        7 -> R.drawable.user_avatar_7
        8 -> R.drawable.user_avatar_8
        else -> null
    }
}
