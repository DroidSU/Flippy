package com.fliq.common

import com.fliq.core.models.UserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyManager @Inject constructor() {

    fun getVisibleDurationRange(gameTime: Long, progressionInterval: Long, userData: UserData?): LongRange {
        val baseReflex = userData?.baseReflex ?: 400L // Default to 400ms if not calibrated
        
        // The faster the base reflex, the lower the threshold, but never below 300ms
        // A player with 500ms reflex will have a floor of ~400ms
        val dynamicFloor = maxOf(300L, (baseReflex * 0.75f).toLong())
        
        val tiers = gameTime / progressionInterval
        val min = maxOf(dynamicFloor, 600L - tiers * 50L)
        val max = maxOf(dynamicFloor + 100L, 800L - tiers * 50L)
        
        return min..max
    }

    fun getSpawnIntervalRange(gameTime: Long, progressionInterval: Long, userData: UserData?): LongRange {
        val baseReflex = userData?.baseReflex ?: 400L
        val dynamicFloor = maxOf(300L, (baseReflex * 0.75f).toLong())
        
        val tiers = gameTime / progressionInterval
        val min = maxOf(dynamicFloor, 1200L - tiers * 45L)
        val max = maxOf(dynamicFloor + 200L, 2000L - tiers * 75L)
        
        return min..max
    }
}
