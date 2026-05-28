package com.fliq.leaderboard.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.AppUIState
import com.fliq.common.LeaderboardModel
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.NeonPurple
import com.fliq.core.theme.RankBronze
import com.fliq.core.theme.RankGold
import com.fliq.core.theme.RankSilver
import com.fliq.core.theme.gameColors
import com.fliq.core.util.ChamferedCornerShape
import com.fliq.game_engine.models.Challenge
import com.fliq.game_engine.ui.MeshBackground

@Composable
fun LeaderboardScreen(
    uiState: AppUIState,
    leaderboard: List<LeaderboardModel>,
    selectedChallenge: Challenge,
    currentUserId: String,
    onChallengeSelected: (Challenge) -> Unit,
    onBackClick: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors
    val accentColor = when (selectedChallenge) {
        Challenge.ZEN_MODE -> Color(0xFF2DD4BF)
        Challenge.SPEED_RUN -> NeonCyan
        Challenge.MIRAGE -> NeonPurple
        Challenge.MINEFIELD -> BombRed
        Challenge.FRENZY -> Gold
        Challenge.SURGE -> NeonCyan
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient))
    ) {
        MeshBackground(streak = 0)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                LeaderboardTopBar(onBackClick = onBackClick)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. Challenge Selector
                ChallengeSelectorTabs(
                    selectedChallenge = selectedChallenge,
                    onChallengeSelected = onChallengeSelected
                )

                if (uiState is AppUIState.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (leaderboard.isEmpty()) {
                    EmptyLeaderboardState(accentColor = accentColor)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        // 2. Champions Podium
                        if (leaderboard.isNotEmpty()) {
                            item {
                                ChampionsPodium(
                                    topThree = leaderboard.take(3)
                                )
                            }
                        }

                        // 3. Global Rankings (4+)
                        val remainingRankings = if (leaderboard.size > 3) leaderboard.drop(3) else emptyList()
                        itemsIndexed(remainingRankings) { index, item ->
                            RankingRowItem(
                                rank = index + 4,
                                model = item,
                                isCurrentUser = item.playerId == currentUserId,
                                accentColor = accentColor
                            )
                        }
                    }
                }
            }

            // 4. Fixed Personal Rank HUD
            val myRankItem = leaderboard.find { it.playerId == currentUserId }
            val myRankIndex = if (myRankItem != null) leaderboard.indexOf(myRankItem) + 1 else -1

            if (myRankIndex != -1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    PersonalRankHUD(
                        rank = myRankIndex,
                        model = myRankItem!!,
                        accentColor = accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f),
            modifier = Modifier.size(44.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(20.dp))
        
        Column {
            Text(
                "GLOBAL RANKINGS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White
            )
            Text(
                "COMPETE WITH THE WORLD",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ChallengeSelectorTabs(
    selectedChallenge: Challenge,
    onChallengeSelected: (Challenge) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        items(Challenge.entries.toTypedArray()) { challenge ->
            val isSelected = challenge == selectedChallenge
            val accentColor = when (challenge) {
                Challenge.ZEN_MODE -> Color(0xFF2DD4BF)
                Challenge.SPEED_RUN -> NeonCyan
                Challenge.MIRAGE -> NeonPurple
                Challenge.MINEFIELD -> BombRed
                Challenge.FRENZY -> Gold
                Challenge.SURGE -> NeonCyan
            }

            Surface(
                onClick = { onChallengeSelected(challenge) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                border = BorderStroke(
                    1.dp, 
                    if (isSelected) accentColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when(challenge) {
                            Challenge.ZEN_MODE -> Icons.Default.SelfImprovement
                            Challenge.SPEED_RUN -> Icons.Default.Bolt
                            Challenge.MIRAGE -> Icons.Default.VisibilityOff
                            Challenge.MINEFIELD -> Icons.Default.EmojiEvents
                            Challenge.FRENZY -> Icons.Default.EmojiEvents
                            Challenge.SURGE -> Icons.Default.Bolt
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChampionsPodium(
    topThree: List<LeaderboardModel>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2
        if (topThree.size >= 2) {
            PodiumCard(
                model = topThree[1],
                rank = 2,
                modifier = Modifier.weight(1f),
                height = 160.dp,
                accentColor = RankSilver
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Rank 1
        if (topThree.isNotEmpty()) {
            PodiumCard(
                model = topThree[0],
                rank = 1,
                modifier = Modifier.weight(1.2f),
                height = 200.dp,
                accentColor = RankGold,
                isApex = true
            )
        }

        // Rank 3
        if (topThree.size >= 3) {
            PodiumCard(
                model = topThree[2],
                rank = 3,
                modifier = Modifier.weight(1f),
                height = 140.dp,
                accentColor = RankBronze
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumCard(
    model: LeaderboardModel,
    rank: Int,
    modifier: Modifier,
    height: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    isApex: Boolean = false
) {
    val avatarRes = UtilityMethods.getAvatarResource(model.avatarId)
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isApex) {
                // Glow for #1
                Surface(
                    modifier = Modifier.size(80.dp).graphicsLayer { alpha = 0.3f },
                    shape = CircleShape,
                    color = accentColor,
                    shadowElevation = 40.dp
                ) {}
            }
            
            Surface(
                modifier = Modifier.size(if (isApex) 72.dp else 56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, accentColor)
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth().height(height)) {
            // 3D Base
            Surface(
                modifier = Modifier.fillMaxSize().offset(y = 4.dp).alpha(0.3f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.scrim
            ) {}
            
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = BgSlate.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = accentColor
                    )
                    Text(
                        text = model.username.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = model.totalScore.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingRowItem(
    rank: Int,
    model: LeaderboardModel,
    isCurrentUser: Boolean,
    accentColor: Color
) {
    val avatarRes = UtilityMethods.getAvatarResource(model.avatarId)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        // Physical Shadow
        Surface(
            modifier = Modifier.fillMaxWidth().height(72.dp).offset(y = 2.dp).alpha(0.2f),
            shape = ChamferedCornerShape(12.dp),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier.fillMaxWidth().height(72.dp),
            shape = ChamferedCornerShape(12.dp),
            color = if (isCurrentUser) accentColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f),
            border = BorderStroke(
                1.dp, 
                if (isCurrentUser) accentColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Badge
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.width(36.dp)
                )

                // Avatar
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Username
                Text(
                    text = model.username,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                // Score
                Text(
                    text = model.totalScore.toString().padStart(3, '0'),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        shadow = androidx.compose.ui.graphics.Shadow(accentColor.copy(alpha = 0.3f), blurRadius = 8f)
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PersonalRankHUD(
    rank: Int,
    model: LeaderboardModel,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .graphicsLayer { 
                shadowElevation = 32f
            },
        shape = ChamferedCornerShape(20.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(2.dp, accentColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "YOUR RANKING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = accentColor.copy(alpha = 0.6f)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White
                    )
                    Text(
                        text = " GLOBAL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "PERSONAL BEST",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = accentColor.copy(alpha = 0.6f)
                )
                Text(
                    text = model.totalScore.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        shadow = androidx.compose.ui.graphics.Shadow(accentColor.copy(alpha = 0.5f), blurRadius = 12f)
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun EmptyLeaderboardState(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Physical Glass Card
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Surface(
                    modifier = Modifier.fillMaxSize().offset(y = 4.dp).alpha(0.3f),
                    shape = ChamferedCornerShape(24.dp),
                    color = Color.Black
                ) {}
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = ChamferedCornerShape(24.dp),
                    color = BgSlate.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = accentColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NO DATA FOUND",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "BE THE FIRST TO CLAIM A RANKING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    FliqTheme {
        LeaderboardScreen(
            uiState = AppUIState.Success,
            leaderboard = listOf(
                LeaderboardModel("1", 2500, "Apex Predator", 1),
                LeaderboardModel("2", 2100, "Reflex King", 2),
                LeaderboardModel("3", 1950, "Quick Silver", 3),
                LeaderboardModel("4", 1800, "Gamer One", 4)
            ),
            selectedChallenge = Challenge.SPEED_RUN,
            currentUserId = "4",
            onChallengeSelected = {},
            onBackClick = {}
        )
    }
}
