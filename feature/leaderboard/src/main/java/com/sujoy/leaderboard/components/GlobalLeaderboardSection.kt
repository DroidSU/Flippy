package com.sujoy.leaderboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GlobalLeaderboardSection() {
    var selectedDifficultyIndex by remember { mutableIntStateOf(1) } // Default to Normal
    val difficulties = listOf("EASY", "NORMAL", "HARD")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedDifficultyIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedDifficultyIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedDifficultyIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            divider = {}
        ) {
            difficulties.forEachIndexed { index, difficulty ->
                Tab(
                    selected = selectedDifficultyIndex == index,
                    onClick = { selectedDifficultyIndex = index },
                    text = {
                        Text(
                            text = difficulty,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedDifficultyIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Placeholder data based on difficulty
        val globalScores = when (selectedDifficultyIndex) {
            0 -> listOf(
                ScoreData("QuickTap", 3200, 2),
                ScoreData("EasyWin", 3150, 5),
                ScoreData("ChillPlayer", 3000, 8)
            )
            1 -> listOf(
                ScoreData("ProFlippy", 5400, 1),
                ScoreData("MasterMind", 5200, 3),
                ScoreData("ReflexKing", 5100, 4),
                ScoreData("Speedy", 4800, 6)
            )
            else -> listOf(
                ScoreData("GodMode", 9800, 7),
                ScoreData("Legend", 9500, 2),
                ScoreData("InsaneTaps", 9200, 5)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(globalScores) { index, data ->
                LeaderboardItem(
                    rank = index + 1,
                    username = data.username,
                    score = data.score,
                    avatarId = data.avatarId
                )
            }
        }
    }
}
