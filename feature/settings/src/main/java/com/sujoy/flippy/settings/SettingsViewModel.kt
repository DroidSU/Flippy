package com.sujoy.flippy.settings

import androidx.lifecycle.ViewModel
import com.sujoy.flippy.core.settings.AppTheme
import com.sujoy.flippy.core.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _appTheme = MutableStateFlow(settingsRepository.getAppTheme())
    val appTheme = _appTheme.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(settingsRepository.getNotificationsEnabled())
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _gameSoundEnabled = MutableStateFlow(settingsRepository.getGameSoundEnabled())
    val gameSoundEnabled = _gameSoundEnabled.asStateFlow()

    private val _hapticFeedbackEnabled = MutableStateFlow(settingsRepository.getHapticFeedbackEnabled())
    val hapticFeedbackEnabled = _hapticFeedbackEnabled.asStateFlow()

    fun onNotificationChanged(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        settingsRepository.setNotificationsEnabled(enabled)
    }

    fun onGameSoundChanged(enabled: Boolean) {
        _gameSoundEnabled.value = enabled
        settingsRepository.setGameSoundEnabled(enabled)
    }

    fun onHapticFeedbackChanged(enabled: Boolean) {
        _hapticFeedbackEnabled.value = enabled
        settingsRepository.setHapticFeedbackEnabled(enabled)
    }

    fun onThemeChanged(theme: AppTheme) {
        _appTheme.value = theme
        settingsRepository.setAppTheme(theme)
    }
}
