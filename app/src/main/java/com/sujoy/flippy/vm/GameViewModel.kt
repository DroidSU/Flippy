// File: app/src/main/java/com/sujoy/flippy/vm/GameViewModel.kt
package com.sujoy.flippy.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.flippy.models.CardType
import com.sujoy.flippy.models.Tile
import com.sujoy.flippy.utils.GameStatus
import com.sujoy.flippy.utils.SoundPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// Inherit from AndroidViewModel to get access to the Application context
class GameViewModel(
    application: Application,
    private val soundPlayer: SoundPlayer,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    private val _tiles = MutableStateFlow(List(16) { Tile(it) })
    val tiles = _tiles.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _lives = MutableStateFlow(3)
    val lives = _lives.asStateFlow()

    private val _status = MutableStateFlow<GameStatus>(GameStatus.READY)
    val status = _status.asStateFlow()


    fun startGame() {
        _score.value = 0
        _lives.value = 3
        _tiles.value = List(16) { Tile(it) }
        _status.value = GameStatus.PLAYING
        soundPlayer.startBackgroundMusic()
        viewModelScope.launch(dispatcher) {
            gameLoop()
        }
    }

    private suspend fun gameLoop() {
        while (_status.value == GameStatus.PLAYING) {
            delay((500L..1500L).random())
            if (_status.value != GameStatus.PLAYING) {
                break
            }
            val hiddenTiles = _tiles.value.filter { !it.isRevealed }
            if (hiddenTiles.isNotEmpty()) {
                val tileToReveal = hiddenTiles.random()
                revealAndHideTile(tileToReveal.id)
            }
        }
    }


    private fun revealAndHideTile(tileId: Int) {
        viewModelScope.launch(dispatcher) {
            val newType = if (Random.nextFloat() > 0.3f) CardType.COIN else CardType.BOMB
            updateTile(tileId) {
                it.copy(isRevealed = true, type = newType)
            }
            delay(1200)
            if (_tiles.value.find { it.id == tileId }?.isRevealed == true) {
                updateTile(tileId) {
                    it.copy(isRevealed = false)
                }
            }
        }
    }

    private fun updateTile(tileId: Int, updateAction: (Tile) -> Tile) {
        _tiles.update { currentTiles ->
            currentTiles.map { if (it.id == tileId) updateAction(it) else it }
        }
    }

    fun onTileTapped(tileId: Int) {
        val tile = _tiles.value.find { it.id == tileId } ?: return

        if (!tile.isRevealed || _status.value != GameStatus.PLAYING) {
            return
        }

        updateTile(tileId) { it.copy(isRevealed = false) }

        when (tile.type) {
            CardType.COIN -> {
                _score.update { it + 1 }
            }

            CardType.BOMB -> {
                // Play bomb sound
                soundPlayer.playBombSound()
                _lives.update { it - 1 }
                if (_lives.value <= 0) {
                    _status.value = GameStatus.GAME_OVER
                    // Play game over sound
                    soundPlayer.playGameOverSound()
                }
            }
            CardType.HIDDEN -> { /* No action */ }
        }
    }
}
