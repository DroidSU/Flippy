package com.fliq.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isHidden: Boolean = false,
) {
    // Speedster
    THE_FLASH(
        "speed_flash",
        "The Flash",
        "Tap a tile in under 250ms",
        Icons.Default.Bolt
    ),
    LIGHTNING_STRIKES(
        "speed_lightning",
        "Lightning Strikes",
        "Average reaction time under 350ms in a match",
        Icons.Default.Bolt
    ),

    // Survivor
    STAYIN_ALIVE(
        "survivor_1min",
        "Stayin' Alive",
        "Survive for 1 minute",
        Icons.Default.Timer
    ),
    MARATHONER(
        "survivor_5min",
        "Marathoner",
        "Survive for 5 minutes",
        Icons.Default.Schedule
    ),
    AGAINST_ALL_ODDS(
        "survivor_clutch",
        "Against All Odds",
        "Survive for 30s with only 1 life left",
        Icons.Default.Favorite
    ),

    // Perfectionist
    CLEAN_SHEET(
        "perf_no_bomb",
        "Clean Sheet",
        "Score 50 without hitting a bomb",
        Icons.Default.Stars
    ),
    SNIPER(
        "perf_accuracy",
        "Sniper",
        "100% accuracy with at least 50 taps",
        Icons.Default.MilitaryTech
    ),

    // Streak
    ON_FIRE(
        "streak_20",
        "On Fire",
        "Reach a 20-tile streak",
        Icons.Default.LocalFireDepartment
    ),
    UNSTOPPABLE(
        "streak_50",
        "Unstoppable",
        "Reach a 50-tile streak",
        Icons.Default.EmojiEvents
    ),

    // Veteran
    FIRST_STEPS(
        "vet_first",
        "First Steps",
        "Play your first match",
        Icons.Default.EmojiEvents
    ),
    FLIQ_ADDICT(
        "vet_100",
        "Fliq Addict",
        "Play 100 total matches",
        Icons.Default.EmojiEvents
    );

    companion object {
        fun fromId(id: String): Badge? = entries.find { it.id == id }
    }
}
