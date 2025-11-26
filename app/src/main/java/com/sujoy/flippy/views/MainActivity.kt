package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sujoy.flippy.components.GameScreen
import com.sujoy.flippy.ui.theme.FlippyTheme
import com.sujoy.flippy.utils.SoundPlayer
import com.sujoy.flippy.vm.GameViewModel
import com.sujoy.flippy.vm.GameViewModelFactory

class MainActivity : ComponentActivity() {

    // Single instance of SoundPlayer for all sounds
    private lateinit var soundPlayer: SoundPlayer

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application, soundPlayer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the SoundPlayer
        soundPlayer = SoundPlayer(this)

        setContent {
            FlippyTheme(dynamicColor = false) {
                GameScreen(viewModel = gameViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume background music
        soundPlayer.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        // Pause the background music when the app is not in the foreground
        soundPlayer.pauseBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release all sound resources
        soundPlayer.release()
    }
}