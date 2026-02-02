package com.sujoy.flippy.profile.repository

import android.content.Context
import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val context: Context,
    private val matchDao: MatchDAO
) : ProfileRepository {
    private val prefs = context.getSharedPreferences("flippy_profile", Context.MODE_PRIVATE)

    override fun getUsername(): String {
        return prefs.getString("username", null) ?: ""
    }

    override fun getAvatarId(): Int {
        // Return 0 as default if no avatar selected
        return prefs.getInt("avatar_id", 0)
    }

    override fun saveProfile(username: String, avatarId: Int) {
        prefs.edit()
            .putString("username", username)
            .putInt("avatar_id", avatarId)
            .apply()
    }

    override fun getTopScores(playerId: String): Flow<List<MatchHistory>> {
        return matchDao.getTopThreeScores(playerId)
    }

    override suspend fun getMatchHistory(playerId: String): List<MatchHistory> {
        return matchDao.getMatchHistoryForId(playerId)
    }
}
