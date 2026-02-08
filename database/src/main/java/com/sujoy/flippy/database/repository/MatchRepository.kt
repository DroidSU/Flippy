package com.sujoy.flippy.database.repository

import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun saveMatch(match: MatchHistory)
    suspend fun saveMatches(matchList: List<MatchHistory>)
    fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>>
}
