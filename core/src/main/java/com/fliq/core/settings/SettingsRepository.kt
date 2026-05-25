package com.fliq.core.settings

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

    fun setZenTutorialCompleted(completed: Boolean)
    fun isZenTutorialCompleted(): Boolean

    fun setZenRotationHintDisabled(disabled: Boolean)
    fun isZenRotationHintDisabled(): Boolean

    fun setZenLandscapePreferred(preferred: Boolean)
    fun isZenLandscapePreferred(): Boolean

    fun setSpeedRunTutorialCompleted(completed: Boolean)
    fun isSpeedRunTutorialCompleted(): Boolean

    fun setSpeedRunRotationHintDisabled(disabled: Boolean)
    fun isSpeedRunRotationHintDisabled(): Boolean

    fun setMirageTutorialCompleted(completed: Boolean)
    fun isMirageTutorialCompleted(): Boolean

    fun setMirageRotationHintDisabled(disabled: Boolean)
    fun isMirageRotationHintDisabled(): Boolean

    fun setMinefieldTutorialCompleted(completed: Boolean)
    fun isMinefieldTutorialCompleted(): Boolean

    fun setMinefieldRotationHintDisabled(disabled: Boolean)
    fun isMinefieldRotationHintDisabled(): Boolean

    fun setFrenzyTutorialCompleted(completed: Boolean)
    fun isFrenzyTutorialCompleted(): Boolean

    fun setFrenzyRotationHintDisabled(disabled: Boolean)
    fun isFrenzyRotationHintDisabled(): Boolean
}
