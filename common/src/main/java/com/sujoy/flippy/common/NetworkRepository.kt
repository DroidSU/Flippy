package com.sujoy.flippy.common

import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
    fun storeMatchData(matchList: List<MatchHistory>)
    fun getLeaderBoard() : Flow<List<LeaderboardModel>>
    fun fetchUserData(userId: String): Flow<Result<UserData?>>
    suspend fun saveUserData(username: String, avatarId: Int): Result<Unit>
    fun fetchMatchHistory(userId: String): Flow<Result<List<MatchHistory>>>
}
