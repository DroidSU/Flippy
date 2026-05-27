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
import com.fliq.game_engine.repository.SoundRepository
import com.fliq.profile.ui.ProfileScreen
import com.fliq.profile.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var soundRepository: SoundRepository

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                val username by viewModel.username.collectAsState()
                val avatarId by viewModel.avatarId.collectAsState()
                val uiState by viewModel.uiState.collectAsState()
                val isEditing by viewModel.isEditing.collectAsState(false)
                val totalMatches by viewModel.totalMatchesPlayed.collectAsState()
                val highestScore by viewModel.highestScore.collectAsState()
                val longestRound by viewModel.longestRound.collectAsState()
                val reflexAverage by viewModel.reflexAverage.collectAsState()
                val accuracyRate by viewModel.accuracyRate.collectAsState()
                val unlockedBadges by viewModel.unlockedBadges.collectAsState()
                val latencyOffset by viewModel.latencyOffset.collectAsState()
                val showCalibration by viewModel.showCalibration.collectAsState()

                if (showCalibration) {
                    androidx.compose.runtime.DisposableEffect(Unit) {
                        soundRepository.stopBackgroundMusic()
                        onDispose {
                            // Optionally resume music here if needed, 
                            // but usually it resumes on activity return or specific actions
                        }
                    }

                    com.fliq.auth.ui.ReflexCalibrationScreen(
                        onCalibrationComplete = { offset ->
                            viewModel.recalibrateLatency(offset)
                            viewModel.onCalibrationDismiss()
                        },
                        onDismiss = {
                            viewModel.onCalibrationDismiss()
                        }
                    )
                } else {
                    ProfileScreen(
                        username = username,
                        avatarId = avatarId,
                        uiState = uiState,
                        isEditing = isEditing,
                        onAvatarIdChanged = { viewModel.onAvatarIdChanged(it) },
                        onSaveProfile = { _, a ->
                            viewModel.saveProfile(a)
                            viewModel.onCancelEdit()
                        },
                        onBackClick = { finish() },
                        onEdit = { viewModel.onEdit() },
                        onDismissEdit = {
                            viewModel.onCancelEdit()
                        },
                        totalMatches = totalMatches,
                        highestScore = highestScore,
                        longestRound = longestRound,
                        accuracyRate = accuracyRate,
                        reflexAverage = reflexAverage,
                        latencyOffset = latencyOffset,
                        unlockedBadges = unlockedBadges,
                        onRecalibrate = { viewModel.onRecalibrate() }
                    )
                }
            }
        }
    }
}
