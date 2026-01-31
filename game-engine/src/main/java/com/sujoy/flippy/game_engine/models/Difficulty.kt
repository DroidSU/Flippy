package com.sujoy.flippy.game_engine.models

enum class Difficulty(val label: String, val minInterval: Long, val maxInterval: Long) {
    EASY("EASY", 600L, 1200L),
    NORMAL("NORMAL", 400L, 800L),
    HARD("HARD", 200L, 400L)
}
