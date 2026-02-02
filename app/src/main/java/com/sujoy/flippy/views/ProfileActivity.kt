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
import com.google.firebase.auth.FirebaseAuth
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
                val auth = FirebaseAuth.getInstance()
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = ProfileRepositoryImpl(applicationContext, database.matchDao())
                return ProfileViewModel(auth.currentUser?.uid ?: "anonymous", repository) as T
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
                val highestScoreMatch by viewModel.highestScoreMatch.collectAsState()
                val matchHistory by viewModel.matchHistory.collectAsState()
                val uiState by viewModel.uiState.collectAsState()

                ProfileScreen(
                    username = username,
                    avatarId = avatarId,
                    highestScoreMatch = highestScoreMatch,
                    matchHistory = matchHistory,
                    uiState = uiState,
                    onSaveProfile = { u, a ->
                        viewModel.saveProfile(u, a)
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }
}
