package com.sujoy.flippy.common

data class LeaderboardModel(
    val playerId: String = "",
    val score: Int = 0,
    val username: String = "",
    val lastUpdated: Long = 0
)