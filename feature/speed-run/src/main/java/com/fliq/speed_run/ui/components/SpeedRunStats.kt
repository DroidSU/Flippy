package com.fliq.speed_run.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqStatChip

@Composable
fun SpeedRunStats(
    score: Int,
    lives: Int,
    gameTime: Long,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .padding(horizontal = FliqTheme.spacing.screenPadding)
            .onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
    ) {
        FliqCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            elevation = FliqTheme.elevation.medium,
            contentPadding = FliqTheme.spacing.cardPadding
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FliqStatChip(
                    label = "SCORE",
                    value = score.toString().padStart(3, '0')
                )
                
                FliqStatChip(
                    label = "TIME",
                    value = UtilityMethods.formatTime(gameTime)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LIVES",
                        style = FliqTheme.typography.label,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Row(modifier = Modifier.padding(top = FliqTheme.spacing.extraSmall)) {
                        repeat(3) { index ->
                            SpeedRunBeatingHeartIcon(
                                isAlive = index < lives,
                                modifier = Modifier.padding(horizontal = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
