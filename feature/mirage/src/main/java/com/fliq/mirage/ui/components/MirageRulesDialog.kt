package com.fliq.mirage.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.game_engine.R

@Composable
fun MirageRulesDialog(
    onDismiss: (showOnStartup: Boolean) -> Unit
) {
    var showOnStartup by remember { mutableStateOf(false) }
    val accentColor = MaterialTheme.colorScheme.primary
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Dialog(
        onDismissRequest = { onDismiss(showOnStartup) },
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
                    .widthIn(max = if (isLandscape) 600.dp else 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)))
            ) {
                Column(
                    modifier = Modifier.padding(if (isLandscape) 20.dp else 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderSection(accentColor, isLandscape)

                    Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))

                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                RulesList(accentColor, isSmall = true)
                            }
                            Column(modifier = Modifier.weight(0.8f)) {
                                PlayButton(accentColor) { onDismiss(showOnStartup) }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            RulesList(accentColor)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        PlayButton(accentColor) { onDismiss(showOnStartup) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(accentColor: Color, isLandscape: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isLandscape) 40.dp else 48.dp),
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(if (isLandscape) 24.dp else 28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "THE MIRAGE",
            style = (if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall).copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RulesList(accentColor: Color, isSmall: Boolean = false) {
    MirageRuleItem(
        iconRes = R.drawable.ic_coin,
        title = "INITIAL COIN",
        description = "Every tile starts as a coin. But don't be too eager!",
        accent = accentColor,
        isSmall = isSmall
    )
    MirageRuleItem(
        iconRes = R.drawable.ic_bomb,
        title = "TRANSFORMATION",
        description = "Some coins transform into bombs before disappearing. Watch closely!",
        accent = MaterialTheme.colorScheme.secondary,
        isSmall = isSmall
    )
    MirageRuleItem(
        iconVector = Icons.Default.Favorite,
        title = "SURVIVAL",
        description = "Missing a coin or tapping a bomb costs a heart.",
        accent = MaterialTheme.colorScheme.error,
        isVector = true,
        isSmall = isSmall
    )
}

@Composable
private fun PlayButton(accentColor: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val btnScale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "s")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(btnScale)
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PLAY NOW",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun MirageRuleItem(
    iconRes: Int = 0,
    iconVector: ImageVector? = null,
    title: String,
    description: String,
    accent: Color,
    isVector: Boolean = false,
    isSmall: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(if (isSmall) 32.dp else 40.dp),
            shape = RoundedCornerShape(10.dp),
            color = accent.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isVector && iconVector != null) {
                    Icon(iconVector, null, tint = accent, modifier = Modifier.size(if (isSmall) 16.dp else 20.dp))
                } else {
                    Image(painterResource(id = iconRes), null, modifier = Modifier.size(if (isSmall) 18.dp else 22.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(if (isSmall) 10.dp else 14.dp))
        Column {
            Text(
                text = title,
                style = (if (isSmall) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge).copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = accent
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = if (isSmall) 9.sp else 11.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = if (isSmall) 12.sp else 14.sp
            )
        }
    }
}
