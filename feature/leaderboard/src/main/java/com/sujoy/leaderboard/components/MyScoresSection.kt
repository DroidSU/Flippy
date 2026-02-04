package com.sujoy.leaderboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.flippy.database.MatchHistory

@Composable
fun MyScoresSection() {
    // Using MatchHistory as dummy data
    val myScores = listOf(
        MatchHistory(id = "1", playerId = "player1", score = 2450, difficulty = "NORMAL", gameDuration = 60000, timestamp = System.currentTimeMillis(), correctTaps = 100, totalTaps = 110, totalReflexTime = 500, perfectStreak = 20),
        MatchHistory(id = "2", playerId = "player1", score = 2100, difficulty = "NORMAL", gameDuration = 60000, timestamp = System.currentTimeMillis(), correctTaps = 90, totalTaps = 100, totalReflexTime = 550, perfectStreak = 15),
        MatchHistory(id = "3", playerId = "player1", score = 1850, difficulty = "NORMAL", gameDuration = 60000, timestamp = System.currentTimeMillis(), correctTaps = 80, totalTaps = 95, totalReflexTime = 600, perfectStreak = 10),
        MatchHistory(id = "4", playerId = "player1", score = 1500, difficulty = "NORMAL", gameDuration = 60000, timestamp = System.currentTimeMillis(), correctTaps = 70, totalTaps = 90, totalReflexTime = 650, perfectStreak = 5),
        MatchHistory(id = "5", playerId = "player1", score = 1200, difficulty = "NORMAL", gameDuration = 60000, timestamp = System.currentTimeMillis(), correctTaps = 60, totalTaps = 85, totalReflexTime = 700, perfectStreak = 3)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MyScoreHeader(bestScore = myScores.firstOrNull()?.score ?: 0)
        }

        item {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        itemsIndexed(myScores) { index, data ->
            LeaderboardItem(
                rank = index + 1,
                username = "You",
                score = data.score,
                avatarId = 1,
                isCurrentUser = true
            )
        }
    }
}

@Composable
fun MyScoreHeader(bestScore: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Personal Best",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = bestScore.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    letterSpacing = 2.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Games", value = "12")
                StatItem(label = "Avg Score", value = "1850")
                StatItem(label = "Rank", value = "#42")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        )
    }
}
