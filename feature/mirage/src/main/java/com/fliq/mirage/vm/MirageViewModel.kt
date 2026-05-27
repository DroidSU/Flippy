package com.fliq.mirage.vm

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
import com.fliq.core.settings.SettingsRepository
import com.fliq.database.MatchHistory
import com.fliq.database.repository.BadgeRepository
import com.fliq.database.repository.MatchRepository
import com.fliq.game_engine.models.CardType
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameEffect.Particle
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.ParticleType
import com.fliq.game_engine.models.Tile
import com.fliq.game_engine.models.VibrationType
import com.fliq.game_engine.repository.GamePreferencesRepository
import com.fliq.game_engine.repository.SoundRepository
import com.fliq.mirage.models.MirageTutorialStep
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
class MirageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val soundRepository: SoundRepository,
    private val matchRepository: MatchRepository,
    private val badgeRepository: BadgeRepository,
    private val networkRepository: NetworkRepository,
    private val preferencesRepository: GamePreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val adManager: AdManager,
    private val difficultyManager: com.fliq.common.DifficultyManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("MirageViewModel", "Coroutine exception: ${throwable.localizedMessage}", throwable)
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

    private val _gameTime = MutableStateFlow(0L)
    val gameTime = _gameTime.asStateFlow()

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

    private val _showRules = MutableStateFlow(false)
    val showRules = _showRules.asStateFlow()

    private val _effects = MutableSharedFlow<GameEffect>()
    val effects = _effects.asSharedFlow()

    private var _currentUsername = ""
    private var _currentAvatarId = 1

    private val _totalTaps = MutableStateFlow(0)
    val totalTaps = _totalTaps.asStateFlow()

    private val _correctTaps = MutableStateFlow(0)
    val correctTaps = _correctTaps.asStateFlow()

    private val _tutorialStep = MutableStateFlow<MirageTutorialStep?>(null)
    val tutorialStep = _tutorialStep.asStateFlow()

    private val _showRotationPrompt = MutableStateFlow(false)
    val showRotationPrompt = _showRotationPrompt.asStateFlow()

    private val tutorialTileId = 5

    private var timerJob: Job? = null
    private var gameLoopJob: Job? = null
    private var coinsMissedConsecutively = 0
    private var lastStartTime = 0L
    private var accumulatedTime = 0L
    private var totalReflexTime = 0L
    private var bestReactionTime = Long.MAX_VALUE
    private var clutchTime = 0L
    private var clutchStartTime = 0L
    private var perfectStreak = 0
    private var _cachedUserData: UserData? = null
    
    private val progressionInterval = 15000L // Slower scaling for Mirage to allow observation

    private val visibleDurationRange: LongRange
        get() = difficultyManager.getVisibleDurationRange(_gameTime.value, progressionInterval)

    private val pauseDuration: Long = 800L

    private val spawnIntervalRange: LongRange
        get() = difficultyManager.getSpawnIntervalRange(_gameTime.value, progressionInterval)

    init {
        getUserData()
        if (!settingsRepository.isMirageTutorialCompleted()) {
            _tutorialStep.value = MirageTutorialStep.WELCOME
        } else {
            checkRotationPromptVisibility()
        }
        checkRulesVisibility()
    }

    companion object {
        private var hasShownRotationPromptInSession = false
    }

    private fun checkRotationPromptVisibility() {
        if (!settingsRepository.isMirageRotationHintDisabled() && !hasShownRotationPromptInSession) {
            _showRotationPrompt.value = true
            hasShownRotationPromptInSession = true
        }
    }

    private fun getUserData() {
        _currentUsername = profileRepository.getUsername()
        _currentAvatarId = profileRepository.getAvatarId()
        scope.launch {
            _cachedUserData = profileRepository.getUserDataSync(playerId)
        }
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

    fun nextTutorialStep() {
        val current = _tutorialStep.value ?: return

        when (current) {
            MirageTutorialStep.TILE_INTRO -> {
                updateTile(tutorialTileId) {
                    it.copy(
                        isRevealed = true,
                        isIconVisible = true,
                        type = CardType.COIN,
                        lastRevealTime = System.currentTimeMillis(),
                        currentDuration = 999999L
                    )
                }
            }
            else -> {}
        }

        _tutorialStep.value = when (current) {
            MirageTutorialStep.WELCOME -> MirageTutorialStep.STATS
            MirageTutorialStep.STATS -> MirageTutorialStep.TILE_INTRO
            MirageTutorialStep.TILE_INTRO -> MirageTutorialStep.TILE_INTERACT
            MirageTutorialStep.TILE_INTERACT -> MirageTutorialStep.START_GAME
            MirageTutorialStep.START_GAME -> {
                settingsRepository.setMirageTutorialCompleted(true)
                _tiles.value = List(16) { Tile(it) }
                checkRotationPromptVisibility()
                null
            }
        }
    }

    fun skipTutorial() {
        settingsRepository.setMirageTutorialCompleted(true)
        _tutorialStep.value = null
        _tiles.value = List(16) { Tile(it) }
        checkRotationPromptVisibility()
    }

    fun onRotationPromptDismissed(dontShowAgain: Boolean) {
        _showRotationPrompt.value = false
        if (dontShowAgain) {
            settingsRepository.setMirageRotationHintDisabled(true)
        }
    }

    fun startGame() {
        stopTimer()
        gameLoopJob?.cancel()

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
        bestReactionTime = Long.MAX_VALUE
        clutchTime = 0L
        clutchStartTime = 0L
        perfectStreak = 0
        _isAdRewardAvailable.value = true
        _showAdRewardDialog.value = false
        _newlyUnlockedBadges.value = emptyList()
        adManager.loadRewardedAd()

        soundRepository.startBackgroundMusic()
        analyticsRepository.logEvent("game_started", mapOf("challenge" to "MIRAGE"))
        startTimer()

        gameLoopJob = scope.launch {
            gameLoop()
        }
    }

    fun resetGame() {
        stopTimer()
        gameLoopJob?.cancel()
        _status.value = GameStatus.READY
        _gameTime.value = 0L
        accumulatedTime = 0L
        _tiles.value = List(16) { Tile(it) }
        _lives.value = 3
        _score.value = 0
        _streak.value = 0
        _totalTaps.value = 0
        _correctTaps.value = 0
        totalReflexTime = 0L
        perfectStreak = 0
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
        soundRepository.playBombSound()
        _isGamePaused.value = true
        accumulatedTime += System.currentTimeMillis() - lastStartTime
        scope.launch {
            soundRepository.pauseBackgroundMusic()
            delay(pauseDuration)
            if (_status.value == GameStatus.PLAYING) soundRepository.startBackgroundMusic()
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
                revealAndHideTile(hiddenTiles.random().id)
            }
        }
    }

    private fun revealAndHideTile(tileId: Int) {
        val parentJob = gameLoopJob ?: return
        scope.launch(parentJob) {
            val currentVisibleDuration = Random.nextLong(visibleDurationRange.first, visibleDurationRange.last + 1)
            val newType = CardType.COIN // Mirage starts with Coin

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
            val willTransform = Random.nextFloat() < 0.25f
            var transformed = false
            // Transform at 60% of the duration so it's always consistent regardless of speed
            val transformThreshold = (currentVisibleDuration * 0.6f).toLong()

            while (elapsed < currentVisibleDuration) {
                if (!_isGamePaused.value) {
                    elapsed += 50L
                    
                    // MIRAGE logic: transform if decided and threshold reached
                    if (willTransform && !transformed && elapsed >= transformThreshold) {
                        transformed = true
                        updateTile(tileId) { it.copy(type = CardType.BOMB) }
                        _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
                    }
                }
                delay(50L)
                if (_status.value != GameStatus.PLAYING) return@launch
            }

            val tile = _tiles.value.find { it.id == tileId }
            if (tile?.isRevealed == true && tile.lastRevealTime == revealTime) {
                if (tile.type == CardType.COIN) {
                    handleMissedCoin()
                }
                updateTile(tileId) { it.copy(isRevealed = false) }
            }
        }
    }

    private fun handleMissedCoin() {
        val threshold = 3
        if (_status.value != GameStatus.PLAYING) return
        coinsMissedConsecutively++
        if (perfectStreak < _streak.value) perfectStreak = _streak.value
        _streak.value = 0
        if (coinsMissedConsecutively >= threshold) {
            _lives.update { (it - 1).coerceAtLeast(0) }
            coinsMissedConsecutively = 0
            if (_lives.value == 1) clutchStartTime = System.currentTimeMillis()
            if (_lives.value <= 0) {
                if (_isAdRewardAvailable.value && adManager.isAdLoaded()) pauseForAdReward()
                else endGame()
            } else pauseGameTemporarily()
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
        _newlyUnlockedBadges.value = emptyList()
        if (clutchStartTime > 0) {
            clutchTime += System.currentTimeMillis() - clutchStartTime
            clutchStartTime = 0
        }
        if (perfectStreak < _streak.value) perfectStreak = _streak.value
        
        analyticsRepository.logEvent("game_over", mapOf(
            "score" to _score.value,
            "duration" to _gameTime.value,
            "challenge" to "MIRAGE",
            "accuracy" to if (_totalTaps.value > 0) (_correctTaps.value.toDouble() / _totalTaps.value) else 0.0
        ))
        saveMatchResult()
    }

    private fun saveMatchResult() {
        val currentScore = _score.value
        val currentTime = _gameTime.value
        val timestamp = System.currentTimeMillis()
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
                challengeName = "MIRAGE"
            )
            matchRepository.saveMatch(match)

            // Trigger UI critical logic immediately (Badges and Sound)
            checkAndAwardBadges(match, currentBestReactionTime, currentClutchTime)

            // Perform potentially slow network sync in background
            launch {
                updateUserStats(match)
                try {
                    if (networkRepository.isInternetAvailable()) networkRepository.storeMatchData(listOf(match))
                } catch (e: Exception) { Log.e("MirageViewModel", "Failed to sync: ${e.message}") }
            }
        }
    }

    private suspend fun checkAndAwardBadges(match: MatchHistory, bestReactionTime: Long, clutchTime: Long) {
        val userId = playerId
        val allMatches = matchRepository.getMatchHistorySync(userId)
        val userData = profileRepository.getUserDataSync(userId)
        val existingBadges = badgeRepository.getBadgesForUser(userId).firstOrNull() ?: emptyList()
        val existingBadgeIds = existingBadges.map { it.badgeId }.toSet()
        val newlyUnlocked = AchievementManager.checkBadges(match, allMatches, userData, bestReactionTime, clutchTime).filter { it.id !in existingBadgeIds }
        
        _newlyUnlockedBadges.value = newlyUnlocked
        
        if (newlyUnlocked.isNotEmpty()) {
            newlyUnlocked.forEach { badgeRepository.saveBadge(it.id, userId) }
            
            // Sync with UserData's badges list as well
            userData?.let {
                val updatedBadges = it.badges.toMutableList()
                newlyUnlocked.forEach { badge -> updatedBadges.add(badge.id) }
                val updatedUserData = it.copy(badges = updatedBadges)
                profileRepository.saveUserData(updatedUserData)
                if (networkRepository.isInternetAvailable()) {
                    networkRepository.uploadUserData(updatedUserData)
                }
            }
            soundRepository.playBonusSound()
        } else {
            soundRepository.playGameOverSound()
        }
    }

    private suspend fun updateUserStats(match: MatchHistory) {
        val currentStats = profileRepository.getUserDataSync(playerId) ?: UserData(userId = playerId, username = _currentUsername, avatarId = _currentAvatarId)
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
        if (networkRepository.isInternetAvailable()) networkRepository.uploadUserData(updatedStats)
    }

    private fun updateTile(tileId: Int, updateAction: (Tile) -> Tile) {
        _tiles.update { currentTiles -> currentTiles.map { if (it.id == tileId) updateAction(it) else it } }
    }

    fun onTileTapped(tileId: Int, tapPosition: Offset? = null) {
        if (_tutorialStep.value == MirageTutorialStep.TILE_INTERACT) {
            if (tileId == tutorialTileId) {
                scope.launch {
                    tapPosition?.let { _effects.emit(GameEffect.BackgroundRipple(it)) }
                    _effects.emit(GameEffect.ScorePopup(tileId, "+1"))
                    _effects.emit(Particle(tileId, ParticleType.COIN))
                    _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
                    soundRepository.playCoinTapSound()
                    updateTile(tileId) { it.copy(isRevealed = false) }
                    nextTutorialStep()
                }
            }
            return
        }

        val tile = _tiles.value.find { it.id == tileId } ?: return
        if (_status.value != GameStatus.PLAYING || _isGamePaused.value) return
        _totalTaps.update { it + 1 }
        val isRecentlyHidden = !tile.isRevealed && (System.currentTimeMillis() - (tile.lastRevealTime + tile.currentDuration)) < 100
        if (!tile.isRevealed && !isRecentlyHidden) return
        updateTile(tileId) { it.copy(isRevealed = false) }
        scope.launch {
            tapPosition?.let { _effects.emit(GameEffect.BackgroundRipple(it)) }
            when (tile.type) {
                CardType.COIN -> {
                    _score.update { it + 1 }
                    coinsMissedConsecutively = 0
                    _correctTaps.update { it + 1 }
                    _streak.update { it + 1 }
                    
                    val rawReactionTime = System.currentTimeMillis() - tile.lastRevealTime
                    val offset = _cachedUserData?.latencyOffset ?: 0L
                    val reactionTime = (rawReactionTime - offset).coerceAtLeast(10L)

                    _lastReactionTime.value = reactionTime
                    totalReflexTime += reactionTime
                    bestReactionTime = minOf(bestReactionTime, reactionTime)
                    _effects.emit(GameEffect.ScorePopup(tileId, "+1"))
                    _effects.emit(Particle(tileId, ParticleType.COIN))
                    _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
                    soundRepository.playCoinTapSound()
                }
                CardType.BOMB -> {
                    val oldLives = _lives.value
                    _lives.update { (it - 1).coerceAtLeast(0) }
                    if (_lives.value == 1 && oldLives > 1) clutchStartTime = System.currentTimeMillis()
                    _streak.value = 0
                    _effects.emit(Particle(tileId, ParticleType.BOMB))
                    _effects.emit(GameEffect.Vibration(VibrationType.LONG))
                    if (_lives.value <= 0) {
                        if (_isAdRewardAvailable.value && adManager.isAdLoaded()) pauseForAdReward()
                        else endGame()
                    } else pauseGameTemporarily()
                }
                else -> {}
            }
        }
    }

    fun onWatchAdClicked(activity: android.app.Activity) {
        _showAdRewardDialog.value = false
        var rewardEarned = false
        adManager.showRewardedAd(activity, onRewardEarned = {
            rewardEarned = true
            _lives.value = 1
            clutchStartTime = System.currentTimeMillis()
            _isAdRewardAvailable.value = false
        }, onAdClosed = {
            if (rewardEarned) {
                _isGamePaused.value = false
                lastStartTime = System.currentTimeMillis()
                soundRepository.startBackgroundMusic()
            } else endGame()
        })
    }

    fun onSkipAdClicked() {
        _showAdRewardDialog.value = false
        _isAdRewardAvailable.value = false
        endGame()
    }

    fun signOut() { scope.launch { profileRepository.clearLocalData(); auth.signOut() } }
}
