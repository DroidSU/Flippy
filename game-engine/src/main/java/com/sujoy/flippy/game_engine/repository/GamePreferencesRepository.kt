package com.sujoy.flippy.game_engine.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface GamePreferencesRepository {
    fun shouldShowRulesOnStartup(): Boolean
    fun setShowRulesOnStartup(show: Boolean)
    fun hasShownRulesOnce(): Boolean
    fun setRulesShownOnce(shown: Boolean)
}

class GamePreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : GamePreferencesRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("flippy_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHOW_ON_STARTUP = "show_rules_on_startup"
        private const val KEY_SHOWN_ONCE = "rules_shown_once"
    }

    override fun shouldShowRulesOnStartup(): Boolean = prefs.getBoolean(KEY_SHOW_ON_STARTUP, false)

    override fun setShowRulesOnStartup(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ON_STARTUP, show).apply()
    }

    override fun hasShownRulesOnce(): Boolean = prefs.getBoolean(KEY_SHOWN_ONCE, false)

    override fun setRulesShownOnce(shown: Boolean) {
        prefs.edit().putBoolean(KEY_SHOWN_ONCE, shown).apply()
    }
}
