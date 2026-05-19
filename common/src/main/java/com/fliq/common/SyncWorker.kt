package com.fliq.common

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fliq.common.repository.ProfileRepository
import com.fliq.database.repository.BadgeRepository
import com.fliq.database.repository.MatchRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val matchRepository: MatchRepository,
    private val badgeRepository: BadgeRepository,
    private val networkRepository: NetworkRepository,
    private val profileRepository: ProfileRepository,
    private val auth: FirebaseAuth
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Starting sync work...")
        
        return try {
            if (networkRepository.isInternetAvailable()) {
                // Sync Matches
                val pendingMatches = matchRepository.getPendingMatches()
                if (pendingMatches.isNotEmpty()) {
                    Log.d("SyncWorker", "Syncing ${pendingMatches.size} matches")
                    networkRepository.storeMatchData(pendingMatches)
                }

                // Sync Badges
                val pendingBadges = badgeRepository.getPendingBadges()
                if (pendingBadges.isNotEmpty()) {
                    Log.d("SyncWorker", "Syncing ${pendingBadges.size} badges")
                    networkRepository.storeBadgeData(pendingBadges)
                }

                // Sync User Stats
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val localStats = profileRepository.getUserDataSync(userId)
                    if (localStats != null) {
                        Log.d("SyncWorker", "Syncing user stats for $userId")
                        networkRepository.uploadUserData(localStats)
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during sync", e)
            Result.retry()
        }
    }
}
