package com.fliq.views

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
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.game_engine.models.Challenge
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.VibrationType
import com.fliq.game_engine.repository.SoundRepository
import com.fliq.game_engine.ui.GameScreen
import com.fliq.game_engine.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    @Inject
    lateinit var soundRepository: SoundRepository
    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val challengeName = intent.getStringExtra(EXTRA_CHALLENGE) ?: Challenge.SPEED_RUN.name
        val challenge = Challenge.valueOf(challengeName)
        gameViewModel.setChallenge(challenge)

        enableEdgeToEdge()

        setContent {
            FliqTheme(settingsRepository) {
                val tiles by gameViewModel.tiles.collectAsState()
                val score by gameViewModel.score.collectAsState()
                val lives by gameViewModel.lives.collectAsState()
                val status by gameViewModel.status.collectAsState()
                val selectedChallenge by gameViewModel.selectedChallenge.collectAsState()
                val gameTime by gameViewModel.gameTime.collectAsState()
                val topThreeScores by gameViewModel.topThreeScores.collectAsState()
                val showRules by gameViewModel.showRules.collectAsState()
                val showAdRewardDialog by gameViewModel.showAdRewardDialog.collectAsState()
                val isPaused by gameViewModel.isGamePaused.collectAsState()
                val streak by gameViewModel.streak.collectAsState()
                val reactionTime by gameViewModel.lastReactionTime.collectAsState()
                val newlyUnlockedBadges by gameViewModel.newlyUnlockedBadges.collectAsState()
                val totalTaps by gameViewModel.totalTaps.collectAsState()
                val correctTaps by gameViewModel.correctTaps.collectAsState()

                val accuracy = if (totalTaps > 0) correctTaps.toFloat() / totalTaps else 0f

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
                    currentChallenge = selectedChallenge,
                    gameTime = gameTime,
                    leaderboard = topThreeScores,
                    showRules = showRules,
                    showAdRewardDialog = showAdRewardDialog,
                    onTileTapped = gameViewModel::onTileTapped,
                    onPlayClick = gameViewModel::startGame,
                    onResetGame = gameViewModel::resetGame,
                    onChallengeChange = gameViewModel::setChallenge,
                    onRulesDismissed = gameViewModel::onRulesDismissed,
                    onWatchAdClick = { gameViewModel.onWatchAdClicked(this@GameActivity) },
                    onSkipAdClick = gameViewModel::onSkipAdClicked,
                    onHelpClick = gameViewModel::showRulesDialog,
                    onSignOutClick = {
                        gameViewModel.signOut()
                        val intent = Intent(this@GameActivity, AuthenticationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                    isPaused = isPaused,
                    onProfileIntentClicked = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    },
                    onLeaderboardIntentClicked = {
                        startActivity(Intent(this, LeaderboardActivity::class.java))
                    },
                    onAchievementsIntentClicked = {
                        startActivity(Intent(this, AchievementsActivity::class.java))
                    },
                    onPreferencesIntentClicked = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    streak = streak,
                    reactionTime = reactionTime,
                    accuracy = accuracy,
                    newBadges = newlyUnlockedBadges,
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
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                VibrationType.LONG -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundRepository.setMusicAllowed(true)
        soundRepository.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        soundRepository.setMusicAllowed(false)
        soundRepository.pauseBackgroundMusic()
    }

    companion object {
        const val EXTRA_CHALLENGE = "extra_challenge"
    }
}
