package com.fliq.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.profile.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository) {
                val username by profileViewModel.username.collectAsState()
                val highestScore by profileViewModel.highestScore.collectAsState()
                val accuracy by profileViewModel.accuracyRate.collectAsState()
                val unlockedBadges by profileViewModel.unlockedBadges.collectAsState()

                DashboardScreen(
                    userName = username,
                    highestScore = highestScore,
                    accuracy = accuracy,
                    unlockedBadges = unlockedBadges,
                    onChallengeSelected = { challenge ->
                        val intent = Intent(this, GameActivity::class.java).apply {
                            putExtra(GameActivity.EXTRA_CHALLENGE, challenge.name)
                        }
                        startActivity(intent)
                    },
                    onProfileClick = { startActivity(Intent(this, ProfileActivity::class.java)) },
                    onLeaderboardClick = { startActivity(Intent(this, LeaderboardActivity::class.java)) },
                    onAchievementsClick = { startActivity(Intent(this, AchievementsActivity::class.java)) },
                    onSettingsClick = { startActivity(Intent(this, SettingsActivity::class.java)) }
                )
            }
        }
    }
}
