package com.fliq.mirage.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods

@Composable
fun MirageGameOverDialog(
    visible: Boolean,
    score: Int,
    gameTime: Long,
    accuracy: Float,
    newBadges: List<Badge> = emptyList(),
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (visible) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .widthIn(max = if (isLandscape) 560.dp else 400.dp)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.15f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(if (isLandscape) 20.dp else 24.dp)
                    ) {
                        Text(
                            text = "GAME OVER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

                        if (isLandscape) {
                            LandscapeContent(
                                score = score,
                                gameTime = gameTime,
                                accuracy = accuracy,
                                newBadges = newBadges,
                                accentColor = accentColor,
                                onRetry = onRetry,
                                onBackToDashboard = onBackToDashboard
                            )
                        } else {
                            PortraitContent(
                                score = score,
                                gameTime = gameTime,
                                accuracy = accuracy,
                                newBadges = newBadges,
                                accentColor = accentColor,
                                onRetry = onRetry,
                                onBackToDashboard = onBackToDashboard
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortraitContent(
    score: Int,
    gameTime: Long,
    accuracy: Float,
    newBadges: List<Badge>,
    accentColor: Color,
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScoreBlock(score = score, accentColor = accentColor)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PerformanceStatItem(
                modifier = Modifier.weight(1f),
                label = "TIME",
                value = UtilityMethods.formatTime(gameTime),
                icon = Icons.Default.Timer,
                color = accentColor
            )
            PerformanceStatItem(
                modifier = Modifier.weight(1f),
                label = "ACCURACY",
                value = "${(accuracy * 100).toInt()}%",
                icon = Icons.Default.TouchApp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        if (newBadges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            BadgeUnlockSection(newBadges)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ActionsSection(onRetry, onBackToDashboard, accentColor)
    }
}

@Composable
private fun LandscapeContent(
    score: Int,
    gameTime: Long,
    accuracy: Float,
    newBadges: List<Badge>,
    accentColor: Color,
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ScoreBlock(score = score, accentColor = accentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PerformanceStatItem(
                    modifier = Modifier.weight(1f),
                    label = "TIME",
                    value = UtilityMethods.formatTime(gameTime),
                    icon = Icons.Default.Timer,
                    color = accentColor
                )
                PerformanceStatItem(
                    modifier = Modifier.weight(1f),
                    label = "ACCURACY",
                    value = "${(accuracy * 100).toInt()}%",
                    icon = Icons.Default.TouchApp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            if (newBadges.isNotEmpty()) {
                BadgeUnlockSection(newBadges)
                Spacer(modifier = Modifier.height(12.dp))
            }
            ActionsSection(onRetry, onBackToDashboard, accentColor)
        }
    }
}

@Composable
private fun ScoreBlock(score: Int, accentColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "SCORE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = accentColor.copy(alpha = 0.6f)
            )
            Text(
                text = score.toString().padStart(3, '0'),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActionsSection(
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionRequestButton(
            text = "PLAY AGAIN",
            icon = Icons.Default.Refresh,
            primary = true,
            accentColor = accentColor,
            onClick = onRetry
        )
        ActionRequestButton(
            text = "MAIN MENU",
            icon = Icons.Default.Home,
            primary = false,
            accentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            onClick = onBackToDashboard
        )
    }
}

@Composable
private fun PerformanceStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = color.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 7.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun ActionRequestButton(
    text: String,
    icon: ImageVector,
    primary: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "s")

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .scale(scale),
        shape = RoundedCornerShape(14.dp),
        color = if (primary) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
        border = if (primary) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = if (primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BadgeUnlockSection(badges: List<Badge>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "NEW TROPHIES!",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(badge)
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(badge.icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
