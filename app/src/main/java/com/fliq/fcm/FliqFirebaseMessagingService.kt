package com.fliq.fcm

import android.util.Log
import com.fliq.common.notifications.FliqNotificationManager
import com.fliq.core.settings.SettingsRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FliqFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: FliqNotificationManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (!settingsRepository.getNotificationsEnabled()) return

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Fliq Update"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: "New content is available!"

        notificationManager.showNotification(
            channelId = FliqNotificationManager.REMOTE_CHANNEL_ID,
            title = title,
            message = message
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New Token: $token")
    }
}
