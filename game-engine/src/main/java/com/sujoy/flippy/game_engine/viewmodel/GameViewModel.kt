package com.sujoy.flippy.game_engine.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.common.AdManager
import com.sujoy.flippy.common.AnalyticsRepository
import com.sujoy.flippy.common.Difficulty
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.common.repository.ProfileRepository
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.database.repository.MatchRepository
import com.sujoy.flippy.game_engine.models.CardType
import com.sujoy.flippy.game_engine.models.GameEffect
import com.sujoy.flippy.game_engine.models.GameEffect.Particle
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.ParticleType
import com.sujoy.flippy.game_engine.models.Tile
import com.sujoy.flippy.game_engine.models.VibrationType
import com.sujoy.flippy.game_engine.repository.GamePreferencesRepository
import com.sujoy.flippy.game_engine.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val soundRepository: SoundRepository,
    private val matchRepository: MatchRepository,
    private val networkRepository: NetworkRepository,
    private val preferencesRepository: GamePreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val adManager: AdManager
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("GameViewModel", "Coroutine exception: ${throwable.localizedMessage}", throwable)
    }

    private val scope = viewModelScope + exceptionHandler

    private val playerId: String get() = auth.currentUser?.uid ?: "anonymous"

    private val _tiles = MutableStateFlow(List(16) { Tile(it) })
    val tiles = _tiles.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _lives = MutableStateFlow(3)
    val lives = _lives.asStateFlow()

    private val _status = MutableStateFlow<GameStatus>(GameStatus.READY)
    val status = _status.asStateFlow()

    private val _difficulty = MutableStateFlow(Difficulty.NORMAL)
    val difficulty = _difficulty.asStateFlow()

    private val _gameTime = MutableStateFlow(0L)
    val gameTime = _gameTime.asStateFlow()

    private val _topThreeScores = MutableStateFlow<List<MatchHistory>>(emptyList())
    val topThreeScores = _topThreeScores.asStateFlow()

    private val _showRules = MutableStateFlow(false)
    val showRules = _showRules.asStateFlow()

    private val _showAdRewardDialog = MutableStateFlow(false)
    val showAdRewardDialog = _showAdRewardDialog.asStateFlow()

    private val _isAdRewardAvailable = MutableStateFlow(true)
    val isAdRewardAvailable = _isAdRewardAvailable.asStateFlow()

    private val _isGamePaused = MutableStateFlow(false)
    val isGamePaused = _isGamePaused.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak = _streak.asStateFlow()

    private val _lastReactionTime = MutableStateFlow(0L)
    val lastReactionTime = _lastReactionTime.asStateFlow()

    private val _effects = MutableSharedFlow<GameEffect>()
    val effects = _effects.asSharedFlow()

    private var _currentUsername = ""
    private var _currentAvatarId = 1

    private val _totalTaps = MutableStateFlow(0)
    private val _correctTaps = MutableStateFlow(0)

    private var timerJob: Job? = null
    private var coinsMissedConsecutively = 0
    private var lastStartTime = 0L
    private var accumulatedTime = 0L
    private var tileRevealTime = 0L
    private var totalReflexTime = 0L
    private var perfectStreak = 0
    private var visibleDuration = 500L

    init {
        visibleDuration = when (_difficulty.value) {
            Difficulty.EASY -> 700L
            Difficulty.NORMAL -> 500L
            Difficulty.HARD -> 250L
        }

        getUserData()
        getTopThreeScores()
        checkRulesVisibility()
    }

    private fun checkRulesVisibility() {
        val showOnStartup = preferencesRepository.shouldShowRulesOnStartup()
        val shownOnce = preferencesRepository.hasShownRulesOnce()

        if (showOnStartup || !shownOnce) {
            _showRules.value = true
        }
    }

    fun showRulesDialog() {
        _showRules.value = true
    }

    fun onRulesDismissed(showOnStartup: Boolean) {
        _showRules.value = false
        preferencesRepository.setShowRulesOnStartup(showOnStartup)
        preferencesRepository.setRulesShownOnce(true)
    }

    fun setDifficulty(difficulty: Difficulty) {
        if (_status.value == GameStatus.READY) {
            _difficulty.value = difficulty

            visibleDuration = when (_difficulty.value) {
                Difficulty.EASY -> 1200L
                Difficulty.NORMAL -> 1000L
                Difficulty.HARD -> 800L
            }
        }
    }

    fun startGame() {
        _score.value = 0
        _lives.value = 3
        _gameTime.value = 0L
        accumulatedTime = 0L
        _tiles.value = List(16) { Tile(it) }
        _status.value = GameStatus.PLAYING
        coinsMissedConsecutively = 0
        _isGamePaused.value = false
        _streak.value = 0
        _lastReactionTime.value = 0L
        _totalTaps.value = 0
        _correctTaps.value = 0
        totalReflexTime = 0L
        perfectStreak = 0
        _isAdRewardAvailable.value = true
        _showAdRewardDialog.value = false
        adManager.loadRewardedAd()

        soundRepository.startBackgroundMusic()

        analyticsRepository.logEvent("game_started", mapOf("difficulty" to _difficulty.value.label))
        analyticsRepository.logScreenView("GameScreen")

        startTimer()

        scope.launch {
            gameLoop()
        }
    }

    fun resetGame() {
        stopTimer()
        _score.value = 0
        _lives.value = 3
        _gameTime.value = 0L
        accumulatedTime = 0L
        _tiles.value = List(16) { Tile(it) }
        _status.value = GameStatus.READY
        coinsMissedConsecutively = 0
        _isGamePaused.value = false
        _streak.value = 0
        _lastReactionTime.value = 0L
        _totalTaps.value = 0
        _correctTaps.value = 0
        totalReflexTime = 0L
        perfectStreak = 0
        _isAdRewardAvailable.value = true
        _showAdRewardDialog.value = false
        adManager.loadRewardedAd()
        soundRepository.startBackgroundMusic()
    }

    private fun startTimer() {
        timerJob?.cancel()
        lastStartTime = System.currentTimeMillis()
        timerJob = scope.launch {
            while (_status.value == GameStatus.PLAYING) {
                if (!_isGamePaused.value) {
                    _gameTime.value = accumulatedTime + (System.currentTimeMillis() - lastStartTime)
                }
                delay(100L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun pauseGameTemporarily() {
        if (_status.value != GameStatus.PLAYING || _isGamePaused.value) return

        val pauseDuration = if (_difficulty.value == Difficulty.EASY || _difficulty.value == Difficulty.NORMAL) 1000L else 500L

        _isGamePaused.value = true
        soundRepository.playBombSound()
        accumulatedTime += System.currentTimeMillis() - lastStartTime

        scope.launch {
            soundRepository.pauseBackgroundMusic()
            // Explicit delay ensures the pause lasts the intended duration even if sound is disabled
            delay(pauseDuration)

            if (_status.value == GameStatus.PLAYING) {
                soundRepository.startBackgroundMusic()
            }
            
            _isGamePaused.value = false
            lastStartTime = System.currentTimeMillis()
        }
    }

    private suspend fun gameLoop() {
        while (_status.value == GameStatus.PLAYING) {
            if (_isGamePaused.value) {
                if (_difficulty.value == Difficulty.EASY || _difficulty.value == Difficulty.NORMAL) {
                    delay(300L)
                } else {
                    delay(800L)
                }
                continue
            }

            val currentDiff = _difficulty.value
            val randomInterval = Random.nextLong(currentDiff.minInterval, currentDiff.maxInterval)
            delay(randomInterval)

            if (_status.value != GameStatus.PLAYING || _isGamePaused.value) continue

            val hiddenTiles = _tiles.value.filter { !it.isRevealed }
            if (hiddenTiles.isNotEmpty()) {
                val tileToReveal = hiddenTiles.random()
                revealAndHideTile(tileToReveal.id)
            }
        }
    }

    private fun revealAndHideTile(tileId: Int) {
        scope.launch {
            val newType = if (Random.nextFloat() > 0.3f) CardType.COIN else CardType.BOMB
            tileRevealTime = System.currentTimeMillis()
            updateTile(tileId) {
                it.copy(isRevealed = true, type = newType)
            }

            var elapsed = 0L
            while (elapsed < visibleDuration) {
                if (!_isGamePaused.value) {
                    elapsed += 50L
                }
                delay(50L)
                if (_status.value != GameStatus.PLAYING) return@launch
            }

            val tile = _tiles.value.find { it.id == tileId }
            if (tile?.isRevealed == true) {
                if (tile.type == CardType.COIN) {
                    handleMissedCoin()
                }

                updateTile(tileId) {
                    it.copy(isRevealed = false)
                }
            }
        }
    }

    private fun handleMissedCoin() {
        val threshold = when (_difficulty.value) {
            Difficulty.EASY -> 2
            Difficulty.NORMAL -> 2
            Difficulty.HARD -> 3
        }

        if (_status.value != GameStatus.PLAYING) return

        coinsMissedConsecutively++
        if (perfectStreak < _streak.value) {
            perfectStreak = _streak.value
        }
        _streak.value = 0
        if (coinsMissedConsecutively >= threshold) {
            _lives.update { (it - 1).coerceAtLeast(0) }
            coinsMissedConsecutively = 0

            if (_lives.value <= 0) {
                if (_isAdRewardAvailable.value && adManager.isAdLoaded()) {
                    pauseForAdReward()
                } else {
                    endGame()
                }
            } else {
                pauseGameTemporarily()
            }
        }
    }

    private fun pauseForAdReward() {
        _isGamePaused.value = true
        _showAdRewardDialog.value = true
        accumulatedTime += System.currentTimeMillis() - lastStartTime
        soundRepository.pauseBackgroundMusic()
    }

    private fun endGame() {
        _status.value = GameStatus.GAME_OVER
        _isGamePaused.value = false
        stopTimer()
        soundRepository.playGameOverSound()

        // Finalize perfect streak in case the game ended on a high streak
        if (perfectStreak < _streak.value) {
            perfectStreak = _streak.value
        }

        analyticsRepository.logEvent("game_over", mapOf(
            "score" to _score.value,
            "duration" to _gameTime.value,
            "difficulty" to _difficulty.value.label,
            "accuracy" to if (_totalTaps.value > 0) (_correctTaps.value.toDouble() / _totalTaps.value) else 0.0
        ))

        saveMatchResult()
    }

    private fun saveMatchResult() {
        val currentScore = _score.value
        val currentTime = _gameTime.value
        val currentDifficulty = _difficulty.value.label
        val timestamp = System.currentTimeMillis()

        scope.launch(Dispatchers.IO) {
            val match = MatchHistory(
                id = "${playerId}_$timestamp",
                playerId = playerId,
                score = currentScore,
                difficulty = currentDifficulty,
                gameDuration = currentTime,
                timestamp = timestamp,
                correctTaps = _correctTaps.value,
                totalTaps = _totalTaps.value,
                totalReflexTime = totalReflexTime,
                perfectStreak = perfectStreak,
                isBackedUp = false,
                username = _currentUsername,
                avatarId = _currentAvatarId
            )
            matchRepository.saveMatch(match)

            try {
                if (networkRepository.isInternetAvailable()) {
                    networkRepository.storeMatchData(listOf(match))
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Failed to sync match result: ${e.message}")
            }

            updateUserStats(match)
        }
    }

    private suspend fun updateUserStats(match: MatchHistory) {
        val currentStats = profileRepository.getUserDataSync(playerId) ?: UserData(
            userId = playerId,
            username = _currentUsername,
            avatarId = _currentAvatarId
        )

        val updatedStats = currentStats.copy(
            totalMatches = currentStats.totalMatches + 1,
            highestScore = maxOf(currentStats.highestScore, match.score),
            longestRound = maxOf(currentStats.longestRound, match.gameDuration),
            totalCorrectTaps = currentStats.totalCorrectTaps + match.correctTaps,
            totalTaps = currentStats.totalTaps + match.totalTaps,
            totalReflexTime = currentStats.totalReflexTime + match.totalReflexTime,
            bestPerfectStreak = maxOf(currentStats.bestPerfectStreak, match.perfectStreak)
        )

        profileRepository.saveUserData(updatedStats)

        if (networkRepository.isInternetAvailable()) {
            networkRepository.uploadUserData(updatedStats)
        }
    }

    private fun updateTile(tileId: Int, updateAction: (Tile) -> Tile) {
        _tiles.update { currentTiles ->
            currentTiles.map { if (it.id == tileId) updateAction(it) else it }
        }
    }

    fun onTileTapped(tileId: Int, tapPosition: Offset? = null) {
        val tile = _tiles.value.find { it.id == tileId } ?: return
        if (_status.value != GameStatus.PLAYING || _isGamePaused.value) return

        // Every tap on a tile area during gameplay counts towards total taps (accuracy)
        _totalTaps.update { it + 1 }

        if (!tile.isRevealed) return

        updateTile(tileId) { it.copy(isRevealed = false) }

        scope.launch {
            tapPosition?.let {
                _effects.emit(GameEffect.BackgroundRipple(it))
            }
            
            when (tile.type) {
                CardType.COIN -> {
                    _score.update { it + 1 }
                    coinsMissedConsecutively = 0
                    _correctTaps.update { it + 1 }
                    _streak.update { it + 1 }
                    val reactionTime = System.currentTimeMillis() - tileRevealTime
                    _lastReactionTime.value = reactionTime
                    totalReflexTime += reactionTime

                    _effects.emit(GameEffect.ScorePopup(tileId, "+1"))
                    _effects.emit(Particle(tileId, ParticleType.COIN))
                    _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
                }

                CardType.BOMB -> {
                    _lives.update { (it - 1).coerceAtLeast(0) }
                    
                    analyticsRepository.logEvent("bomb_hit", mapOf(
                        "score_at_hit" to _score.value,
                        "lives_remaining" to _lives.value
                    ))

                    if (perfectStreak < _streak.value)
                        perfectStreak = _streak.value

                    _streak.value = 0

                    _effects.emit(Particle(tileId, ParticleType.BOMB))
                    _effects.emit(GameEffect.Vibration(VibrationType.LONG))

                    if (_lives.value <= 0) {
                        if (_isAdRewardAvailable.value && adManager.isAdLoaded()) {
                            pauseForAdReward()
                        } else {
                            endGame()
                        }
                    } else {
                        pauseGameTemporarily()
                    }
                }

                else -> {}
            }
        }
    }

    fun getTopThreeScores() {
        scope.launch(Dispatchers.IO) {
            matchRepository.getTopThreeScores(playerId).collect {
                _topThreeScores.value = it
            }
        }
    }

    fun onWatchAdClicked(activity: android.app.Activity) {
        _showAdRewardDialog.value = false
        var rewardEarned = false

        adManager.showRewardedAd(
            activity = activity,
            onRewardEarned = {
                rewardEarned = true
                _lives.value = 1
                _isAdRewardAvailable.value = false
                analyticsRepository.logEvent("rewarded_ad_watched", mapOf("score" to _score.value))
            },
            onAdClosed = {
                if (rewardEarned) {
                    _isGamePaused.value = false
                    lastStartTime = System.currentTimeMillis()
                    soundRepository.startBackgroundMusic()
                } else {
                    endGame()
                }
            }
        )
    }

    fun onSkipAdClicked() {
        _showAdRewardDialog.value = false
        _isAdRewardAvailable.value = false
        endGame()
    }

    fun signOut() {
        scope.launch {
            profileRepository.clearLocalData()
            auth.signOut()
        }
    }

    private fun getUserData() {
        _currentUsername = profileRepository.getUsername()
        _currentAvatarId = profileRepository.getAvatarId()
    }
}
