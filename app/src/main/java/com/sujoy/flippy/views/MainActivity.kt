package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.repository.SoundRepositoryImpl
import com.sujoy.flippy.game_engine.ui.GameScreen
import com.sujoy.flippy.game_engine.viewmodel.GameViewModel
import com.sujoy.flippy.vm.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var soundRepository: SoundRepository

    private val gameViewModel: GameViewModel by viewModels {
        ViewModelFactory(soundRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundRepository = SoundRepositoryImpl(this)

        setContent {
            FlippyTheme {
                val tiles by gameViewModel.tiles.collectAsState()
                val score by gameViewModel.score.collectAsState()
                val lives by gameViewModel.lives.collectAsState()
                val status by gameViewModel.status.collectAsState()

                GameScreen(
                    tiles = tiles,
                    score = score,
                    lives = lives,
                    status = status,
                    onTileTapped = gameViewModel::onTileTapped,
                    onPlayClick = gameViewModel::startGame,
                    onResetGame = gameViewModel::resetGame
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundRepository.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        soundRepository.pauseBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundRepository.release()
    }
}
