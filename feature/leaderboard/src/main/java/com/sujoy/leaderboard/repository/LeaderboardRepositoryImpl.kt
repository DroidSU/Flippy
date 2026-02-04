package com.sujoy.leaderboard.repository

import com.sujoy.flippy.common.LeaderboardModel
import com.sujoy.flippy.common.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val networkRepository: NetworkRepository
) : LeaderboardRepository {

    override fun getLeaderBoard(): Flow<List<LeaderboardModel>> {
        return networkRepository.getLeaderBoard()
    }
}
