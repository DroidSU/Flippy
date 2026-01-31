package com.sujoy.flippy.game_engine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.game_engine.models.CardType
import com.sujoy.flippy.game_engine.models.Difficulty
import com.sujoy.flippy.game_engine.models.GameStatus
import com.sujoy.flippy.game_engine.models.Tile
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
    private val matchRepository: MatchRepository
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

    private val _leaderBoard = MutableStateFlow<List<MatchHistory>>(emptyList())
    val leaderBoard = _leaderBoard.asStateFlow()

    private var timerJob: Job? = null
    private var coinsMissedConsecutively = 0

    fun setDifficulty(difficulty: Difficulty) {
        if (_status.value == GameStatus.READY) {
            _difficulty.value = difficulty
        }
    }

    fun startGame() {
        _score.value = 0
        _lives.value = 3
        _gameTime.value = 0L
        _tiles.value = List(16) { Tile(it) }
        _status.value = GameStatus.PLAYING
        coinsMissedConsecutively = 0
        soundRepository.startBackgroundMusic()
        
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
        _tiles.value = List(16) { Tile(it) }
        _status.value = GameStatus.READY
        coinsMissedConsecutively = 0
        soundRepository.startBackgroundMusic()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (_status.value == GameStatus.PLAYING) {
                _gameTime.value = System.currentTimeMillis() - startTime
                delay(100L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun gameLoop() {
        while (_status.value == GameStatus.PLAYING) {
            val currentDiff = _difficulty.value
            val randomInterval = Random.nextLong(currentDiff.minInterval, currentDiff.maxInterval)
            delay(randomInterval)
            
            if (_status.value != GameStatus.PLAYING) break
            
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
            
            delay(visibleDuration)
            
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
        if (_status.value != GameStatus.PLAYING) return
        coinsMissedConsecutively++
        if (coinsMissedConsecutively >= 2) {
            _lives.update { (it - 1).coerceAtLeast(0) }
            coinsMissedConsecutively = 0
            
            if (_lives.value <= 0) {
                endGame()
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
                id = $$"$${playerId}_${timestamp}",
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
        if (!tile.isRevealed || _status.value != GameStatus.PLAYING) return

        updateTile(tileId) { it.copy(isRevealed = false) }

        when (tile.type) {
            CardType.COIN -> {
                _score.update { it + 1 }
                coinsMissedConsecutively = 0
            }
            CardType.BOMB -> {
                soundRepository.playBombSound()
                _lives.update { (it - 1).coerceAtLeast(0) }
                if (_lives.value <= 0) {
                    endGame()
                }
            }
            else -> {}
        }
    }
}
