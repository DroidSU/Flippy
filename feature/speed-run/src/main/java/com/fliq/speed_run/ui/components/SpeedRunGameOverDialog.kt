package com.fliq.speed_run.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.components.FliqButton
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqStatChip
import com.fliq.core.theme.components.FliqSurface

@Composable
fun SpeedRunGameOverDialog(
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
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f))
                    .padding(horizontal = FliqTheme.spacing.screenPadding, vertical = FliqTheme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                FliqCard(
                    modifier = Modifier
                        .widthIn(max = if (isLandscape) 560.dp else 400.dp)
                        .wrapContentHeight(),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    elevation = FliqTheme.elevation.overlay,
                    contentPadding = 0.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(if (isLandscape) FliqTheme.spacing.medium else FliqTheme.spacing.large)
                    ) {
                        Text(
                            text = "GAME OVER",
                            style = FliqTheme.typography.heading,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) FliqTheme.spacing.small else FliqTheme.spacing.medium))

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

        Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.small)
        ) {
            FliqStatChip(
                modifier = Modifier.weight(1f),
                label = "TIME",
                value = UtilityMethods.formatTime(gameTime),
                accentColor = accentColor
            )
            FliqStatChip(
                modifier = Modifier.weight(1f),
                label = "ACCURACY",
                value = "${(accuracy * 100).toInt()}%",
                accentColor = MaterialTheme.colorScheme.tertiary
            )
        }

        if (newBadges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))
            BadgeUnlockSection(newBadges)
        }

        Spacer(modifier = Modifier.height(FliqTheme.spacing.large))

        ActionsSection(onRetry, onBackToDashboard)
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
        horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ScoreBlock(score = score, accentColor = accentColor)
            Spacer(modifier = Modifier.height(FliqTheme.spacing.small))
            Row(horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.small)) {
                FliqStatChip(
                    modifier = Modifier.weight(1f),
                    label = "TIME",
                    value = UtilityMethods.formatTime(gameTime),
                    accentColor = accentColor
                )
                FliqStatChip(
                    modifier = Modifier.weight(1f),
                    label = "ACCURACY",
                    value = "${(accuracy * 100).toInt()}%",
                    accentColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            if (newBadges.isNotEmpty()) {
                BadgeUnlockSection(newBadges)
                Spacer(modifier = Modifier.height(FliqTheme.spacing.small))
            }
            ActionsSection(onRetry, onBackToDashboard)
        }
    }
}

@Composable
private fun ScoreBlock(score: Int, accentColor: Color) {
    FliqSurface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        showBorder = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(FliqTheme.spacing.medium).fillMaxWidth()
        ) {
            Text(
                text = "SCORE",
                style = FliqTheme.typography.label,
                color = accentColor.copy(alpha = 0.6f)
            )
            Text(
                text = score.toString().padStart(3, '0'),
                style = FliqTheme.typography.scoreDisplay,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActionsSection(
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.small)) {
        FliqButton(
            text = "PLAY AGAIN",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
        FliqButton(
            text = "MAIN MENU",
            onClick = onBackToDashboard,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BadgeUnlockSection(badges: List<Badge>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "NEW TROPHIES!",
            style = FliqTheme.typography.label,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(FliqTheme.spacing.small))

        LazyRow(
            contentPadding = PaddingValues(horizontal = FliqTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
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
        FliqSurface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            showBorder = true
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(badge.icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(FliqTheme.spacing.extraSmall))
        Text(
            text = badge.title.uppercase(),
            style = FliqTheme.typography.label.copy(fontSize = 7.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
