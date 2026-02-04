package com.sujoy.flippy.game_engine.repository

import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val matchDAO: MatchDAO
) : MatchRepository {
    override suspend fun saveMatch(match: MatchHistory) {
        matchDAO.insertMatch(match)
    }

    override fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>> {
        return matchDAO.getTopThreeScores(playerId)
    }
}
