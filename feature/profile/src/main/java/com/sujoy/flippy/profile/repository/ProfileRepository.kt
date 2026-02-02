package com.sujoy.flippy.profile.repository

import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUsername(): String
    fun getAvatarId(): Int
    fun saveProfile(username: String, avatarId: Int)
    fun getTopScores(playerId: String): Flow<List<MatchHistory>>
    suspend fun getMatchHistory(playerId: String): List<MatchHistory>
}
