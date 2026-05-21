package com.fliq.leaderboard.repository

import com.fliq.common.LeaderboardModel
import com.fliq.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderBoard(challengeName: String) : Flow<List<LeaderboardModel>>
    fun getMyScores() : Flow<List<MatchHistory>>
}
