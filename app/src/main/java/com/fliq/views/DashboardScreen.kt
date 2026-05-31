package com.fliq.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
import com.fliq.core.theme.components.FliqButton
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqSurface
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
        FliqSurface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RectangleShape
        ) {
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
                        contentPadding = PaddingValues(
                            horizontal = FliqTheme.spacing.screenPadding,
                            vertical = FliqTheme.spacing.screenPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
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
                                horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
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
                                horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
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
                            Spacer(modifier = Modifier.height(FliqTheme.spacing.huge * 2))
                        }
                    }
                }

                // Navigation Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = FliqTheme.spacing.extraLarge)
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
            .padding(
                top = FliqTheme.spacing.extraLarge,
                start = FliqTheme.spacing.screenPadding,
                end = FliqTheme.spacing.medium
            )
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
                    .width(FliqTheme.spacing.extraSmall)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(FliqTheme.spacing.elementSpacing))
            Column {
                Text(
                    text = "READY TO PLAY?",
                    style = FliqTheme.typography.label,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Hey $displayUser!",
                    style = FliqTheme.typography.heading,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(FliqTheme.spacing.elementSpacing))

        // Profile Icon
        FliqSurface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            elevation = FliqTheme.elevation.low,
            showBorder = true
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
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
        animationSpec = FliqTheme.motion.pressAnimation,
        label = "scale"
    )

    val accentColor = when (challenge) {
        Challenge.ZEN_MODE -> Color(0xFF2DD4BF)
        Challenge.SPEED_RUN -> NeonCyan
        Challenge.MIRAGE -> NeonPurple
        Challenge.MINEFIELD -> BombRed
        Challenge.FRENZY -> Gold
    }

    FliqCard(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isFeatured) 180.dp else 130.dp)
            .scale(scale),
        backgroundColor = BgSlate.copy(alpha = 0.6f),
        contentPadding = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                            addRoundRect(
                                androidx.compose.ui.geometry.RoundRect(
                                    0f, 0f, size.width, size.height,
                                    24.dp.toPx(), 24.dp.toPx()
                                )
                            )
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

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(FliqTheme.spacing.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
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

                    Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))

                    Text(
                        text = challenge.title,
                        style = if (isFeatured) FliqTheme.typography.heading else FliqTheme.typography.subHeading,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isFeatured) {
                        Text(
                            text = challenge.description,
                            style = FliqTheme.typography.body.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                if (isFeatured) {
                    FliqButton(
                        text = "PLAY",
                        onClick = onClick,
                        modifier = Modifier.height(44.dp),
                        containerColor = accentColor,
                        contentColor = Color.Black
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
    FliqSurface(
        modifier = Modifier
            .padding(horizontal = FliqTheme.spacing.screenPadding)
            .height(72.dp),
        shape = RoundedCornerShape(36.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
        elevation = FliqTheme.elevation.medium,
        showBorder = true
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = FliqTheme.spacing.medium),
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
    val scale by animateFloatAsState(
        if (isPressed) 0.9f else 1f,
        animationSpec = FliqTheme.motion.pressAnimation,
        label = "scale"
    )

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = FliqTheme.spacing.elementSpacing, vertical = FliqTheme.spacing.small)
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
        Spacer(modifier = Modifier.height(FliqTheme.spacing.extraSmall))
        Text(
            text = label,
            style = FliqTheme.typography.label.copy(fontSize = 9.sp),
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
