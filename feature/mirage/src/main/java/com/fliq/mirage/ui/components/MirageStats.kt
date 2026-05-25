package com.fliq.mirage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.core.util.ChamferedCornerShape

@Composable
fun MirageStats(
    score: Int,
    lives: Int,
    gameTime: Long,
    onPositioned: (Offset, Size) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .onGloballyPositioned { coords ->
                val center = Offset(
                    coords.positionInRoot().x + coords.size.width / 2,
                    coords.positionInRoot().y + coords.size.height / 2
                )
                onPositioned(center, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ChamferedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            shadowElevation = 8.dp,
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MirageStatBlock(label = "SCORE", value = score.toString().padStart(3, '0'))
                MirageStatBlock(label = "TIME", value = UtilityMethods.formatTime(gameTime))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LIVES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        repeat(3) { index ->
                            MirageBeatingHeartIcon(
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
