package com.fliq.common

import com.fliq.core.models.UserData
import com.fliq.database.MatchHistory

object AchievementManager {

    fun checkBadges(
        match: MatchHistory,
        allMatches: List<MatchHistory>,
        userData: UserData?,
        bestReactionTime: Long,
        clutchTime: Long
    ): List<Badge> {
        val unlockedBadges = mutableListOf<Badge>()

        // Speedster
        if (bestReactionTime < 250) unlockedBadges.add(Badge.THE_FLASH)
        
        if (match.totalReflexTime > 0 && match.correctTaps > 0) {
            val avgReaction = match.totalReflexTime / match.correctTaps
            if (avgReaction < 350) unlockedBadges.add(Badge.LIGHTNING_STRIKES)
        }

        // Survivor
        if (match.gameDuration >= 60000) unlockedBadges.add(Badge.STAYIN_ALIVE)
        if (match.gameDuration >= 300000) unlockedBadges.add(Badge.MARATHONER)
        if (clutchTime >= 30000) unlockedBadges.add(Badge.AGAINST_ALL_ODDS)

        // Perfectionist
        if (match.score >= 50 && match.totalTaps == match.correctTaps) {
            unlockedBadges.add(Badge.CLEAN_SHEET)
        }
        
        if (match.totalTaps >= 100 && match.correctTaps == match.totalTaps) {
            unlockedBadges.add(Badge.SNIPER)
        }

        // Streak
        if (match.perfectStreak >= 20) unlockedBadges.add(Badge.ON_FIRE)
        if (match.perfectStreak >= 50) unlockedBadges.add(Badge.UNSTOPPABLE)

        // Veteran
        if (allMatches.size <= 1) unlockedBadges.add(Badge.FIRST_STEPS)
        if (allMatches.size >= 100) unlockedBadges.add(Badge.FLIQ_ADDICT)

        // Challenge Specific
        if (match.challengeName == "SPEED_RUN" && match.levelReached >= 5) {
            unlockedBadges.add(Badge.SPEED_DEMON)
        }
        
        if (match.challengeName == "MIRAGE" && match.score >= 30 && match.correctTaps == match.totalTaps) {
            unlockedBadges.add(Badge.EAGLE_EYE)
        }
        
        if (match.challengeName == "MINEFIELD" && match.score >= 50) {
            unlockedBadges.add(Badge.UNTOUCHABLE)
        }
        
        if (match.challengeName == "FRENZY" && match.score >= 100) {
            unlockedBadges.add(Badge.GOLD_DIGGER)
        }

        return unlockedBadges
    }
}
