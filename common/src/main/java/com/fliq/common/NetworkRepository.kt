package com.fliq.common

import com.fliq.core.models.UserData
import com.fliq.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
    suspend fun storeMatchData(matchList: List<MatchHistory>)
    fun getLeaderBoard(challengeName: String) : Flow<List<LeaderboardModel>>
    fun fetchUserData(userId: String): Flow<Result<UserData?>>
    suspend fun updateUserName(username: String, avatarId: Int, oldUsername: String? = null): Result<Unit>
    suspend fun uploadUserData(userData: UserData): Result<Unit>
    suspend fun updateBaseReflex(baseReflex: Long): Result<Unit>
    fun fetchMatchHistory(userId: String): Flow<Result<List<MatchHistory>>>
    suspend fun isUsernameExisting(username: String) : Boolean
}
