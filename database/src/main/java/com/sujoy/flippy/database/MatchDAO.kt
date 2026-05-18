package com.sujoy.flippy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchHistory>)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getMatchHistory(): Flow<List<MatchHistory>>

    @Query("SELECT * FROM match_history WHERE playerId = :playerId ORDER BY timestamp DESC")
    suspend fun getMatchHistorySync(playerId: String): List<MatchHistory>

    @Query("UPDATE match_history SET isBackedUp = 1 WHERE id IN (:matchIds)")
    suspend fun markMatchesAsBackedUp(matchIds: List<String>)

    @Query("SELECT * FROM match_history WHERE playerId = :playerId ORDER BY score DESC LIMIT 3")
    fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>>

    @Query("SELECT * FROM match_history WHERE isBackedUp = 0")
    suspend fun getPendingMatches(): List<MatchHistory>

    @Query("DELETE FROM match_history")
    suspend fun clearAll()
}
