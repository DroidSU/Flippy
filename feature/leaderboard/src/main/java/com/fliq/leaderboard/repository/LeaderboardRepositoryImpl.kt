package com.fliq.leaderboard.repository

import com.fliq.common.LeaderboardModel
import com.fliq.common.NetworkRepository
import com.fliq.database.MatchHistory
import com.fliq.database.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val matchRepository: MatchRepository,
) : LeaderboardRepository {

    override fun getLeaderBoard(challengeName: String): Flow<List<LeaderboardModel>> {
        return networkRepository.getLeaderBoard(challengeName)
    }

    override fun getMyScores(): Flow<List<MatchHistory>> {
        return matchRepository.getMatchHistory()
    }
}
