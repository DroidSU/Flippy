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
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.Badge
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.gameColors
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
                KineticWelcome(
                    userName = userName,
                    onProfileClick = onProfileClick
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        ChallengeTile(
                            challenge = Challenge.SPEED_RUN,
                            isFeatured = true,
                            onClick = { onChallengeSelected(Challenge.SPEED_RUN) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            ChallengeTile(
                                challenge = Challenge.MIRAGE,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.MIRAGE) }
                            )
                            ChallengeTile(
                                challenge = Challenge.BLACKOUT,
                                modifier = Modifier.weight(1f),
                                onClick = { onChallengeSelected(Challenge.BLACKOUT) }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
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

            // Prism Navigation Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                PrismNavBar(
                    onLeaderboardClick = onLeaderboardClick,
                    onAchievementsClick = onAchievementsClick,
                    onSettingsClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
fun KineticWelcome(
    userName: String,
    onProfileClick: () -> Unit
) {
    var displayUser by remember { mutableStateOf("") }
    val fullText = userName.ifEmpty { "OPERATOR" }.uppercase()

    LaunchedEffect(userName) {
        displayUser = ""
        fullText.forEach { char ->
            delay(Random.nextLong(30, 100))
            displayUser += char
        }
    }

    Row(
        modifier = Modifier
            .padding(top = 48.dp, start = 24.dp, end = 16.dp)
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
                    .width(2.dp)
                    .height(36.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "SYSTEM.STATUS: ONLINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Text(
                    text = "HELLO, $displayUser",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp,
                        fontSize = 28.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Profile Icon - Optimized for space
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val accentColor = when (challenge) {
        Challenge.SPEED_RUN -> Color(0xFF22D3EE)
        Challenge.MIRAGE -> Color(0xFF8B5CF6)
        Challenge.MINEFIELD -> Color(0xFFF43F5E)
        Challenge.BLACKOUT -> Color(0xFF94A3B8)
        Challenge.FRENZY -> Color(0xFFFACC15)
    }

    val shape = remember { ChamferedCornerShape(24.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isFeatured) 160.dp else 130.dp)
            .scale(scale)
            .graphicsLayer {
                clip = true
                this.shape = shape
            }
            .background(Color(0xFF0F172A).copy(alpha = 0.8f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Liquid Border Canvas - Reactive or Featured
        if (isFeatured || isPressed) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chamferPx = 24.dp.toPx()
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width - chamferPx, 0f)
                    lineTo(size.width, chamferPx)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.2f),
                    style = Stroke(width = 2.dp.toPx())
                )
                
                val angleRad = Math.toRadians(borderRotation.toDouble())
                val centerX = size.width / 2
                val centerY = size.height / 2
                val spotX = centerX + (size.width / 2) * Math.cos(angleRad).toFloat()
                val spotY = centerY + (size.height / 2) * Math.sin(angleRad).toFloat()
                
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to accentColor.copy(alpha = 0.3f),
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
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = when (challenge) {
                    Challenge.SPEED_RUN -> Icons.Default.ChangeHistory
                    Challenge.MIRAGE -> Icons.Default.Hexagon
                    Challenge.MINEFIELD -> Icons.Default.Square
                    Challenge.BLACKOUT -> Icons.Default.Circle
                    Challenge.FRENZY -> Icons.Default.Star
                },
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier
                    .size(if (isFeatured) 32.dp else 24.dp)
                    .alpha(0.8f)
            )

            Column {
                Text(
                    text = challenge.title,
                    style = (if (isFeatured) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium).copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color.White
                )
                if (isFeatured) {
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PrismNavBar(
    onLeaderboardClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .height(72.dp),
        shape = RoundedCornerShape(36.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrismNavButton(icon = Icons.Default.Star, label = "BADGES", onClick = onAchievementsClick)
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            PrismNavButton(icon = Icons.Default.Circle, label = "SCORES", onClick = onLeaderboardClick)
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            PrismNavButton(icon = Icons.Default.ChangeHistory, label = "CONFIG", onClick = onSettingsClick)
        }
    }
}

@Composable
fun PrismNavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.8f else 1f, label = "scale")

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
            modifier = Modifier.size(20.dp),
            tint = Color.White.copy(alpha = if (isPressed) 1f else 0.4f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            ),
            color = Color.White.copy(alpha = if (isPressed) 1f else 0.4f)
        )
    }
}

class ChamferedCornerShape(private val chamferSize: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val chamferPx = with(density) { chamferSize.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width - chamferPx, 0f)
            lineTo(size.width, chamferPx)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
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
