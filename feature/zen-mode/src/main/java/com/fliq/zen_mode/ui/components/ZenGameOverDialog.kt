package com.fliq.zen_mode.ui.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.Gold
import com.fliq.core.util.ChamferedCornerShape

@Composable
fun ZenGameOverDialog(
    visible: Boolean,
    score: Int,
    gameTime: Long,
    accuracy: Float,
    newBadges: List<Badge> = emptyList(),
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    val accentColor = Color(0xFF2DD4BF) // Teal for Zen

    if (visible) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                // 3D Depth Container
                Box(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight()) {
                    // Physical Shadow Layer
                    Surface(
                        modifier = Modifier.matchParentSize().offset(y = 8.dp).alpha(0.4f),
                        shape = ChamferedCornerShape(40.dp),
                        color = Color.Black
                    ) {}

                    Surface(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        shape = ChamferedCornerShape(40.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(
                            1.dp, 
                            Brush.linearGradient(listOf(accentColor.copy(alpha = 0.2f), Color.Transparent, accentColor.copy(alpha = 0.05f)))
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "SESSION ENDED",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Elevated Score Card
                            Surface(
                                modifier = Modifier.fillMaxWidth().graphicsLayer { shadowElevation = 12f },
                                shape = ChamferedCornerShape(24.dp),
                                color = Color.White.copy(alpha = 0.03f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "ZEN SCORE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = accentColor.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = score.toString().padStart(3, '0'),
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            shadow = androidx.compose.ui.graphics.Shadow(Color.Black.copy(alpha = 0.5f), offset = Offset(0f, 4f))
                                        ),
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ZenTelemetryStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "TIME",
                                    value = UtilityMethods.formatTime(gameTime),
                                    icon = Icons.Default.Timer,
                                    color = accentColor
                                )
                                ZenTelemetryStatItem(
                                    modifier = Modifier.weight(1f),
                                    label = "ACCURACY",
                                    value = "${(accuracy * 100).toInt()}%",
                                    icon = Icons.Default.TouchApp,
                                    color = Color(0xFF22D3EE)
                                )
                            }

                            if (newBadges.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                BadgeUnlockSection(newBadges)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Actions
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ZenActionRequestButton(
                                    text = "RESTART FLOW",
                                    icon = Icons.Default.Refresh,
                                    primary = true,
                                    accentColor = accentColor,
                                    onClick = onRetry
                                )
                                ZenActionRequestButton(
                                    text = "RETURN TO HUB",
                                    icon = Icons.Default.Home,
                                    primary = false,
                                    accentColor = Color.White.copy(alpha = 0.6f),
                                    onClick = onBackToDashboard
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ZenTelemetryStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = ChamferedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = color.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun ZenActionRequestButton(
    text: String,
    icon: ImageVector,
    primary: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "s")

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .scale(scale),
        shape = ChamferedCornerShape(14.dp),
        color = if (primary) accentColor else Color.White.copy(alpha = 0.05f),
        border = if (primary) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (primary) Color(0xFF0F172A) else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = if (primary) Color(0xFF0F172A) else Color.White
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
            color = Gold
        )
        
        Spacer(modifier = Modifier.height(10.dp))

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
            color = Gold.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(badge.icon, null, tint = Gold, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
