package com.fliq.common

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.fliq.core.ConstantsManager
import com.fliq.core.models.UserData
import com.fliq.core.models.toMap
import com.fliq.database.BadgeDAO
import com.fliq.database.BadgeEntity
import com.fliq.database.MatchDAO
import com.fliq.database.MatchHistory
import com.fliq.database.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val matchDAO: MatchDAO,
    private val badgeDAO: BadgeDAO,
    private val auth: FirebaseAuth
) : NetworkRepository {

    private val database = FirebaseDatabase.getInstance().reference

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isInternetAvailable(): Boolean {
        return UtilityMethods.isInternetAvailable(context)
    }

    override suspend fun storeMatchData(matchList: List<MatchHistory>) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext

        try {
            val rootUpdates = mutableMapOf<String, Any>()
            val matchIds = matchList.map { it.id }

            matchList.forEach { match ->
                // New format: {Challenge_Name_Table}/{userId}/{matchId}
                val path = "${match.challengeName}/$userId/${match.id}"
                rootUpdates[path] = match.toMap()
            }

            database.updateChildren(rootUpdates).await()
            matchDAO.markMatchesAsBackedUp(matchIds)
        } catch (e: Exception) {
            Log.e(ConstantsManager.APP_TAG, "Error storing match data: ${e.message}")
            e.printStackTrace()
            throw e 
        }
    }

    override suspend fun storeBadgeData(badgeList: List<BadgeEntity>) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext

        try {
            val badgeMap = mutableMapOf<String, Any>()
            val badgeIds = badgeList.map { it.badgeId }

            badgeList.forEach { badge ->
                badgeMap[badge.badgeId] = badge.toMap()
            }

            database.child("badges").child(userId).updateChildren(badgeMap).await()
            badgeDAO.markAsBackedUp(badgeIds)
        } catch (e: Exception) {
            Log.e(ConstantsManager.APP_TAG, "Error storing badge data: ${e.message}")
            throw e
        }
    }

    override fun getLeaderBoard(difficulty: String): Flow<List<LeaderboardModel>> = callbackFlow {
        val leaderboardRef = database.child("leaderboard").child(difficulty)
            .orderByChild("totalScore")
            .limitToLast(13)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    child.getValue(LeaderboardModel::class.java)
                }.reversed()
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        leaderboardRef.addValueEventListener(listener)
        awaitClose { leaderboardRef.removeEventListener(listener) }
    }

    override fun fetchUserData(userId: String): Flow<Result<UserData?>> = flow {
        try {
            val snapshot = database.child("users").child(userId).get().await()
            if (snapshot.exists()) {
                val userData = snapshot.getValue(UserData::class.java)
                emit(Result.Success(userData))
            } else {
                emit(Result.Success(null))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Failed to fetch user data"))
        }
    }

    override suspend fun updateUserName(username: String, avatarId: Int, oldUsername: String?): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext Result.Failure("User not logged in")

        try {
            val updates = mutableMapOf<String, Any?>()
            
            // Only update username and avatarId in user profile to avoid wiping stats
            updates["users/$userId/username"] = username
            updates["users/$userId/avatarId"] = avatarId
            
            // Remove old username claim if it changed
            if (oldUsername != null && oldUsername.lowercase() != username.lowercase()) {
                updates["usernames/${oldUsername.lowercase()}"] = null
            }
            
            // Add new username claim
            updates["usernames/${username.lowercase()}"] = userId

            database.updateChildren(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Failed to save user data")
        }
    }

    override suspend fun uploadUserData(userData: UserData): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext Result.Failure("User not logged in")
        try {
            database.child("users").child(userId).setValue(userData.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Failed to upload user stats")
        }
    }

    override fun fetchMatchHistory(userId: String): Flow<Result<List<MatchHistory>>> = flow {
        try {
            val snapshot = database.child("matches").child(userId).get().await()
            val matchHistory = snapshot.children.mapNotNull { child ->
                child.getValue(MatchHistory::class.java)
            }
            emit(Result.Success(matchHistory))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Failed to fetch match history"))
        }
    }

    override fun fetchBadges(userId: String): Flow<Result<List<BadgeEntity>>> = flow {
        try {
            val snapshot = database.child("badges").child(userId).get().await()
            val badges = snapshot.children.mapNotNull { child ->
                child.getValue(BadgeEntity::class.java)
            }
            emit(Result.Success(badges))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Failed to fetch badges"))
        }
    }

    override suspend fun isUsernameExisting(username: String): Boolean{
        return try {
            val snapshot = database.child("usernames").child(username.lowercase()).get().await()
            snapshot.exists() && snapshot.value != auth.currentUser?.uid
        } catch (e: Exception) {
            false
        }
    }
}
