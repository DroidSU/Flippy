package com.sujoy.flippy.profile.repository

import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUsername(): String
    fun getAvatarId(): Int
    fun saveProfile(username: String, avatarId: Int)
    fun getMatchHistory() : Flow<List<MatchHistory>>
}
