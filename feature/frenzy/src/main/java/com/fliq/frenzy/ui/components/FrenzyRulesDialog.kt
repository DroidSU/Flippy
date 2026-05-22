package com.fliq.frenzy.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.core.theme.BgDeepDark
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.Gold
import com.fliq.core.theme.NeonCyan
import com.fliq.core.util.ChamferedCornerShape
import com.fliq.game_engine.R

@Composable
fun FrenzyRulesDialog(
    onDismiss: (showOnStartup: Boolean) -> Unit
) {
    var showOnStartup by remember { mutableStateOf(false) }
    val accentColor = Gold // Gold/Yellow for Frenzy

    Dialog(
        onDismissRequest = { onDismiss(showOnStartup) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight()) {
            // Shadow Layer
            Surface(
                modifier = Modifier.fillMaxWidth().height(380.dp).graphicsLayer { 
                    translationY = 8.dp.toPx()
                    alpha = 0.4f
                },
                shape = ChamferedCornerShape(32.dp),
                color = Color.Black
            ) {}

            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = ChamferedCornerShape(32.dp),
                color = BgDeepDark.copy(alpha = 0.98f),
                border = BorderStroke(1.dp, Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp).graphicsLayer { shadowElevation = 16f },
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "FRENZY",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FrenzyRuleItem(
                            iconRes = R.drawable.ic_coin,
                            title = "PURE GOLD",
                            description = "No bombs here. Only coins. Tap them all as fast as you can!",
                            accent = accentColor
                        )
                        FrenzyRuleItem(
                            iconVector = Icons.Default.Warning,
                            title = "ZERO TOLERANCE",
                            description = "Missing even a single coin ends your run immediately.",
                            accent = BombRed,
                            isVector = true
                        )
                        FrenzyRuleItem(
                            iconVector = Icons.Default.PlayArrow,
                            title = "BLITZ SPEED",
                            description = "Visible duration is locked at a brutal 350ms. No warming up.",
                            accent = NeonCyan,
                            isVector = true
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val btnScale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "s")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .scale(btnScale)
                            .graphicsLayer { shadowElevation = 12f }
                            .clip(ChamferedCornerShape(16.dp))
                            .background(accentColor)
                            .clickable(interactionSource = interactionSource, indication = null) { onDismiss(showOnStartup) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START BLITZ",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = BgDeepDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FrenzyRuleItem(
    iconRes: Int = 0,
    iconVector: ImageVector? = null,
    title: String,
    description: String,
    accent: Color,
    isVector: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(44.dp).graphicsLayer { shadowElevation = 8f },
            shape = ChamferedCornerShape(8.dp),
            color = accent.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isVector && iconVector != null) {
                    Icon(iconVector, null, tint = accent, modifier = Modifier.size(20.dp))
                } else {
                    Image(painterResource(id = iconRes), null, modifier = Modifier.size(24.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = accent
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 16.sp
            )
        }
    }
}
