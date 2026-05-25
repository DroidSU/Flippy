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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            .padding(horizontal = 24.dp, vertical = 20.dp),
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
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
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = if (isPaused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        }

        SpeedRunIconButton(icon = Icons.AutoMirrored.Filled.HelpOutline, onClick = onHelpClick)
    }
}
