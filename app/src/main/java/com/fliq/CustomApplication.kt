package com.fliq

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.fliq.common.SyncScheduler
import com.fliq.common.notifications.FliqNotificationManager
import com.fliq.core.settings.SettingsRepository
import com.fliq.game_engine.repository.SoundRepository
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CustomApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var soundRepository: SoundRepository

    @Inject
    lateinit var notificationManager: FliqNotificationManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        syncScheduler.schedulePeriodicSync()

        if (settingsRepository.getNotificationsEnabled()) {
            notificationManager.scheduleDailyReminder()
        }

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                android.util.Log.d("FCM_TOKEN", "Current Token: ${task.result}")
            }
        }

        MobileAds.initialize(this@CustomApplication) {}

        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // Check if this is a debug build
        if (BuildConfig.DEBUG) {
            // Use the debug provider for development
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            // Use the Play Integrity provider for production
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // App comes to foreground
                if (soundRepository.isMusicActivated()) {
                    soundRepository.startBackgroundMusic()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                // App goes to background
                soundRepository.pauseBackgroundMusic()
            }
        })
    }
}
