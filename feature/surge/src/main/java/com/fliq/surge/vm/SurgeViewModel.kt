package com.fliq.surge.vm

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AnalyticsRepository
import com.fliq.game_engine.models.CardType
import com.fliq.game_engine.models.GameEffect
import com.fliq.game_engine.models.GameStatus
import com.fliq.game_engine.models.ParticleType
import com.fliq.game_engine.models.Tile
import com.fliq.game_engine.models.VibrationType
import com.fliq.game_engine.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
class SurgeViewModel @Inject constructor(
    private val soundRepository: SoundRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SurgeViewModel", "Coroutine exception: ${throwable.localizedMessage}", throwable)
    }

    private val scope = viewModelScope + exceptionHandler

    private val _tiles = MutableStateFlow(List(16) { Tile(it) })
    val tiles = _tiles.asStateFlow()

    private val _reservoirLevel = MutableStateFlow(0.2f)
    val reservoirLevel = _reservoirLevel.asStateFlow()

    private val _status = MutableStateFlow<GameStatus>(GameStatus.READY)
    val status = _status.asStateFlow()

    private val _effects = MutableSharedFlow<GameEffect>()
    val effects = _effects.asSharedFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private var gameLoopJob: Job? = null
    private var drainJob: Job? = null

    // Gameplay Constants
    private val baseGain = 0.04f
    private val bombPenalty = 0.25f
    private val emptyTapPenalty = 0.05f
    private val baseDrainRate = 0.015f // per second
    private val spawnInterval = 700L
    private val tileDuration = 1500L

    fun startGame() {
        _status.value = GameStatus.PLAYING
        _reservoirLevel.value = 0.2f
        _score.value = 0
        _tiles.value = List(16) { Tile(it) }
        
        startGameLoop()
        startDrain()
        
        soundRepository.startBackgroundMusic()
        analyticsRepository.logEvent("game_started", mapOf("challenge" to "SURGE"))
    }

    private fun startDrain() {
        drainJob?.cancel()
        drainJob = scope.launch {
            while (_status.value == GameStatus.PLAYING) {
                delay(100)
                // Drain increases as the reservoir gets fuller (pressure)
                val pressureMultiplier = 1f + (_reservoirLevel.value * 2f)
                val currentDrain = (baseDrainRate * pressureMultiplier) / 10f
                _reservoirLevel.update { (it - currentDrain).coerceAtLeast(0f) }
                
                if (_reservoirLevel.value <= 0f) {
                    endGame(won = false)
                }
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = scope.launch {
            while (_status.value == GameStatus.PLAYING) {
                delay(spawnInterval)
                spawnTile()
            }
        }
    }

    private fun spawnTile() {
        val hiddenTiles = _tiles.value.filter { !it.isRevealed }
        if (hiddenTiles.isEmpty()) return

        val tileToReveal = hiddenTiles.random()
        val isBomb = Random.nextFloat() < 0.25f // 25% chance of bomb
        val type = if (isBomb) CardType.BOMB else CardType.COIN

        updateTile(tileToReveal.id) { 
            it.copy(isRevealed = true, type = type, lastRevealTime = System.currentTimeMillis()) 
        }

        // Auto-hide after duration
        scope.launch {
            delay(tileDuration)
            val currentTile = _tiles.value.find { it.id == tileToReveal.id }
            if (currentTile?.isRevealed == true && currentTile.lastRevealTime == tileToReveal.lastRevealTime) {
                updateTile(tileToReveal.id) { it.copy(isRevealed = false) }
            }
        }
    }

    fun onTileTapped(tileId: Int, tapPosition: Offset? = null) {
        if (_status.value != GameStatus.PLAYING) return

        val tile = _tiles.value.find { it.id == tileId } ?: return

        scope.launch {
            tapPosition?.let { _effects.emit(GameEffect.BackgroundRipple(it)) }

            if (tile.isRevealed) {
                updateTile(tileId) { it.copy(isRevealed = false) }
                
                if (tile.type == CardType.COIN) {
                    handleCoinTap(tileId)
                } else {
                    handleBombTap(tileId)
                }
            } else {
                handleEmptyTap()
            }
        }
    }

    private suspend fun handleCoinTap(tileId: Int) {
        _score.update { it + 1 }
        _reservoirLevel.update { (it + baseGain).coerceAtMost(1f) }
        
        _effects.emit(GameEffect.ScorePopup(tileId, "+FILL"))
        _effects.emit(GameEffect.Particle(tileId, ParticleType.COIN))
        _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
        soundRepository.playCoinTapSound()

        if (_reservoirLevel.value >= 1f) {
            endGame(won = true)
        }
    }

    private suspend fun handleBombTap(tileId: Int) {
        _reservoirLevel.update { (it - bombPenalty).coerceAtLeast(0f) }
        _effects.emit(GameEffect.Particle(tileId, ParticleType.BOMB))
        _effects.emit(GameEffect.Vibration(VibrationType.LONG))
        soundRepository.playBombSound()
        
        if (_reservoirLevel.value <= 0f) {
            endGame(won = false)
        }
    }

    private suspend fun handleEmptyTap() {
        _reservoirLevel.update { (it - emptyTapPenalty).coerceAtLeast(0f) }
        _effects.emit(GameEffect.Vibration(VibrationType.SHORT))
        // Subtle sound or effect for miss
    }

    private fun endGame(won: Boolean) {
        _status.value = GameStatus.GAME_OVER
        gameLoopJob?.cancel()
        drainJob?.cancel()
        soundRepository.stopBackgroundMusic()
        
        if (won) {
            soundRepository.playBonusSound()
        } else {
            soundRepository.playGameOverSound()
        }

        analyticsRepository.logEvent("game_over", mapOf("won" to won, "challenge" to "SURGE"))
    }

    fun resetGame() {
        _status.value = GameStatus.READY
        _reservoirLevel.value = 0.2f
        _tiles.value = List(16) { Tile(it) }
    }

    private fun updateTile(tileId: Int, update: (Tile) -> Tile) {
        _tiles.update { current ->
            current.map { if (it.id == tileId) update(it) else it }
        }
    }
}
