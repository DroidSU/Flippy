package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.settings.SettingsRepository
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.settings.SettingsScreen
import com.sujoy.flippy.settings.SettingsViewModel
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

            FlippyTheme(settingsRepository = settingsRepository) {
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
                    onBackClick = { finish() },
                )
            }
        }
    }
}
