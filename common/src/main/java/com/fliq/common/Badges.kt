package com.fliq.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector

enum class BadgeCategory(val title: String) {
    GENERAL("GENERAL"),
    SPEED_RUN("SPEED RUN"),
    MIRAGE("THE MIRAGE"),
    MINEFIELD("MINEFIELD"),
    FRENZY("FRENZY"),
    BLACKOUT("BLACKOUT")
}

enum class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: BadgeCategory = BadgeCategory.GENERAL,
    val isHidden: Boolean = false,
) {
    // General / Legacy
    THE_FLASH(
        "speed_flash",
        "The Flash",
        "Tap a tile in under 250ms",
        Icons.Default.Bolt,
        BadgeCategory.GENERAL
    ),
    LIGHTNING_STRIKES(
        "speed_lightning",
        "Lightning Strikes",
        "Average reaction time under 350ms in a match",
        Icons.Default.Bolt,
        BadgeCategory.GENERAL
    ),

    // Survivor
    STAYIN_ALIVE(
        "survivor_1min",
        "Stayin' Alive",
        "Survive for 1 minute",
        Icons.Default.Timer,
        BadgeCategory.GENERAL
    ),
    MARATHONER(
        "survivor_5min",
        "Marathoner",
        "Survive for 5 minutes",
        Icons.Default.Schedule,
        BadgeCategory.GENERAL
    ),
    AGAINST_ALL_ODDS(
        "survivor_clutch",
        "Against All Odds",
        "Survive for 30s with only 1 life left",
        Icons.Default.Favorite,
        BadgeCategory.GENERAL
    ),

    // Perfectionist
    CLEAN_SHEET(
        "perf_no_bomb",
        "Clean Sheet",
        "Score 50 without hitting a bomb",
        Icons.Default.Stars,
        BadgeCategory.GENERAL
    ),
    SNIPER(
        "perf_accuracy",
        "Sniper",
        "100% accuracy with at least 50 taps",
        Icons.Default.MilitaryTech,
        BadgeCategory.GENERAL
    ),

    // Streak
    ON_FIRE(
        "streak_20",
        "On Fire",
        "Reach a 20-tile streak",
        Icons.Default.LocalFireDepartment,
        BadgeCategory.GENERAL
    ),
    UNSTOPPABLE(
        "streak_50",
        "Unstoppable",
        "Reach a 50-tile streak",
        Icons.Default.EmojiEvents,
        BadgeCategory.GENERAL
    ),

    // Veteran
    FIRST_STEPS(
        "vet_first",
        "First Steps",
        "Play your first match",
        Icons.Default.EmojiEvents,
        BadgeCategory.GENERAL
    ),
    FLIQ_ADDICT(
        "vet_100",
        "Fliq Addict",
        "Play 100 total matches",
        Icons.Default.EmojiEvents,
        BadgeCategory.GENERAL
    ),

    // Challenge Specific
    SPEED_DEMON(
        "speed_run_tier_5",
        "Speed Demon",
        "Reach visibility tier 5 in Speed Run",
        Icons.Default.Bolt,
        BadgeCategory.SPEED_RUN
    ),
    EAGLE_EYE(
        "mirage_no_bomb",
        "Eagle Eye",
        "Score 30 in Mirage without hitting a transformed bomb",
        Icons.Default.Visibility,
        BadgeCategory.MIRAGE
    ),
    UNTOUCHABLE(
        "minefield_50",
        "Untouchable",
        "Score 50 in Minefield",
        Icons.Default.Security,
        BadgeCategory.MINEFIELD
    ),
    GOLD_DIGGER(
        "frenzy_100",
        "Gold Digger",
        "Score 100 in Frenzy",
        Icons.Default.Savings,
        BadgeCategory.FRENZY
    );

    companion object {
        fun fromId(id: String): Badge? = entries.find { it.id == id }
    }
}
