package com.sujoy.leaderboard.repository

import com.sujoy.flippy.common.LeaderboardModel
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderBoard() : Flow<List<LeaderboardModel>>
}