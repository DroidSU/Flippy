package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sujoy.flippy.components.GameScreen
import com.sujoy.flippy.repositories.game.SoundRepository
import com.sujoy.flippy.repositories.game.SoundRepositoryImpl
import com.sujoy.flippy.ui.theme.FlippyTheme
import com.sujoy.flippy.vm.GameViewModel
import com.sujoy.flippy.vm.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var soundRepository: SoundRepository

    private val gameViewModel: GameViewModel by viewModels {
        ViewModelFactory(soundRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the SoundRepository
        soundRepository = SoundRepositoryImpl(this)

        setContent {
            FlippyTheme(dynamicColor = false) {
                GameScreen(viewModel = gameViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume background music
        soundRepository.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        // Pause the background music when the app is not in the foreground
        soundRepository.pauseBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release all sound resources
        soundRepository.release()
    }
}
