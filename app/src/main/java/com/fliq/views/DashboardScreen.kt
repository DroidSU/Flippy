package com.fliq.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.Badge
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.NeonPurple
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.R
import com.fliq.game_engine.models.Challenge
import com.fliq.game_engine.ui.MeshBackground
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DashboardScreen(
    userName: String = "",
    highestScore: Int = 0,
    accuracy: Double = 0.0,
    unlockedBadges: List<Badge> = emptyList(),
    onChallengeSelected: (Challenge) -> Unit,
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gameColors.backgroundGradient))
        ) {
            MeshBackground(streak = 0)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                DashboardHeader(
                    userName = userName,
                    onProfileClick = onProfileClick
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ChallengeTile(
                            challenge = Challenge.ZEN_MODE,
                            isFeatured = true,
                            onClick = { onChallengeSelected(Challenge.ZEN_MODE) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ChallengeTile(
                                challenge = Challenge.SPEED_RUN,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.SPEED_RUN) }
                            )
                            ChallengeTile(
                                challenge = Challenge.MIRAGE,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.MIRAGE) }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ChallengeTile(
                                challenge = Challenge.MINEFIELD,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.MINEFIELD) }
                            )
                            ChallengeTile(
                                challenge = Challenge.FRENZY,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.FRENZY) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            // Navigation Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                DashboardNavBar(
                    onLeaderboardClick = onLeaderboardClick,
                    onAchievementsClick = onAchievementsClick,
                    onSettingsClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    onProfileClick: () -> Unit
) {
    var displayUser by remember { mutableStateOf("") }
    val fullText = userName.ifEmpty { "Gamer" }

    LaunchedEffect(userName) {
        displayUser = ""
        fullText.forEach { char ->
            delay(Random.nextLong(30, 80))
            displayUser += char
        }
    }

    Row(
        modifier = Modifier
            .padding(top = 32.dp, start = 24.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "READY TO PLAY?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Hey $displayUser!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Profile Icon
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun ChallengeTile(
    challenge: Challenge,
    modifier: Modifier = Modifier,
    isFeatured: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val accentColor = when (challenge) {
        Challenge.ZEN_MODE -> Color(0xFF2DD4BF)
        Challenge.SPEED_RUN -> NeonCyan
        Challenge.MIRAGE -> NeonPurple
        Challenge.MINEFIELD -> BombRed
        Challenge.FRENZY -> Gold
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isFeatured) 160.dp else 130.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(BgSlate.copy(alpha = 0.6f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Subtle animated border for featured
        if (isFeatured || isPressed) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(
                    path = Path().apply {
                        addRoundRect(androidx.compose.ui.geometry.RoundRect(0f, 0f, size.width, size.height, 24.dp.toPx(), 24.dp.toPx()))
                    },
                    color = accentColor.copy(alpha = 0.3f),
                    style = Stroke(width = 2.dp.toPx())
                )
                
                val angleRad = Math.toRadians(borderRotation.toDouble())
                val centerX = size.width / 2
                val centerY = size.height / 2
                val spotX = centerX + (size.width / 2) * Math.cos(angleRad).toFloat()
                val spotY = centerY + (size.height / 2) * Math.sin(angleRad).toFloat()
                
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to accentColor.copy(alpha = 0.4f),
                        1.0f to Color.Transparent,
                        center = Offset(spotX, spotY),
                        radius = 120f
                    ),
                    radius = 120f,
                    center = Offset(spotX, spotY)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Use Game Icons for a more authentic game feel
            Box(modifier = Modifier.size(if (isFeatured) 40.dp else 32.dp)) {
                when (challenge) {
                    Challenge.ZEN_MODE -> Icon(Icons.Default.SelfImprovement, null, tint = accentColor, modifier = Modifier.fillMaxSize())
                    Challenge.SPEED_RUN -> Icon(Icons.Default.Bolt, null, tint = accentColor, modifier = Modifier.fillMaxSize())
                    Challenge.MINEFIELD -> Image(painterResource(id = R.drawable.ic_bomb), null, modifier = Modifier.fillMaxSize())
                    Challenge.FRENZY -> Image(painterResource(id = R.drawable.ic_coin), null, modifier = Modifier.fillMaxSize())
                    Challenge.MIRAGE -> Icon(Icons.Default.VisibilityOff, null, tint = accentColor, modifier = Modifier.fillMaxSize())
                }
            }

            Column {
                Text(
                    text = challenge.title,
                    style = (if (isFeatured) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium).copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isFeatured) {
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardNavBar(
    onLeaderboardClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .height(72.dp),
        shape = RoundedCornerShape(36.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(icon = Icons.Default.EmojiEvents, label = "TROPHIES", onClick = onAchievementsClick)
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
            NavBarItem(icon = Icons.Default.Leaderboard, label = "RANKINGS", onClick = onLeaderboardClick)
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
            NavBarItem(icon = Icons.Default.Settings, label = "OPTIONS", onClick = onSettingsClick)
        }
    }
}

@Composable
fun NavBarItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, label = "scale")

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isPressed) 1f else 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isPressed) 1f else 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    FliqTheme {
        DashboardScreen(
            userName = "Sujoy",
            highestScore = 0,
            accuracy = 0.0,
            onLeaderboardClick = {},
            onProfileClick = {},
            onSettingsClick = {},
            onAchievementsClick = {},
            onChallengeSelected = {}
        )
    }
}
