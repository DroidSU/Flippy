package com.fliq.common

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.fliq.core.ConstantsManager
import com.fliq.core.models.UserData
import com.fliq.core.models.toMap
import com.fliq.database.BadgeDAO
import com.fliq.database.MatchDAO
import com.fliq.database.MatchHistory
import com.fliq.database.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) : NetworkRepository {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isInternetAvailable(): Boolean {
        return UtilityMethods.isInternetAvailable(context)
    }

    override suspend fun storeMatchData(matchList: List<MatchHistory>) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext

        try {
            val matchIds = matchList.map { it.id }

            // Write each match object atomically using setValue at its specific path.
            // This ensures a single write operation per match node, preventing multiple
            // child-created events that can occur with multi-path updates or field-level updates.
            matchList.forEach { match ->
                database.child("matches")
                    .child(match.challengeName)
                    .child(userId)
                    .child(match.id)
                    .setValue(match.toMap())
                    .await()
            }

            matchDAO.markMatchesAsBackedUp(matchIds)
        } catch (e: Exception) {
            Log.e(ConstantsManager.APP_TAG, "Error storing match data: ${e.message}")
            e.printStackTrace()
            throw e 
        }
    }

    override fun getLeaderBoard(challengeName: String): Flow<List<LeaderboardModel>> = callbackFlow {
        val leaderboardRef = database.child("leaderboard").child(challengeName)
            .orderByChild("totalScore")
            .limitToLast(100)

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

    override suspend fun updateLatencyOffset(latencyOffset: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext Result.Failure("User not logged in")
        try {
            database.child("users").child(userId).child("latencyOffset").setValue(latencyOffset).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Failed to update latency offset")
        }
    }

    override fun fetchMatchHistory(userId: String): Flow<Result<List<MatchHistory>>> = flow {
        try {
            val challengeTypes = listOf("SPEED_RUN", "MIRAGE", "MINEFIELD", "ZEN_MODE", "FRENZY")
            val allMatches = mutableListOf<MatchHistory>()
            
            for (type in challengeTypes) {
                val snapshot = database.child("matches").child(type).child(userId).get().await()
                val matches = snapshot.children.mapNotNull { child ->
                    child.getValue(MatchHistory::class.java)
                }
                allMatches.addAll(matches)
            }
            
            emit(Result.Success(allMatches))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Failed to fetch match history"))
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
