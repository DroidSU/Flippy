package com.sujoy.leaderboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MyScoresSection() {
    // Placeholder data
    val myScores = listOf(
        ScoreData("Player One", 2450, 1),
        ScoreData("Player One", 2100, 1),
        ScoreData("Player One", 1850, 1),
        ScoreData("Player One", 1500, 1),
        ScoreData("Player One", 1200, 1)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Your Personal Bests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        itemsIndexed(myScores) { index, data ->
            LeaderboardItem(
                rank = index + 1,
                username = data.username,
                score = data.score,
                avatarId = data.avatarId,
                isCurrentUser = true
            )
        }
    }
}

data class ScoreData(val username: String, val score: Int, val avatarId: Int)
