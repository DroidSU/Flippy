package com.sujoy.flippy.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sujoy.flippy.models.GameTile
import com.sujoy.flippy.utils.GameStatus
import kotlin.random.Random


class GameViewModel : ViewModel() {
    private val _tiles = mutableStateOf<List<GameTile>>(emptyList())
    val tiles: State<List<GameTile>> = _tiles

    private val _score = mutableStateOf(30)
    val score: State<Int> = _score

    private val _status = mutableStateOf<GameStatus>(GameStatus.PLAYING)
    val status: State<GameStatus> = _status

    init {
        resetGame()
    }

    fun onTileFlipped(tileId: Int) {
        // Prevent action if game is already over
        if (_status.value != GameStatus.PLAYING) return

        val updatedTiles = _tiles.value.toMutableList()
        val tileIndex = updatedTiles.indexOfFirst { it.id == tileId }
        val tile = updatedTiles[tileIndex]

        // Ignore if already flipped
        if (tile.isFlipped) return

        // Flip the tile
        updatedTiles[tileIndex] = tile.copy(isFlipped = true)
        _tiles.value = updatedTiles

        if (tile.isImage) {
            // Player won
            _status.value = GameStatus.WON
        } else {
            // Deduct score
            val newScore = _score.value + tile.value
            _score.value = newScore

            if (newScore <= 0) {
                // Player lost
                _status.value = GameStatus.LOST
            }
        }
    }

    fun resetGame() {
        _score.value = 30
        _status.value = GameStatus.PLAYING
        _tiles.value = generateTiles()
    }

    private fun generateTiles(gridSize: Int = 16): List<GameTile> {
        val imagePosition = Random.nextInt(0, gridSize)
        return List(gridSize) { index ->
            GameTile(id = index, isImage = (index == imagePosition))
        }
    }
}
