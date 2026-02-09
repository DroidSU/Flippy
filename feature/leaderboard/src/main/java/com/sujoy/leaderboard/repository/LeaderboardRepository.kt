package com.sujoy.leaderboard.repository

import com.sujoy.flippy.common.LeaderboardModel
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderBoard(difficulty: String) : Flow<List<LeaderboardModel>>
    fun getMyScores() : Flow<List<MatchHistory>>
}