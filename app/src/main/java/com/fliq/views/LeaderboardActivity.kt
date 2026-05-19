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
import com.fliq.leaderboard.viewmodels.LeaderboardViewModel
import com.fliq.leaderboard.views.LeaderboardScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LeaderboardActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                val leaderboardList by viewModel.leaderboard.collectAsState()
                val uiState by viewModel.uiState.collectAsState()
                val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
                val myScoresList by viewModel.myScores.collectAsState(emptyList())

                LeaderboardScreen(
                    uiState = uiState,
                    leaderboardList = leaderboardList,
                    myScores = myScoresList,
                    onSwitchDifficulty = {
                        viewModel.filterWithDifficulty(it)
                    },
                    onBackClick = { finish() },
                    selectedTabIndex = selectedTabIndex,
                    onSwitchTab = {
                        viewModel.onSwitchTab(it)
                    }
                )
            }
        }
    }
}
