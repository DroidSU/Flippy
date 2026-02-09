package com.sujoy.flippy.common

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sujoy.flippy.common.UtilityMethods.Companion.toMap
import com.sujoy.flippy.core.ConstantsManager
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val matchDAO: MatchDAO,
    private val auth: FirebaseAuth
) : NetworkRepository {

    private val database = FirebaseDatabase.getInstance().reference

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isInternetAvailable(): Boolean {
        return UtilityMethods.isInternetAvailable(context)
    }

    override fun storeMatchData(matchList: List<MatchHistory>) {
        val userId = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val matchMap = mutableMapOf<String, Any>()
                val matchIds = matchList.map { it.id }

                matchList.forEach { match ->
                    matchMap[match.id] = match
                }

                database.child("matches").child(userId).updateChildren(matchMap).await()
                matchDAO.markMatchesAsBackedUp(matchIds)
            } catch (e: Exception) {
                Log.e(ConstantsManager.APP_TAG, "Error storing match data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun getLeaderBoard(): Flow<List<LeaderboardModel>> = callbackFlow {
        val leaderboardRef = database.child("leaderboard")
            .orderByChild("totalScore")
            .limitToLast(10)

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

    override suspend fun saveUserData(username: String, avatarId: Int, oldUsername: String?): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext Result.Failure("User not logged in")
        val userdata = UserData(userId = userId, username = username, avatarId = avatarId)

        try {
            val updates = mutableMapOf<String, Any?>()
            
            // Update user profile
            updates["users/$userId"] = userdata.toMap()
            
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

    override suspend fun isUsernameExisting(username: String): Boolean{
        return try {
            val snapshot = database.child("usernames").child(username.lowercase()).get().await()
            snapshot.exists() && snapshot.value != auth.currentUser?.uid
        } catch (e: Exception) {
            false
        }
    }
}
