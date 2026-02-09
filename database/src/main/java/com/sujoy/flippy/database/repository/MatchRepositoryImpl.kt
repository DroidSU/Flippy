package com.sujoy.flippy.database.repository

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

    override suspend fun saveMatches(matchList: List<MatchHistory>) {
        matchDAO.insertMatches(matchList)
    }

    override fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>> {
        return matchDAO.getTopThreeScores(playerId)
    }

    override fun getMatchHistory(): Flow<List<MatchHistory>> {
        return matchDAO.getMatchHistory()
    }
}
