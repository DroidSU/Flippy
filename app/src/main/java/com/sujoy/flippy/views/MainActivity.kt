package com.sujoy.flippy.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.ui.GameScreen
import com.sujoy.flippy.game_engine.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var soundRepository: SoundRepository

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FlippyTheme {
                val tiles by gameViewModel.tiles.collectAsState()
                val score by gameViewModel.score.collectAsState()
                val lives by gameViewModel.lives.collectAsState()
                val status by gameViewModel.status.collectAsState()
                val difficulty by gameViewModel.difficulty.collectAsState()
                val gameTime by gameViewModel.gameTime.collectAsState()
                val topThreeScores by gameViewModel.topThreeScores.collectAsState()
                val showRules by gameViewModel.showRules.collectAsState()
                val isPaused by gameViewModel.isGamePaused.collectAsState()

                GameScreen(
                    tiles = tiles,
                    score = score,
                    lives = lives,
                    status = status,
                    difficulty = difficulty,
                    gameTime = gameTime,
                    leaderboard = topThreeScores,
                    showRules = showRules,
                    onTileTapped = gameViewModel::onTileTapped,
                    onPlayClick = gameViewModel::startGame,
                    onResetGame = gameViewModel::resetGame,
                    onDifficultyChange = gameViewModel::setDifficulty,
                    onRulesDismissed = gameViewModel::onRulesDismissed,
                    onHelpClick = gameViewModel::showRulesDialog,
                    onSignOutClick = {
                        gameViewModel.signOut()
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                        finish()
                    },
                    isPaused = isPaused,
                    onProfileIntentClicked = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    },
                    onLeaderboardIntentClicked = {
                        startActivity(Intent(this, LeaderboardActivity::class.java))
                    },
                    onPreferencesIntentClicked = {

                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundRepository.startBackgroundMusic()
//        if (gameViewModel.status.value == GameStatus.PLAYING) {
//            gameViewModel.pauseGameTemporarily()
//        }
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
