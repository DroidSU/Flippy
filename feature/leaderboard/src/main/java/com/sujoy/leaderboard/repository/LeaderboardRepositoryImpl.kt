package com.sujoy.leaderboard.repository

import com.sujoy.flippy.common.LeaderboardModel
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.database.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val matchRepository: MatchRepository,
) : LeaderboardRepository {

    override fun getLeaderBoard(difficulty: String): Flow<List<LeaderboardModel>> {
        return networkRepository.getLeaderBoard(difficulty)
    }

    override fun getMyScores(): Flow<List<MatchHistory>> {
        return matchRepository.getMatchHistory()
    }
}
