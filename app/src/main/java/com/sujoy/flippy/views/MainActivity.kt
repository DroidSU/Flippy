package com.sujoy.flippy.views

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.flippy.core.settings.SettingsRepository
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.game_engine.models.GameEffect
import com.sujoy.flippy.game_engine.models.VibrationType
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.ui.GameScreen
import com.sujoy.flippy.game_engine.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var soundRepository: SoundRepository
    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FlippyTheme(settingsRepository) {
                val tiles by gameViewModel.tiles.collectAsState()
                val score by gameViewModel.score.collectAsState()
                val lives by gameViewModel.lives.collectAsState()
                val status by gameViewModel.status.collectAsState()
                val difficulty by gameViewModel.difficulty.collectAsState()
                val gameTime by gameViewModel.gameTime.collectAsState()
                val topThreeScores by gameViewModel.topThreeScores.collectAsState()
                val showRules by gameViewModel.showRules.collectAsState()
                val isPaused by gameViewModel.isGamePaused.collectAsState()
                val streak by gameViewModel.streak.collectAsState()
                val reactionTime by gameViewModel.lastReactionTime.collectAsState()
                
                // New stats for end-game overlay
                val totalTaps by gameViewModel.totalTaps.collectAsState()
                val correctTaps by gameViewModel.correctTaps.collectAsState()
                val maxStreak by gameViewModel.maxStreak.collectAsState()
                val totalReflexTime by gameViewModel.totalReflexTime.collectAsState()

                LaunchedEffect(Unit) {
                    gameViewModel.effects.collectLatest { effect ->
                        when (effect) {
                            is GameEffect.Vibration -> {
                                if (settingsRepository.getHapticFeedbackEnabled()) {
                                    triggerVibration(effect.type)
                                }
                            }
                            else -> {}
                        }
                    }
                }

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
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    streak = streak,
                    reactionTime = reactionTime,
                    totalTaps = totalTaps,
                    correctTaps = correctTaps,
                    maxStreak = maxStreak,
                    totalReflexTime = totalReflexTime,
                    effects = gameViewModel.effects
                )
            }
        }
    }

    private fun triggerVibration(type: VibrationType) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            when (type) {
                VibrationType.SHORT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                }
                VibrationType.LONG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
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
