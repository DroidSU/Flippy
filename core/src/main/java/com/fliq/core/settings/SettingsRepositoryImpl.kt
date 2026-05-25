package com.fliq.core.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("fliq_settings", Context.MODE_PRIVATE)

    private val _themeFlow = MutableStateFlow(getAppTheme())
    override val themeFlow: StateFlow<AppTheme> = _themeFlow.asStateFlow()

    private val _gameSoundFlow = MutableStateFlow(getGameSoundEnabled())
    override val gameSoundFlow: StateFlow<Boolean> = _gameSoundFlow.asStateFlow()

    init {
        applyTheme(getAppTheme())
    }

    companion object {
        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_GAME_SOUND = "game_sound_enabled"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback_enabled"
        private const val KEY_ZEN_TUTORIAL_COMPLETED = "zen_tutorial_completed"
        private const val KEY_ZEN_ROTATION_HINT_DISABLED = "zen_rotation_hint_disabled"
        private const val KEY_ZEN_LANDSCAPE_PREFERRED = "zen_landscape_preferred"
        private const val KEY_SPEED_RUN_TUTORIAL_COMPLETED = "speed_run_tutorial_completed"
        private const val KEY_SPEED_RUN_ROTATION_HINT_DISABLED = "speed_run_rotation_hint_disabled"
        private const val KEY_MIRAGE_TUTORIAL_COMPLETED = "mirage_tutorial_completed"
        private const val KEY_MIRAGE_ROTATION_HINT_DISABLED = "mirage_rotation_hint_disabled"
        private const val KEY_MINEFIELD_TUTORIAL_COMPLETED = "minefield_tutorial_completed"
        private const val KEY_MINEFIELD_ROTATION_HINT_DISABLED = "minefield_rotation_hint_disabled"
        private const val KEY_FRENZY_TUTORIAL_COMPLETED = "frenzy_tutorial_completed"
        private const val KEY_FRENZY_ROTATION_HINT_DISABLED = "frenzy_rotation_hint_disabled"
    }

    override fun setAppTheme(theme: AppTheme) {
        prefs.edit { putString(KEY_APP_THEME, theme.name) }
        _themeFlow.value = theme
        applyTheme(theme)
    }

    override fun getAppTheme(): AppTheme {
        val themeName = prefs.getString(KEY_APP_THEME, AppTheme.DARK.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.DARK.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.DARK
        }
    }

    private fun applyTheme(theme: AppTheme) {
        /* 
        val mode = when (theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        */
        // Force Dark mode only as per requirement
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIFICATIONS, enabled) }
    }

    override fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    override fun setGameSoundEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_GAME_SOUND, enabled) }
        _gameSoundFlow.value = enabled
    }

    override fun getGameSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_GAME_SOUND, true)
    }

    override fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_HAPTIC_FEEDBACK, enabled) }
    }

    override fun getHapticFeedbackEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
    }

    override fun setZenTutorialCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_ZEN_TUTORIAL_COMPLETED, completed) }
    }

    override fun isZenTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_ZEN_TUTORIAL_COMPLETED, false)
    }

    override fun setZenRotationHintDisabled(disabled: Boolean) {
        prefs.edit { putBoolean(KEY_ZEN_ROTATION_HINT_DISABLED, disabled) }
    }

    override fun isZenRotationHintDisabled(): Boolean {
        return prefs.getBoolean(KEY_ZEN_ROTATION_HINT_DISABLED, false)
    }

    override fun setZenLandscapePreferred(preferred: Boolean) {
        prefs.edit { putBoolean(KEY_ZEN_LANDSCAPE_PREFERRED, preferred) }
    }

    override fun isZenLandscapePreferred(): Boolean {
        return prefs.getBoolean(KEY_ZEN_LANDSCAPE_PREFERRED, false)
    }

    override fun setSpeedRunTutorialCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_SPEED_RUN_TUTORIAL_COMPLETED, completed) }
    }

    override fun isSpeedRunTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_SPEED_RUN_TUTORIAL_COMPLETED, false)
    }

    override fun setSpeedRunRotationHintDisabled(disabled: Boolean) {
        prefs.edit { putBoolean(KEY_SPEED_RUN_ROTATION_HINT_DISABLED, disabled) }
    }

    override fun isSpeedRunRotationHintDisabled(): Boolean {
        return prefs.getBoolean(KEY_SPEED_RUN_ROTATION_HINT_DISABLED, false)
    }

    override fun setMirageTutorialCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_MIRAGE_TUTORIAL_COMPLETED, completed) }
    }

    override fun isMirageTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_MIRAGE_TUTORIAL_COMPLETED, false)
    }

    override fun setMirageRotationHintDisabled(disabled: Boolean) {
        prefs.edit { putBoolean(KEY_MIRAGE_ROTATION_HINT_DISABLED, disabled) }
    }

    override fun isMirageRotationHintDisabled(): Boolean {
        return prefs.getBoolean(KEY_MIRAGE_ROTATION_HINT_DISABLED, false)
    }

    override fun setMinefieldTutorialCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_MINEFIELD_TUTORIAL_COMPLETED, completed) }
    }

    override fun isMinefieldTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_MINEFIELD_TUTORIAL_COMPLETED, false)
    }

    override fun setMinefieldRotationHintDisabled(disabled: Boolean) {
        prefs.edit { putBoolean(KEY_MINEFIELD_ROTATION_HINT_DISABLED, disabled) }
    }

    override fun isMinefieldRotationHintDisabled(): Boolean {
        return prefs.getBoolean(KEY_MINEFIELD_ROTATION_HINT_DISABLED, false)
    }

    override fun setFrenzyTutorialCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_FRENZY_TUTORIAL_COMPLETED, completed) }
    }

    override fun isFrenzyTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_FRENZY_TUTORIAL_COMPLETED, false)
    }

    override fun setFrenzyRotationHintDisabled(disabled: Boolean) {
        prefs.edit { putBoolean(KEY_FRENZY_ROTATION_HINT_DISABLED, disabled) }
    }

    override fun isFrenzyRotationHintDisabled(): Boolean {
        return prefs.getBoolean(KEY_FRENZY_ROTATION_HINT_DISABLED, false)
    }
}
