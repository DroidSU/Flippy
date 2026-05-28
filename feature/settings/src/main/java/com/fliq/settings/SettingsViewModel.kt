package com.fliq.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AppUIState
import com.fliq.common.NetworkRepository
import com.fliq.common.Result
import com.fliq.common.notifications.FliqNotificationManager
import com.fliq.common.repository.ProfileRepository
import com.fliq.core.settings.AppTheme
import com.fliq.core.settings.SettingsRepository
import com.fliq.database.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository,
    private val networkRepository: NetworkRepository,
    private val auth: FirebaseAuth,
    private val database: AppDatabase,
    private val notificationManager: FliqNotificationManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _showCalibration = MutableStateFlow(false)
    val showCalibration = _showCalibration.asStateFlow()

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
        if (enabled) {
            notificationManager.scheduleDailyReminder()
        } else {
            notificationManager.cancelDailyReminder()
        }
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

    fun onRecalibrate() {
        _showCalibration.value = true
    }

    fun onCalibrationDismiss() {
        _showCalibration.value = false
    }

    fun saveLatencyOffset(offsetMs: Long) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }
            
            when (val result = networkRepository.updateLatencyOffset(offsetMs)) {
                is Result.Success -> {
                    val currentData = profileRepository.getUserDataSync(userId)
                    if (currentData != null) {
                        profileRepository.saveUserData(currentData.copy(latencyOffset = offsetMs))
                    }
                    _uiState.update { AppUIState.Success }
                    onCalibrationDismiss()
                }
                is Result.Failure -> {
                    _uiState.update { AppUIState.Error(result.message) }
                }
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()
            database.clearAllTables()
            onComplete()
        }
    }
}
