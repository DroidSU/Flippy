package com.sujoy.flippy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sujoy.flippy.common.ConstantsManager
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDAO {
    @Insert
    suspend fun insertMatch(match: MatchHistory)
    
    @Query("SELECT * FROM `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` WHERE playerId = :playerId ORDER BY score DESC, gameDuration ASC LIMIT 5")
    fun getTopScores(playerId: String): Flow<List<MatchHistory>>

    @Query("SELECT * FROM `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` WHERE playerId = :playerId ORDER BY timestamp DESC")
    suspend fun getMatchHistoryForId(playerId: String): List<MatchHistory>
}
