package com.sujoy.flippy.game_engine.ui

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.game_engine.R

/**
 * A modern and aesthetic dialog component that displays the rules of Flippy.
 *
 * This component adheres to the app's visual style, featuring glassmorphism-inspired 
 * surfaces and clear, icon-driven instructions.
 *
 * @param onDismiss Callback invoked when the user clicks "OKAY". 
 * It passes a boolean indicating whether the user wants to see the rules on every startup.
 */
@Composable
fun FlippyRulesDialog(
    onDismiss: (showOnStartup: Boolean) -> Unit
) {
    var showOnStartup by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onDismiss(showOnStartup) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated or Static Header
                Text(
                    text = "Rules of Flippy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Rule Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RuleItemRow(
                        iconRes = R.drawable.ic_coin,
                        title = "Tap the Coins",
                        description = "This is a game of reflex. Tap on Coin icons immediately to score points!",
                        tintColor = Color(0xFFFFD700) // Golden for Coins
                    )

                    RuleItemRow(
                        iconRes = R.drawable.ic_bomb,
                        title = "Avoid the Bombs",
                        description = "Skip the Bomb icons. Tapping a bomb will cost you 1 heart (life).",
                        tintColor = Color(0xFFFF4B4B) // Red for Bombs
                    )

                    RuleItemRow(
                        iconVector = Icons.Default.Favorite,
                        title = "Don't Miss Out",
                        description = "If you skip 2 coin taps in a row, you lose 1 heart. Stay focused!",
                        tintColor = MaterialTheme.colorScheme.primary,
                        isVector = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Persistence Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showOnStartup = !showOnStartup }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showOnStartup,
                        onCheckedChange = null, // Handled by row click
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Show on startup",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button
                Button(
                    onClick = { onDismiss(showOnStartup) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "OKAY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
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
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(tintColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isVector && iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun FlippyRulesComponent(
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
    FlippyTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            FlippyRulesDialog(onDismiss = {})
        }
    }
}
