package com.fliq.common.repository

import com.fliq.core.models.UserData
import com.fliq.database.MatchHistory
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
