package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.profile.ui.ProfileScreen
import com.sujoy.flippy.profile.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                val username by viewModel.username.collectAsState()
                val avatarId by viewModel.avatarId.collectAsState()
                val uiState by viewModel.uiState.collectAsState()
                val isEditing by viewModel.isEditing.collectAsState()
                val totalMatches by viewModel.totalMatchesPlayed.collectAsState()
                val highestScore by viewModel.highestScore.collectAsState()
                val longestRound by viewModel.longestRound.collectAsState()

                ProfileScreen(
                    username = username,
                    avatarId = avatarId,
                    uiState = uiState,
                    isEditing = isEditing,
                    onSaveProfile = { u, a ->
                        viewModel.saveProfile(u, a)
                        viewModel.onCancelEdit()
                    },
                    onBackClick = { finish() },
                    onEdit = { viewModel.onEdit() },
                    onDismissEdit = {
                        viewModel.onCancelEdit()
                    },
                    totalMatches = totalMatches,
                    highestScore = highestScore,
                    longestRound = longestRound
                )
            }
        }
    }
}
