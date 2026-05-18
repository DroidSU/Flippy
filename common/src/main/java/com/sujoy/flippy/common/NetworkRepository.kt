package com.sujoy.flippy.common

import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.BadgeEntity
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
    suspend fun storeMatchData(matchList: List<MatchHistory>)
    suspend fun storeBadgeData(badgeList: List<BadgeEntity>)
    fun getLeaderBoard(difficulty: String) : Flow<List<LeaderboardModel>>
    fun fetchUserData(userId: String): Flow<Result<UserData?>>
    suspend fun updateUserName(username: String, avatarId: Int, oldUsername: String? = null): Result<Unit>
    suspend fun uploadUserData(userData: UserData): Result<Unit>
    fun fetchMatchHistory(userId: String): Flow<Result<List<MatchHistory>>>
    fun fetchBadges(userId: String): Flow<Result<List<BadgeEntity>>>
    suspend fun isUsernameExisting(username: String) : Boolean
}
