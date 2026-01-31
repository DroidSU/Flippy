package com.sujoy.flippy.game_engine.repository

import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

class MatchRepositoryImpl(private val matchDAO: MatchDAO) : MatchRepository {
    override suspend fun saveMatch(match: MatchHistory) {
        matchDAO.insertMatch(match)
    }

    override fun getLeaderboard(playerId: String): Flow<List<MatchHistory>> {
        return matchDAO.getTopScores(playerId)
    }
}
