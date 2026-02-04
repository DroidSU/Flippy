package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.leaderboard.viewmodels.LeaderboardViewModel
import com.sujoy.leaderboard.views.LeaderboardScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardActivity : ComponentActivity() {
    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                val leaderboardList by viewModel.leaderboardState.collectAsState()
                val uiState by viewModel.uiState.collectAsState()

                LeaderboardScreen(
                    uiState = uiState,
                    leaderboardList = leaderboardList,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
