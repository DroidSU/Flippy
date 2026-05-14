package com.sujoy.flippy.common.repository

import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserData(userId: String): Flow<UserData?>
    suspend fun getUserDataSync(userId: String): UserData?
    suspend fun saveUserData(userData: UserData)
    fun getUsername(): String
    fun getAvatarId(): Int
    fun getMatchHistory(): Flow<List<MatchHistory>>
    suspend fun clearLocalData()
}
