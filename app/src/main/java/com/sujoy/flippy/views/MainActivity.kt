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
import com.sujoy.flippy.game_engine.repository.MatchRepositoryImpl
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.repository.SoundRepositoryImpl
import com.sujoy.flippy.game_engine.ui.GameScreen
import com.sujoy.flippy.game_engine.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private lateinit var soundRepository: SoundRepository

    private val gameViewModel: GameViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val auth = FirebaseAuth.getInstance()
                val db = AppDatabase.getDatabase(applicationContext)
                val matchRepository = MatchRepositoryImpl(db.matchDao())
                val soundRepo = SoundRepositoryImpl(applicationContext)
                return GameViewModel(auth, soundRepo, matchRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        soundRepository = SoundRepositoryImpl(applicationContext)

        enableEdgeToEdge()

        setContent {
            FlippyTheme {
                val tiles by gameViewModel.tiles.collectAsState()
                val score by gameViewModel.score.collectAsState()
                val lives by gameViewModel.lives.collectAsState()
                val status by gameViewModel.status.collectAsState()
                val difficulty by gameViewModel.difficulty.collectAsState()
                val gameTime by gameViewModel.gameTime.collectAsState()
                val leaderboard by gameViewModel.leaderBoard.collectAsState()

                GameScreen(
                    tiles = tiles,
                    score = score,
                    lives = lives,
                    status = status,
                    difficulty = difficulty,
                    gameTime = gameTime,
                    leaderboard = leaderboard,
                    onTileTapped = gameViewModel::onTileTapped,
                    onPlayClick = gameViewModel::startGame,
                    onResetGame = gameViewModel::resetGame,
                    onDifficultyChange = gameViewModel::setDifficulty
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
