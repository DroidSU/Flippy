package com.fliq.game_engine.models.v2

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.ui.graphics.vector.ImageVector

enum class Boost(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val cost: Int
) {
    SHIELD(
        title = "Shield",
        description = "Absorbs 1 bomb hit or miss.",
        icon = Icons.Default.AddModerator,
        cost = 100
    ),
    SLOW_MO(
        title = "Slow-Mo",
        description = "Coins stay 20% longer.",
        icon = Icons.Default.HourglassBottom,
        cost = 150
    ),
    DOUBLE_XP(
        title = "Double XP",
        description = "Earn 2x XP for this stage.",
        icon = Icons.Default.KeyboardDoubleArrowUp,
        cost = 200
    )
}
