package com.fliq.common.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: FliqNotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        notificationManager.showNotification(
            channelId = FliqNotificationManager.LOCAL_CHANNEL_ID,
            title = "Time to Fliq!",
            message = "Your daily challenge is waiting for you. Come and beat your high score!"
        )
        return Result.success()
    }
}
