package com.sujoy.flippy.game_engine.repository

import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun saveMatch(match: MatchHistory)
    fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>>
}
