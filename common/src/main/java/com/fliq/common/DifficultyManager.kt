package com.fliq.common

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DifficultyManager @Inject constructor() {

    fun getVisibleDurationRange(gameTime: Long, progressionInterval: Long): LongRange {
        val tiers = gameTime / progressionInterval
        
        // Start at 800ms, decrease per tier, ceiling (min value) at 350ms
        // Gradually decrease by ~30ms per tier
        val baseMin = maxOf(350L, 700L - tiers * 25L)
        val baseMax = maxOf(400L, 800L - tiers * 30L)
        
        // Add jitter (±25ms)
        val jitter = Random.nextLong(-25L, 26L)
        return (baseMin + jitter)..(baseMax + jitter)
    }

    fun getSpawnIntervalRange(gameTime: Long, progressionInterval: Long): LongRange {
        val tiers = gameTime / progressionInterval
        
        // Starting intervals for spawning
        // Decreasing from 1200ms-1800ms down to 450ms-700ms
        val min = maxOf(450L, 1200L - tiers * 45L)
        val max = maxOf(700L, 1800L - tiers * 65L)
        
        val jitter = Random.nextLong(-30L, 31L)
        return (min + jitter)..(max + jitter)
    }
}
