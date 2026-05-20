package com.fliq.game_engine.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.gameColors
import com.fliq.game_engine.R

@Composable
fun FlippyRulesDialog(
    onDismiss: (showOnStartup: Boolean) -> Unit
) {
    var showOnStartup by remember { mutableStateOf(false) }
    val gameColors = MaterialTheme.gameColors

    Dialog(
        onDismissRequest = { onDismiss(showOnStartup) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(40.dp),
            color = gameColors.backgroundGradient.first().copy(alpha = 0.95f),
            shadowElevation = 32.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "HOW TO PLAY",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Rule Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RuleItemRow(
                        iconRes = R.drawable.ic_coin,
                        title = "Speed Matters",
                        description = "Tap coins rapidly to build your score and streak.",
                        tintColor = Gold
                    )

                    RuleItemRow(
                        iconRes = R.drawable.ic_bomb,
                        title = "Danger Zones",
                        description = "Avoid tapping bombs. Each mistake costs you a heart.",
                        tintColor = Color(0xFFF43F5E) // Red
                    )

                    RuleItemRow(
                        iconVector = Icons.Default.Favorite,
                        title = "Vital Focus",
                        description = "Missing 3 coins (2 in Hard mode) will cost a heart.",
                        tintColor = MaterialTheme.colorScheme.primary,
                        isVector = true
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Persistence Option
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    onClick = { showOnStartup = !showOnStartup }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showOnStartup,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Show on startup",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onDismiss(showOnStartup) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GOT IT!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleItemRow(
    iconRes: Int = 0,
    iconVector: ImageVector? = null,
    title: String,
    description: String,
    tintColor: Color,
    isVector: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(18.dp),
            color = tintColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, tintColor.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isVector && iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = tintColor,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun FliqRulesComponent(
    onRulesDismissed: (Boolean) -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(true) }
    
    if (isVisible) {
        FlippyRulesDialog(
            onDismiss = { showOnStartup ->
                isVisible = false
                onRulesDismissed(showOnStartup)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlippyRulesComponentPreview() {
    FliqTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            FlippyRulesDialog(onDismiss = {})
        }
    }
}
