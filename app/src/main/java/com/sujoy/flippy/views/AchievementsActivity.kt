package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.settings.SettingsRepository
import com.sujoy.flippy.core.theme.FliqTheme
import com.sujoy.flippy.profile.ui.AchievementsScreen
import com.sujoy.flippy.profile.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AchievementsActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                val unlockedBadges by viewModel.unlockedBadges.collectAsState()

                AchievementsScreen(
                    unlockedBadges = unlockedBadges,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
