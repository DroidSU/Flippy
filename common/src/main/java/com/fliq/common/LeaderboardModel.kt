package com.fliq.common

data class LeaderboardModel(
    val playerId: String = "",
    val totalScore: Int = 0,
    val username: String = "",
    val avatarId : Int = 1,
    val lastUpdated: Long = 0,
    val difficulty: String = "NORMAL"
)