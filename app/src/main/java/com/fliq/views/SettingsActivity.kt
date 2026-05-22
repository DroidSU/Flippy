package com.fliq.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fliq.common.UtilityMethods
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.settings.SettingsScreen
import com.fliq.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val selectedTheme by viewModel.appTheme.collectAsState()
            val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
            val gameSoundEnabled by viewModel.gameSoundEnabled.collectAsState()
            val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsState()

            FliqTheme(settingsRepository = settingsRepository) {
                SettingsScreen(
                    selectedTheme = selectedTheme,
                    notificationsEnabled = notificationsEnabled,
                    gameSoundEnabled = gameSoundEnabled,
                    hapticFeedbackEnabled = hapticFeedbackEnabled,
                    versionName = UtilityMethods.getAppVersionName(this),
                    onThemeChange = {
                        viewModel.onThemeChanged(it)
                    },
                    onNotificationsChange = {
                        viewModel.onNotificationChanged(it)
                    },
                    onGameSoundChange = {
                        viewModel.onGameSoundChanged(it)
                    },
                    onHapticFeedbackChange = {
                        viewModel.onHapticFeedbackChanged(it)
                    },
                    onSignOut = {
                        viewModel.signOut {
                            val intent = Intent(this@SettingsActivity, SplashActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    },
                    onBackClick = { finish() },
                )
            }
        }
    }
}
