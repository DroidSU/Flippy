package com.fliq.views

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fliq.auth.ui.ReflexCalibrationScreen
import com.fliq.auth.viewmodel.CalibrationViewModel
import com.fliq.common.AppUIState
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.game_engine.repository.SoundRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalibrationActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var soundRepository: SoundRepository

    private val viewModel: CalibrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force Landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        enableEdgeToEdge()
        hideSystemUI()

        window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                val uiState by viewModel.uiState.collectAsState()
                val calState by viewModel.calibrationState.collectAsState()

                if (uiState is AppUIState.Success) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                androidx.compose.runtime.DisposableEffect(Unit) {
                    soundRepository.stopBackgroundMusic()
                    onDispose { }
                }

                ReflexCalibrationScreen(
                    currentState = calState.currentState,
                    currentTrial = calState.currentTrial,
                    totalTrials = calState.totalTrials,
                    lastOffset = calState.lastOffset,
                    trials = calState.trials,
                    averageOffset = calState.averageOffset,
                    onStartCalibration = viewModel::startCalibration,
                    onRecordTrial = viewModel::recordTrial,
                    onCalibrationComplete = { offset ->
                        viewModel.saveLatencyOffset(offset)
                    },
                    onRetake = viewModel::startCalibration,
                    onDismiss = {
                        finish()
                    },
                    showCancelButton = false
                )
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}
