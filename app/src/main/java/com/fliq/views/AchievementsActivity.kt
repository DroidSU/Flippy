package com.fliq.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.profile.ui.AchievementsScreen
import com.fliq.profile.vm.ProfileViewModel
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
