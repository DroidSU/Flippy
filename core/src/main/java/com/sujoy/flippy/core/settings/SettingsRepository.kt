package com.sujoy.flippy.core.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val themeFlow: StateFlow<AppTheme>
    val gameSoundFlow: StateFlow<Boolean>

    fun setAppTheme(theme: AppTheme)
    fun getAppTheme(): AppTheme

    fun setNotificationsEnabled(enabled: Boolean)
    fun getNotificationsEnabled(): Boolean

    fun setGameSoundEnabled(enabled: Boolean)
    fun getGameSoundEnabled(): Boolean

    fun setHapticFeedbackEnabled(enabled: Boolean)
    fun getHapticFeedbackEnabled(): Boolean
}
