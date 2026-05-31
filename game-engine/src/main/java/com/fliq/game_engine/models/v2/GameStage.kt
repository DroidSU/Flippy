package com.fliq.game_engine.models.v2

data class GameStage(
    val id: String,
    val world: Int,
    val stageNumber: Int,
    val title: String,
    val classType: GameClass,
    val config: StageConfig,
    val goals: List<StageGoal>,
    val rewards: StageRewards
)

data class StageGoal(
    val starIndex: Int, // 1, 2, or 3
    val type: GoalType,
    val targetValue: Float
)

enum class GoalType {
    CLEAR,      // Just finish the stage
    ACCURACY,   // Percentage (0.0 to 1.0)
    COMBO,      // Max combo reached
    TIME        // Seconds to complete (less than)
}

data class StageRewards(
    val xp: Int,
    val coins: Int
)
