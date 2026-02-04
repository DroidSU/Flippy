package com.sujoy.flippy.common

import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
    fun storeMatchData(matchList: List<MatchHistory>)
    fun storeUserData(userName: String, avatarResourceId: Int)
    fun getLeaderBoard() : Flow<List<LeaderboardModel>>
}
