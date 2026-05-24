package com.fliq.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fliq.auth.ui.ReflexCalibrationScreen
import com.fliq.auth.viewmodel.CalibrationViewModel
import com.fliq.common.AppUIState
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalibrationActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: CalibrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                val uiState by viewModel.uiState.collectAsState()

                if (uiState is AppUIState.Success) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                ReflexCalibrationScreen(
                    onCalibrationComplete = { reflex ->
                        viewModel.saveBaseReflex(reflex)
                    }
                )
            }
        }
    }
}
