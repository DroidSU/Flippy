package com.fliq.game_engine.v2

import com.fliq.game_engine.models.v2.GameClass
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.models.v2.GoalType
import com.fliq.game_engine.models.v2.StageConfig
import com.fliq.game_engine.models.v2.StageGoal
import com.fliq.game_engine.models.v2.StageRewards

object StageManager {
    
    val world1 = listOf(
        GameStage(
            id = "w1_s1", world = 1, stageNumber = 1, title = "First Pulse",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(1000L, 1200L, 0f, 15),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.ACCURACY, 0.9f), StageGoal(3, GoalType.TIME, 15f)),
            rewards = StageRewards(100, 50)
        ),
        GameStage(
            id = "w1_s2", world = 1, stageNumber = 2, title = "Neon Lite",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(800L, 1000L, 0f, 20),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.COMBO, 10f), StageGoal(3, GoalType.COMBO, 15f)),
            rewards = StageRewards(120, 60)
        ),
        GameStage(
            id = "w1_s3", world = 1, stageNumber = 3, title = "Echo One",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(3, 500L, 600L),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.ACCURACY, 1f), StageGoal(3, GoalType.TIME, 5f)),
            rewards = StageRewards(150, 75)
        ),
        GameStage(
            id = "w1_s4", world = 1, stageNumber = 4, title = "The Slide",
            classType = GameClass.TRACE,
            config = StageConfig.Trace(5),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.TIME, 3f)),
            rewards = StageRewards(130, 65)
        ),
        GameStage(
            id = "w1_s5", world = 1, stageNumber = 5, title = "Mini-Boss: Spark",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(600L, 800L, 0.05f, 40),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.COMBO, 20f), StageGoal(3, GoalType.COMBO, 25f)),
            rewards = StageRewards(300, 200)
        ),
        GameStage(
            id = "w1_s6", world = 1, stageNumber = 6, title = "Static Recall",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(4, 450L, 500L),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.TIME, 5f)),
            rewards = StageRewards(160, 80)
        ),
        GameStage(
            id = "w1_s7", world = 1, stageNumber = 7, title = "Glitch Run",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(700L, 900L, 0.15f, 30),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.ACCURACY, 1f), StageGoal(3, GoalType.TIME, 20f)),
            rewards = StageRewards(180, 90)
        ),
        GameStage(
            id = "w1_s8", world = 1, stageNumber = 8, title = "Zig-Zag",
            classType = GameClass.TRACE,
            config = StageConfig.Trace(10),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(200, 100)
        ),
        GameStage(
            id = "w1_s9", world = 1, stageNumber = 9, title = "Dark Mirror",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(5, 400L, 450L),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(220, 110)
        ),
        GameStage(
            id = "w1_s10", world = 1, stageNumber = 10, title = "Shadow Gate",
            classType = GameClass.HYBRID,
            config = StageConfig.Hybrid(
                StageConfig.Memory(4, 500L, 600L),
                StageConfig.Reflex(800L, 1000L, 0.1f, 20)
            ),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.COMBO, 25f), StageGoal(3, GoalType.COMBO, 30f)),
            rewards = StageRewards(500, 250)
        ),
        GameStage(
            id = "w1_s11", world = 1, stageNumber = 11, title = "Rapid Fire",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(500L, 600L, 0.1f, 30),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.COMBO, 20f)),
            rewards = StageRewards(240, 120)
        ),
        GameStage(
            id = "w1_s12", world = 1, stageNumber = 12, title = "Shrink Wrap",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(600L, 800L, 0.15f, 40),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(260, 130)
        ),
        GameStage(
            id = "w1_s13", world = 1, stageNumber = 13, title = "Neon Maze",
            classType = GameClass.TRACE,
            config = StageConfig.Trace(15),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.TIME, 8f)),
            rewards = StageRewards(280, 140)
        ),
        GameStage(
            id = "w1_s14", world = 1, stageNumber = 14, title = "Ghost Recall",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(6, 350L, 400L, decoyCount = 1),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(300, 150)
        ),
        GameStage(
            id = "w1_s15", world = 1, stageNumber = 15, title = "Fever Dream",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(450L, 650L, 0.2f, 60, feverThreshold = 15),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.COMBO, 40f)),
            rewards = StageRewards(400, 200)
        ),
        GameStage(
            id = "w1_s16", world = 1, stageNumber = 16, title = "Double Tap",
            classType = GameClass.REFLEX,
            config = StageConfig.Reflex(700L, 900L, 0.1f, 30, multiTapChance = 0.4f),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(320, 160)
        ),
        GameStage(
            id = "w1_s17", world = 1, stageNumber = 17, title = "Phantom Grid",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(5, 100L, 500L), // Fast simultaneous flash
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.TIME, 3f)),
            rewards = StageRewards(340, 170)
        ),
        GameStage(
            id = "w1_s18", world = 1, stageNumber = 18, title = "Flow Motion",
            classType = GameClass.TRACE,
            config = StageConfig.Trace(12, movementSpeed = 1f),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(360, 180)
        ),
        GameStage(
            id = "w1_s19", world = 1, stageNumber = 19, title = "Neural Static",
            classType = GameClass.MEMORY,
            config = StageConfig.Memory(7, 300L, 350L),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(3, GoalType.ACCURACY, 1f)),
            rewards = StageRewards(380, 190)
        ),
        GameStage(
            id = "w1_s20", world = 1, stageNumber = 20, title = "World Boss: Obsidian",
            classType = GameClass.HYBRID,
            config = StageConfig.Hybrid(
                StageConfig.Memory(5, 400L, 500L),
                StageConfig.Reflex(600L, 800L, 0.2f, 40)
            ),
            goals = listOf(StageGoal(1, GoalType.CLEAR, 1f), StageGoal(2, GoalType.COMBO, 40f), StageGoal(3, GoalType.COMBO, 50f)),
            rewards = StageRewards(1000, 500)
        )
    )

    fun getStage(id: String): GameStage? {
        return world1.find { it.id == id }
    }
}
