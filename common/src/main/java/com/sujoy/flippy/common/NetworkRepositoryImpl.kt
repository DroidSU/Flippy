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
import com.sujoy.flippy.core.ConstantsManager
import com.sujoy.flippy.database.MatchDAO
import com.sujoy.flippy.database.MatchHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

                // flippy_db/matches/userId/matchId
                database.child("matches").child(userId).updateChildren(matchMap).await()
                matchDAO.markMatchesAsBackedUp(matchIds)
            } catch (e: Exception) {
                Log.e(ConstantsManager.APP_TAG, "Error storing match data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun storeUserData(userName: String, avatarResourceId: Int) {
        val userId = auth.currentUser?.uid ?: return
        val userMap = mapOf(
            "username" to userName,
            "avatarId" to avatarResourceId
        )
        database.child("users").child(userId).setValue(userMap)
    }

    override fun getLeaderBoard(): Flow<List<LeaderboardModel>> = callbackFlow {
        val leaderboardRef = database.child("leaderboard")
            .orderByChild("score")
            .limitToLast(10)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    child.getValue(LeaderboardModel::class.java)
                }.reversed() // limitToLast gives ascending, so we reverse for descending scores
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        leaderboardRef.addValueEventListener(listener)
        awaitClose { leaderboardRef.removeEventListener(listener) }
    }
}
