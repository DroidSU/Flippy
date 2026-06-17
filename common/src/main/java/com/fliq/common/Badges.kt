package com.fliq.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BadgeCategory(val title: String) {
    GENERAL("GENERAL"),
    SPEED_RUN("SPEED RUN"),
    MIRAGE("THE MIRAGE"),
    MINEFIELD("MINEFIELD"),
    FRENZY("FRENZY"),
    ZEN_MODE("ZEN MODE")
}

enum class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: BadgeCategory = BadgeCategory.GENERAL,
    val isHidden: Boolean = false,
    val iconTint: Color? = null,
) {
    // General / Legacy

    FIRST_STEPS(
        id = "badge_g_first_steps",
        title = "First Steps",
        description = "Play your first match",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL,
    ),

    ROOKIE(
        id = "badge_g_rookie",
        title = "Rookie",
        description = "Play 5 matches",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    REGULAR(
        id = "badge_g_regular",
        title = "Regular",
        description = "Play 25 matches",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    VETERAN(
        id = "badge_g_veteran",
        title = "Veteran",
        description = "Play 50 matches",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    FLIQ_ADDICT(
        id = "badge_g_fliq_addict",
        title = "Fliq Addict",
        description = "Play 100 matches",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    MOMENTUM(
        id = "badge_g_momentum",
        title = "Momentum",
        description = "Open game 3 days in a row",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    LOCKED_IN(
        id = "badge_g_locked_in",
        title = "Locked In",
        description = "Open game 7 days in a row",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),

    RELENTLESS(
        id = "badge_g_relentless",
        title = "Relentless",
        description = "Open game 15 days in a row",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.GENERAL
    ),


    // Zen Mode

    TIMEKEEPER(
        id = "badge_zm_timekeeper",
        title = "Timekeeper",
        description = "Survive for 2 minutes in zen mode",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.ZEN_MODE,
        iconTint = Color.Green
    ),

    CHRONOMASTER(
        id = "badge_zm_chronomster",
        "Chronomaster",
        description = "Survive for 5 minutes in zen mode",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.ZEN_MODE,
        iconTint = Color.Green
    ),

    TIMELORD(
        id = "badge_zm_timelord",
        "Timelord",
        description = "Survive for 10 minutes in zen mode",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.ZEN_MODE,
    ),

    CENTURION(
        id = "badge_zm_centurion",
        "Centurion",
        description = "Collect 100 coins in zen mode",
        icon = Icons.Default.EmojiEvents,
        category = BadgeCategory.ZEN_MODE,
    ),


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

    IMPOSSIBLE(
        "streak_100",
        "Impossible",
        "Reach a 100-tile streak",
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
