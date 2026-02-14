package com.flippy.game2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.theme.FlippyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewGameActivity : ComponentActivity() {
    
    private val viewModel: NewGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val title by viewModel.title.collectAsState()
            val description by viewModel.description.collectAsState()
            val activeTarget by viewModel.activeTarget.collectAsState()
            
            FlippyTheme(settingsRepository = viewModel.settingsRepository) {
                NewGameScreen(
                    title = title,
                    description = description,
                    activeTarget = activeTarget,
                    events = viewModel.events,
                    onCanvasTap = { x, y, width, height, density ->
                        viewModel.onCanvasTap(x, y, width, height, density)
                    }
                )
            }
        }
    }
}
