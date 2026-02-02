package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.database.AppDatabase
import com.sujoy.flippy.profile.repository.ProfileRepositoryImpl
import com.sujoy.flippy.profile.ui.ProfileScreen
import com.sujoy.flippy.profile.vm.ProfileViewModel

class ProfileActivity : ComponentActivity() {

    @Suppress("UNCHECKED_CAST")
    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = ProfileRepositoryImpl(applicationContext, database.matchDao())
                return ProfileViewModel(repository) as T
            }
        }
    }

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
