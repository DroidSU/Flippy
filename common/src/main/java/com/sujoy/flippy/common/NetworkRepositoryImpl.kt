package com.sujoy.flippy.common

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sujoy.flippy.core.ConstantsManager
import com.sujoy.flippy.core.models.MatchHistory
import com.sujoy.flippy.database.MatchDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NetworkRepositoryImpl(
    private val context: Context,
    private val matchDAO: MatchDAO
) : NetworkRepository {

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

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

                // matches/userId/matchId
                database.child("matches").child(userId).updateChildren(matchMap).await()
                matchDAO.markMatchesAsBackedUp(matchIds)
            } catch (e: Exception) {
                Log.e(ConstantsManager.APP_TAG, "Error storing match data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun storeUserData(userName: String, avatarResourceId: Int) {
//        val userId = auth.currentUser?.uid ?: return
//        val userMap = mapOf(
//            "username" to userName,
//            "avatarId" to avatarResourceId
//        )
//        database.child("users").child(userId).setValue(userMap)
    }
}
