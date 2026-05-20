package com.fliq.common

enum class Difficulty(val label: String, val minInterval: Long, val maxInterval: Long) {
    EASY("EASY", 1200L, 2000L),
    NORMAL("NORMAL", 800L, 1500L),
    HARD("HARD", 400L, 800L)
}