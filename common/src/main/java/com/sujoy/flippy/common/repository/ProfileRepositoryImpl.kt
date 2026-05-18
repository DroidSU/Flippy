package com.sujoy.flippy.common.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.database.UserDAO
import com.sujoy.flippy.database.toUserData
import com.sujoy.flippy.database.toUserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val matchDao: MatchDAO,
    private val userDao: UserDAO
) : ProfileRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "fliq_profile"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_ID = "avatar_id"
    }

    override fun getUserData(userId: String): Flow<UserData?> {
        return userDao.getUser(userId).map { it?.toUserData() }
    }

    override suspend fun getUserDataSync(userId: String): UserData? {
        return userDao.getUserSync(userId)?.toUserData()
    }

    override suspend fun saveUserData(userData: UserData) {
        userDao.insertUser(userData.toUserEntity())
        // Keep SharedPreferences in sync for simple fields if needed
        saveProfile(userData.username, userData.avatarId)
    }

    override fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, null) ?: ""
    }

    override fun getAvatarId(): Int {
        return prefs.getInt(KEY_AVATAR_ID, 1)
    }

    private fun saveProfile(username: String, avatarId: Int) {
        prefs.edit {
            putString(KEY_USERNAME, username)
            putInt(KEY_AVATAR_ID, avatarId)
        }
    }

    override fun getMatchHistory(): Flow<List<MatchHistory>> {
        return matchDao.getMatchHistory()
    }

    override suspend fun clearLocalData() {
        // Clear SharedPreferences
        prefs.edit { clear() }
        // Clear Room database
        matchDao.clearAll()
        userDao.clearAll()
    }
}
