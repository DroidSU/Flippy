package com.fliq.game_engine.models.v2

sealed interface StageConfig {
    data class Reflex(
        val spawnInterval: Long,      // ms between spawns
        val targetDuration: Long,     // ms coin stays visible
        val bombFrequency: Float,     // 0.0 to 1.0
        val targetCount: Int,         // Coins to hit to clear stage
        val multiTapChance: Float = 0f,
        val feverThreshold: Int = 20
    ) : StageConfig

    data class Memory(
        val sequenceCount: Int,
        val patternSpeed: Long,       // ms between each flash in preview
        val visibleDuration: Long,    // ms each flash stays visible
        val decoyCount: Int = 0,
        val strictOrder: Boolean = true
    ) : StageConfig

    data class Trace(
        val pathLength: Int,
        val movementSpeed: Float = 0f, // 0 means static path
        val accuracyThreshold: Float = 0.8f
    ) : StageConfig

    data class Hybrid(
        val memoryConfig: Memory,
        val reflexConfig: Reflex
    ) : StageConfig
}
