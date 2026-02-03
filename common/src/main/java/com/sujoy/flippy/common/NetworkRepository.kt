package com.sujoy.flippy.common

import com.sujoy.flippy.core.models.MatchHistory

interface NetworkRepository {
    fun isInternetAvailable(): Boolean
    fun storeMatchData(matchList: List<MatchHistory>)
    fun storeUserData(userName: String, avatarResourceId: Int)
}
