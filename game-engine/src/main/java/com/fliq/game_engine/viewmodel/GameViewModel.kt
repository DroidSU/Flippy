package com.fliq.game_engine.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AchievementManager
import com.fliq.common.AdManager
import com.fliq.common.AnalyticsRepository
import com.fliq.common.Badge
import com.fliq.common.NetworkRepository
import com.fliq.common.repository.ProfileRepository
import com.fliq.core.models.UserData
import com.fliq.database.MatchHistory
import com.fliq.database.repository.BadgeRepository
import com.fliq.database.repository.MatchRepository
import com.fliq.game_engine.models.CardType
import com.fliq.game_engine.models.Challenge
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameEffect.Particle
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.ParticleType
import com.fliq.game_engine.models.Tile
import com.fliq.game_engine.models.VibrationType
import com.fliq.game_engine.repository.GamePreferencesRepository
import com.fliq.game_engine.repository.SoundRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
    private val badgeRepository: BadgeRepository,
    private val networkRepository: NetworkRepository,
    private val preferencesRepository: GamePreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val adManager: AdManager,
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

    private val _selectedChallenge = MutableStateFlow(Challenge.SPEED_RUN)
    val selectedChallenge = _selectedChallenge.asStateFlow()

    private val _gameTime = MutableStateFlow(0L)
    val gameTime = _gameTime.asStateFlow()

    private val _topThreeScores = MutableStateFlow<List<MatchHistory>>(emptyList())
    val topThreeScores = _topThreeScores.asStateFlow()

    private val _showRules = MutableStateFlow(value = false)
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

    private val _newlyUnlockedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val newlyUnlockedBadges = _newlyUnlockedBadges.asStateFlow()

    private val _effects = MutableSharedFlow<GameEffect>()
    val effects = _effects.asSharedFlow()

    private var _currentUsername = ""
    private var _currentAvatarId = 1

    private val _totalTaps = MutableStateFlow(0)
    val totalTaps = _totalTaps.asStateFlow()

    private val _correctTaps = MutableStateFlow(0)
    val correctTaps = _correctTaps.asStateFlow()

    private var timerJob: Job? = null
    private var coinsMissedConsecutively = 0
    private var lastStartTime = 0L
    private var accumulatedTime = 0L
    private var totalReflexTime = 0L
    private var bestReactionTime = Long.MAX_VALUE
    private var clutchTime = 0L
    private var clutchStartTime = 0L
    private var perfectStreak = 0
    private val progressionInterval = 15000L // Scale every 10 seconds

    private val visibleDurationRange: LongRange
        get() {
            val tiers = _gameTime.value / progressionInterval
            val min = maxOf(300L, 600L - tiers * 50L)
            val max = maxOf(400L, 800L - tiers * 50L)
            return min..max
        }

    private val pauseDuration: Long = 800L

    private val spawnIntervalRange: LongRange
        get() {
            val tiers = _gameTime.value / progressionInterval
            val min = maxOf(300L, 1200L - tiers * 45L)
            val max = maxOf(500L, 2000L - tiers * 75L)
            return min..max
        }

    init {
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

    fun setChallenge(challenge: Challenge) {
        if (_status.value == GameStatus.READY) {
            _selectedChallenge.value = challenge
        }
    }

    fun startGame() {
        _score.value = 0
        _lives.value = if (_selectedChallenge.value == Challenge.MINEFIELD) 1 else 3
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
        bestReactionTime = Long.MAX_VALUE
        clutchTime = 0L
        clutchStartTime = 0L
        perfectStreak = 0
        _isAdRewardAvailable.value = true
        _showAdRewardDialog.value = false
        _newlyUnlockedBadges.value = emptyList()
        adManager.loadRewardedAd()

        soundRepository.startBackgroundMusic()

        analyticsRepository.logEvent("game_started", mapOf("mode" to "challenge"))
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
        bestReactionTime = Long.MAX_VALUE
        clutchTime = 0L
        clutchStartTime = 0L
        perfectStreak = 0
        _isAdRewardAvailable.value = true
        _showAdRewardDialog.value = false
        _newlyUnlockedBadges.value = emptyList()
        adManager.loadRewardedAd()
        soundRepository.startBackgroundMusic()
    }

    private fun startTimer() {
        timerJob?.cancel()
        lastStartTime = System.currentTimeMillis()
        timerJob = scope.launch {
            while (_status.value == GameStatus.PLAYING) {
                if (!_isGamePaused.value) {
                    val currentTime = accumulatedTime + (System.currentTimeMillis() - lastStartTime)
                    _gameTime.value = currentTime
                    
                    // Progression logic: haptic feedback on speed up
                    val oldTiers = (_gameTime.value - 100L) / progressionInterval
                    val currentTiers = currentTime / progressionInterval
                    if (currentTiers > oldTiers && currentTiers > 0) {
                        // Vibration logic removed as per user request
                    }
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
                delay(100L)
                continue
            }

            val randomInterval = Random.nextLong(spawnIntervalRange.first, spawnIntervalRange.last + 1)
            delay(randomInterval)

            if (_status.value != GameStatus.PLAYING || _isGamePaused.value) continue

            val hiddenTiles = _tiles.value.filter { !it.isRevealed }
            if (hiddenTiles.isNotEmpty()) {
                val challenge = _selectedChallenge.value
                val tileToReveal = hiddenTiles.random()
                
                if (challenge == Challenge.FRENZY) {
                    revealAndHideTile(tileToReveal.id)
                } else if (challenge == Challenge.MINEFIELD) {
                    revealAndHideTile(tileToReveal.id)
                } else if (challenge == Challenge.SPEED_RUN) {
                    // This logic is now handled in SpeedRun module, 
                    // but we keep a basic version for backward compatibility if needed, 
                    // or just let it fall through.
                    revealAndHideTile(tileToReveal.id)
                } else {
                    revealAndHideTile(tileToReveal.id)
                }
            }
        }
    }

    private fun revealAndHideTile(tileId: Int) {
        scope.launch {
            val challenge = _selectedChallenge.value
            val currentVisibleDuration = Random.nextLong(visibleDurationRange.first, visibleDurationRange.last + 1)
            
            val newType = when (challenge) {
                Challenge.FRENZY -> CardType.COIN
                Challenge.MINEFIELD -> if (Random.nextFloat() > 0.5f) CardType.COIN else CardType.BOMB
                else -> if (Random.nextFloat() > 0.3f) CardType.COIN else CardType.BOMB
            }

            val revealTime = System.currentTimeMillis()
            updateTile(tileId) {
                it.copy(
                    isRevealed = true,
                    isIconVisible = true,
                    type = newType,
                    lastRevealTime = revealTime,
                    currentDuration = currentVisibleDuration
                )
            }

            var elapsed = 0L
            while (elapsed < currentVisibleDuration) {
                if (!_isGamePaused.value) {
                    elapsed += 50L
                    
                    // MIRAGE logic: transform Coin to Bomb at 80% duration
                    if (challenge == Challenge.MIRAGE && 
                        newType == CardType.COIN && 
                        elapsed >= currentVisibleDuration * 0.8f && 
                        elapsed < currentVisibleDuration * 0.8f + 50L) {
                        if (Random.nextFloat() < 0.2f) {
                            updateTile(tileId) { it.copy(type = CardType.BOMB) }
                        }
                    }

                    // BLACKOUT logic: hide icon after 150ms
                    if (challenge == Challenge.BLACKOUT && elapsed >= 150L && elapsed < 200L) {
                        updateTile(tileId) { it.copy(isIconVisible = false) }
                    }
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
        val threshold = if (_selectedChallenge.value == Challenge.FRENZY) 1 else 3

        if (_status.value != GameStatus.PLAYING) return

        coinsMissedConsecutively++
        if (perfectStreak < _streak.value) {
            perfectStreak = _streak.value
        }
        _streak.value = 0
        if (coinsMissedConsecutively >= threshold) {
            _lives.update { (it - 1).coerceAtLeast(0) }
            coinsMissedConsecutively = 0

            if (_lives.value == 1) {
                clutchStartTime = System.currentTimeMillis()
            } else if (_lives.value == 0) {
                if (clutchStartTime > 0) {
                    clutchTime += System.currentTimeMillis() - clutchStartTime
                    clutchStartTime = 0
                }
            }

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
        soundRepository.stopBackgroundMusic()

        // Clear badges immediately as game ends to avoid stale data from previous game
        _newlyUnlockedBadges.value = emptyList()
        if (clutchStartTime > 0) {
            clutchTime += System.currentTimeMillis() - clutchStartTime
            clutchStartTime = 0
        }

        // Finalize perfect streak in case the game ended on a high streak
        if (perfectStreak < _streak.value) {
            perfectStreak = _streak.value
        }

        analyticsRepository.logEvent("game_over", mapOf(
            "score" to _score.value,
            "duration" to _gameTime.value,
            "challenge" to _selectedChallenge.value.name,
            "accuracy" to if (_totalTaps.value > 0) (_correctTaps.value.toDouble() / _totalTaps.value) else 0.0
        ))

        saveMatchResult()
    }

    private fun saveMatchResult() {
        val currentScore = _score.value
        val currentTime = _gameTime.value
        val currentChallengeName = _selectedChallenge.value.name
        val timestamp = System.currentTimeMillis()

        // Capture snapshot of metrics to avoid race conditions if a new game starts immediately
        val currentCorrectTaps = _correctTaps.value
        val currentTotalTaps = _totalTaps.value
        val currentTotalReflexTime = totalReflexTime
        val currentPerfectStreak = perfectStreak
        val currentBestReactionTime = bestReactionTime
        val currentClutchTime = clutchTime

        scope.launch(Dispatchers.IO) {
            val match = MatchHistory(
                id = "${playerId}_$timestamp",
                playerId = playerId,
                score = currentScore,
                difficulty = "LEGACY",
                gameDuration = currentTime,
                timestamp = timestamp,
                correctTaps = currentCorrectTaps,
                totalTaps = currentTotalTaps,
                totalReflexTime = currentTotalReflexTime,
                perfectStreak = currentPerfectStreak,
                isBackedUp = false,
                username = _currentUsername,
                avatarId = _currentAvatarId,
                levelReached = 1,
                challengeName = currentChallengeName
            )
            matchRepository.saveMatch(match)

            // Trigger UI critical logic immediately (Badges and Sound)
            checkAndAwardBadges(match, currentBestReactionTime, currentClutchTime)

            // Perform potentially slow network sync in background
            launch {
                updateUserStats(match)
                try {
                    if (networkRepository.isInternetAvailable()) {
                        networkRepository.storeMatchData(listOf(match))
                    }
                } catch (e: Exception) {
                    Log.e("GameViewModel", "Failed to sync match result: ${e.message}")
                }
            }
        }
    }

    private suspend fun checkAndAwardBadges(
        match: MatchHistory,
        bestReactionTime: Long,
        clutchTime: Long
    ) {
        val userId = playerId
        val allMatches = matchRepository.getMatchHistorySync(userId)
        val userData = profileRepository.getUserDataSync(userId)
        val existingBadges = badgeRepository.getBadgesForUser(userId).firstOrNull() ?: emptyList()
        val existingBadgeIds = existingBadges.asSequence().map { it.badgeId }.toSet()

        val newlyUnlocked = AchievementManager.checkBadges(
            match = match,
            allMatches = allMatches,
            userData = userData,
            bestReactionTime = bestReactionTime,
            clutchTime = clutchTime
        ).filter { it.id !in existingBadgeIds }

        // Update the state with ONLY the badges earned in this specific match
        _newlyUnlockedBadges.value = newlyUnlocked

        if (newlyUnlocked.isNotEmpty()) {
            newlyUnlocked.forEach { badge ->
                badgeRepository.saveBadge(badge.id, userId)
            }
            soundRepository.playBonusSound()
        } else {
            soundRepository.playGameOverSound()
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

        // Grace period: allow tap if it's revealed OR if it just disappeared within 100ms
        val isRecentlyHidden = !tile.isRevealed && (System.currentTimeMillis() - (tile.lastRevealTime + tile.currentDuration)) < 100
        
        if (!tile.isRevealed && !isRecentlyHidden) return

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
                    val reactionTime = System.currentTimeMillis() - tile.lastRevealTime
                    _lastReactionTime.value = reactionTime
                    totalReflexTime += reactionTime
                    bestReactionTime = minOf(bestReactionTime, reactionTime)

                    _effects.emit(GameEffect.ScorePopup(tileId, "+1"))
                    _effects.emit(Particle(tileId, ParticleType.COIN))
                    _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
                }

                CardType.BOMB -> {
                    val oldLives = _lives.value
                    _lives.update { (it - 1).coerceAtLeast(0) }
                    
                    if (_lives.value == 1 && oldLives > 1) {
                        clutchStartTime = System.currentTimeMillis()
                    } else if (_lives.value == 0 && clutchStartTime > 0) {
                        clutchTime += System.currentTimeMillis() - clutchStartTime
                        clutchStartTime = 0
                    }

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
                clutchStartTime = System.currentTimeMillis()
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
