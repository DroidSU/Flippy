package com.sujoy.flippy.game_engine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.game_engine.models.CardType
import com.sujoy.flippy.game_engine.models.Difficulty
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.Tile
import com.sujoy.flippy.game_engine.repository.GamePreferencesRepository
import com.sujoy.flippy.game_engine.repository.MatchRepository
import com.sujoy.flippy.game_engine.repository.SoundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val auth: FirebaseAuth,
    private val soundRepository: SoundRepository,
    private val matchRepository: MatchRepository,
    private val preferencesRepository: GamePreferencesRepository
) : ViewModel() {

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

    private val _isGamePaused = MutableStateFlow(false)
    val isGamePaused = _isGamePaused.asStateFlow()

    private var timerJob: Job? = null
    private var coinsMissedConsecutively = 0
    private var lastStartTime = 0L
    private var accumulatedTime = 0L

    init {
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
        
        startTimer()
        
        viewModelScope.launch {
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
    }

    private fun startTimer() {
        timerJob?.cancel()
        lastStartTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
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
        
        viewModelScope.launch {
            _isGamePaused.value = true
            accumulatedTime += System.currentTimeMillis() - lastStartTime

            if(_difficulty.value == Difficulty.EASY || _difficulty.value == Difficulty.NORMAL)
                soundRepository.pauseBackgroundMusicTemp(1000L)
            else
                soundRepository.pauseBackgroundMusicTemp(500L)
            
            _isGamePaused.value = false
            lastStartTime = System.currentTimeMillis()
        }
    }

    private suspend fun gameLoop() {
        while (_status.value == GameStatus.PLAYING) {
            if (_isGamePaused.value) {
                if(_difficulty.value == Difficulty.EASY || _difficulty.value == Difficulty.NORMAL){
                    delay(300L)
                }
                else{
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
        viewModelScope.launch {
            val newType = if (Random.nextFloat() > 0.3f) CardType.COIN else CardType.BOMB
            updateTile(tileId) {
                it.copy(isRevealed = true, type = newType)
            }
            
            val visibleDuration = when(_difficulty.value) {
                Difficulty.EASY -> 1200L
                Difficulty.NORMAL -> 1000L
                Difficulty.HARD -> 800L
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
        val threshold = when(_difficulty.value) {
            Difficulty.EASY -> 2
            Difficulty.NORMAL -> 2
            Difficulty.HARD -> 3
        }

        if (_status.value != GameStatus.PLAYING) return

        coinsMissedConsecutively++
        if (coinsMissedConsecutively >= threshold) {
            _lives.update { (it - 1).coerceAtLeast(0) }
            coinsMissedConsecutively = 0
            
            if (_lives.value <= 0) {
                endGame()
            } else {
                soundRepository.playBombSound()
                pauseGameTemporarily()
            }
        }
    }

    private fun endGame() {
        _status.value = GameStatus.GAME_OVER
        stopTimer()
        soundRepository.playGameOverSound()
        saveMatchResult()
    }

    private fun saveMatchResult() {
        val currentScore = _score.value
        val currentTime = _gameTime.value
        val currentDifficulty = _difficulty.value.label
        val timestamp = System.currentTimeMillis()
        
        viewModelScope.launch(Dispatchers.IO) {
            val match = MatchHistory(
                id = "${playerId}_$timestamp",
                playerId = playerId,
                score = currentScore,
                difficulty = currentDifficulty,
                gameDuration = currentTime,
                timestamp = timestamp
            )
            matchRepository.saveMatch(match)
        }
    }

    private fun updateTile(tileId: Int, updateAction: (Tile) -> Tile) {
        _tiles.update { currentTiles ->
            currentTiles.map { if (it.id == tileId) updateAction(it) else it }
        }
    }

    fun onTileTapped(tileId: Int) {
        val tile = _tiles.value.find { it.id == tileId } ?: return
        if (!tile.isRevealed || _status.value != GameStatus.PLAYING || _isGamePaused.value) return

        updateTile(tileId) { it.copy(isRevealed = false) }

        when (tile.type) {
            CardType.COIN -> {
                _score.update { it + 1 }
                coinsMissedConsecutively = 0
            }
            CardType.BOMB -> {
                _lives.update { (it - 1).coerceAtLeast(0) }
                if (_lives.value <= 0) {
                    endGame()
                } else {
                    soundRepository.playBombSound()
                    pauseGameTemporarily()
                }
            }
            else -> {}
        }
    }

    fun getTopThreeScores(){
        viewModelScope.launch(Dispatchers.IO) {
            matchRepository.getTopThreeScores(playerId).collect{
                _topThreeScores.value = it
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
