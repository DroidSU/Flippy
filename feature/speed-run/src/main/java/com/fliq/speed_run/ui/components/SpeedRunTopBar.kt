package com.fliq.speed_run.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.fliq.core.theme.FliqTheme

@Composable
fun SpeedRunTopBar(
    isPaused: Boolean,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = FliqTheme.spacing.screenPadding,
                vertical = FliqTheme.spacing.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeedRunIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
        ) {
            Text(
                text = "SPEED RUN",
                style = FliqTheme.typography.subHeading.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f
                    )
                ),
                color = Color.White
            )
            Text(
                text = if (isPaused) "PAUSED" else "GO!",
                style = FliqTheme.typography.label,
                color = if (isPaused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        }

        SpeedRunIconButton(icon = Icons.AutoMirrored.Filled.HelpOutline, onClick = onHelpClick)
    }
}
