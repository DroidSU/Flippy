package com.fliq.game_engine.v2

import com.fliq.game_engine.models.v2.Boost
import com.fliq.game_engine.models.v2.GameClass
import com.fliq.game_engine.models.v2.GameEffectV2
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.models.v2.GameStateV2
import com.fliq.game_engine.models.v2.GoalType
import com.fliq.game_engine.models.v2.StageConfig
import com.fliq.game_engine.models.v2.TileState
import com.fliq.game_engine.models.v2.TileType
import com.fliq.game_engine.models.v2.TileV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameEngineV2(
    private val scope: CoroutineScope,
    private val onEffect: (GameEffectV2) -> Unit = {},
) {
    private val _gameState = MutableStateFlow(GameStateV2.IDLE)
    val gameState = _gameState.asStateFlow()

    private val _tiles = MutableStateFlow<List<TileV2>>(emptyList())
    val tiles = _tiles.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _combo = MutableStateFlow(0)
    val combo = _combo.asStateFlow()

    private val _lives = MutableStateFlow(3)
    val lives = _lives.asStateFlow()

    private val _progress = MutableStateFlow(0f) // 0.0 to 1.0
    val progress = _progress.asStateFlow()

    var currentStageId: String? = null
        private set

    private var currentStage: GameStage? = null
    private var gameJob: Job? = null
    private var spawnJob: Job? = null
    private var coinsHit = 0

    // For Memory mode
    private val memorySequence = mutableListOf<Int>()
    private var userSequenceIndex = 0

    // For Trace mode
    private val tracePath = mutableListOf<Int>()
    private var currentTraceIndex = 0
    private var activeBoost: Boost? = null
    private var shieldActive = false

    fun initStage(stage: GameStage, boost: Boost? = null) {
        currentStage = stage
        currentStageId = stage.id
        activeBoost = boost
        shieldActive = boost == Boost.SHIELD

        _gameState.value = GameStateV2.BRIEFING
        _tiles.value = List(16) { TileV2(id = it) } // Standard 4x4
        _score.value = 0
        _combo.value = 0
        _lives.value = 3
        _progress.value = 0f
        coinsHit = 0
    }

    fun start() {
        val stage = currentStage ?: return
        gameJob?.cancel()
        gameJob = scope.launch {
            _gameState.value = GameStateV2.COUNTDOWN
            delay(3000) // 3, 2, 1

            when (stage.classType) {
                GameClass.REFLEX -> startReflexMode(stage.config as StageConfig.Reflex)
                GameClass.MEMORY -> startMemoryMode(stage.config as StageConfig.Memory)
                GameClass.TRACE -> startTraceMode(stage.config as StageConfig.Trace)
                GameClass.HYBRID -> startHybridMode(stage.config as StageConfig.Hybrid)
            }
        }
    }

    private fun startReflexMode(config: StageConfig.Reflex) {
        _gameState.value = GameStateV2.ACTION
        var coinsSpawned = 0
        
        spawnJob = scope.launch {
            while (isActive && (coinsSpawned < config.targetCount) && (_lives.value > 0)) {
                spawnTile(config)
                coinsSpawned++
                delay(config.spawnInterval)
            }
        }
    }

    private fun spawnTile(config: StageConfig.Reflex) {
        val availableTiles = _tiles.value.filter { it.state == TileState.INACTIVE }
        if (availableTiles.isEmpty()) return

        val targetTile = availableTiles.random()
        val isBomb = Random.nextFloat() < config.bombFrequency
        val type = if (isBomb) TileType.BOMB else TileType.COIN
        
        // Determine if it's a shielded coin (multi-tap)
        val isShielded = !isBomb && Random.nextFloat() < config.multiTapChance
        val requiredTaps = if (isShielded) 2 else 1

        val finalDuration = if (activeBoost == Boost.SLOW_MO) (config.targetDuration * 1.2).toLong() else config.targetDuration

        updateTile(targetTile.id) {
            it.copy(
                type = type,
                state = TileState.ACTIVE,
                requiredTaps = requiredTaps,
                tapCount = 0,
                duration = finalDuration,
                lastUpdateTime = System.currentTimeMillis()
            )
        }

        // Auto-vanish logic
        scope.launch {
            delay(finalDuration)
            val currentTile = _tiles.value[targetTile.id]
            if (currentTile.state == TileState.ACTIVE) {
                if (currentTile.type == TileType.COIN) {
                    handleMiss()
                } else {
                    // Bomb vanished safely
                    updateTile(targetTile.id) { it.copy(state = TileState.INACTIVE) }
                }
            }
        }
    }

    private suspend fun startMemoryMode(config: StageConfig.Memory) {
        _gameState.value = GameStateV2.PREVIEW
        memorySequence.clear()
        repeat(config.sequenceCount) {
            memorySequence.add(Random.nextInt(0, 16))
        }

        // Show pattern
        for (tileId in memorySequence) {
            updateTile(tileId) { it.copy(state = TileState.ACTIVE, type = TileType.COIN) }
            delay(config.visibleDuration)
            updateTile(tileId) { it.copy(state = TileState.INACTIVE) }
            delay(config.patternSpeed)
        }

        _gameState.value = GameStateV2.ACTION
        userSequenceIndex = 0
    }

    private suspend fun startTraceMode(config: StageConfig.Trace) {
        _gameState.value = GameStateV2.ACTION
        tracePath.clear()
        
        // Generate a random path of adjacent tiles
        var lastTileId = Random.nextInt(0, 16)
        tracePath.add(lastTileId)
        
        while (tracePath.size < config.pathLength) {
            val adjacent = getAdjacentTiles(lastTileId).filter { it !in tracePath }
            if (adjacent.isEmpty()) break
            lastTileId = adjacent.random()
            tracePath.add(lastTileId)
        }

        // Show all path tiles as LOCKED initially
        _tiles.update { list ->
            list.map { tile ->
                if (tile.id in tracePath) tile.copy(type = TileType.LOCKED_COIN, state = TileState.LOCKED)
                else tile
            }
        }
        
        // Activate only the first tile
        updateTile(tracePath[0]) { it.copy(state = TileState.ACTIVE) }
        currentTraceIndex = 0
    }

    private fun getAdjacentTiles(id: Int): List<Int> {
        val row = id / 4
        val col = id % 4
        val adjacent = mutableListOf<Int>()
        if (row > 0) adjacent.add(id - 4)
        if (row < 3) adjacent.add(id + 4)
        if (col > 0) adjacent.add(id - 1)
        if (col < 3) adjacent.add(id + 1)
        return adjacent
    }

    fun onTileEntered(tileId: Int) {
        if (_gameState.value != GameStateV2.ACTION) return
        
        val stage = currentStage ?: return
        if (stage.classType != GameClass.TRACE) return

        if (tileId == tracePath[currentTraceIndex]) {
            handleCoinHit(tileId)
            updateTile(tileId) { it.copy(state = TileState.POPPED) }
            
            currentTraceIndex++
            _progress.value = currentTraceIndex.toFloat() / tracePath.size

            if (currentTraceIndex < tracePath.size) {
                // Activate next tile in path
                updateTile(tracePath[currentTraceIndex]) { it.copy(state = TileState.ACTIVE) }
            } else {
                endGame(true)
            }
        }
    }

    private suspend fun startHybridMode(config: StageConfig.Hybrid) {
        // Hybrid: Memory pattern at top, Reflex coins at bottom
        _gameState.value = GameStateV2.PREVIEW
        
        // Setup Memory Sequence (Top 2 rows: 0-7)
        memorySequence.clear()
        repeat(config.memoryConfig.sequenceCount) {
            memorySequence.add(Random.nextInt(0, 8))
        }

        // Show pattern while reflex mode starts in background? 
        // Or show pattern first, then start ACTION where both happen?
        // Proposal: PREVIEW shows pattern. ACTION requires repeating pattern while other coins spawn.
        
        for (tileId in memorySequence) {
            updateTile(tileId) { it.copy(state = TileState.ACTIVE, type = TileType.COIN) }
            delay(config.memoryConfig.visibleDuration)
            updateTile(tileId) { it.copy(state = TileState.INACTIVE) }
            delay(config.memoryConfig.patternSpeed)
        }

        _gameState.value = GameStateV2.ACTION
        userSequenceIndex = 0
        
        // Start Reflex spawns in bottom 2 rows (8-15)
        spawnJob = scope.launch {
            while (isActive && _lives.value > 0) {
                spawnHybridReflexTile(config.reflexConfig)
                delay(config.reflexConfig.spawnInterval)
            }
        }
    }

    private fun spawnHybridReflexTile(config: StageConfig.Reflex) {
        val availableTiles = _tiles.value.filter { it.id >= 8 && it.state == TileState.INACTIVE }
        if (availableTiles.isEmpty()) return

        val targetTile = availableTiles.random()
        val isBomb = Random.nextFloat() < config.bombFrequency
        val type = if (isBomb) TileType.BOMB else TileType.COIN

        updateTile(targetTile.id) {
            it.copy(
                type = type,
                state = TileState.ACTIVE,
                duration = config.targetDuration,
                lastUpdateTime = System.currentTimeMillis()
            )
        }

        scope.launch {
            delay(config.targetDuration)
            val currentTile = _tiles.value[targetTile.id]
            if (currentTile.state == TileState.ACTIVE && currentTile.type == TileType.COIN) {
                handleMiss()
            } else {
                updateTile(targetTile.id) { it.copy(state = TileState.INACTIVE) }
            }
        }
    }

    fun onTileTapped(tileId: Int) {
        if (_gameState.value != GameStateV2.ACTION && _gameState.value != GameStateV2.FEVER) return
        
        val tile = _tiles.value[tileId]
        if (tile.state != TileState.ACTIVE) return

        val stage = currentStage ?: return
        
        when (stage.classType) {
            GameClass.REFLEX -> handleReflexTap(tile)
            GameClass.MEMORY -> handleMemoryTap(tile)
            GameClass.HYBRID -> {
                if (tileId < 8) handleMemoryTap(tile)
                else handleReflexTap(tile)
            }
            else -> {}
        }
    }

    private fun handleReflexTap(tile: TileV2) {
        if (tile.type == TileType.BOMB) {
            handleBombHit(tile.id)
            updateTile(tile.id) { it.copy(state = TileState.POPPED) }
            
            // Return to inactive after animation delay
            scope.launch {
                delay(300)
                updateTile(tile.id) { it.copy(state = TileState.INACTIVE) }
            }
        } else {
            val newTapCount = tile.tapCount + 1
            if (newTapCount >= tile.requiredTaps) {
                handleCoinHit(tile.id)
                coinsHit++
                updateTile(tile.id) { it.copy(state = TileState.POPPED, tapCount = newTapCount) }
                
                val stage = currentStage ?: return
                val reflexConfig = stage.config as StageConfig.Reflex
                _progress.value = coinsHit.toFloat() / reflexConfig.targetCount

                if (coinsHit >= reflexConfig.targetCount) {
                    endGame(true)
                }

                // Return to inactive after animation delay
                scope.launch {
                    delay(300)
                    updateTile(tile.id) { it.copy(state = TileState.INACTIVE) }
                }
            } else {
                // Not popped yet (shielded coin)
                updateTile(tile.id) { it.copy(tapCount = newTapCount) }
                onEffect(GameEffectV2.Success(tileId = tile.id, combo = _combo.value))
            }
        }
    }

    private fun handleMemoryTap(tile: TileV2) {
        if (tile.id == memorySequence[userSequenceIndex]) {
            handleCoinHit(tile.id)
            userSequenceIndex++
            updateTile(tile.id) { it.copy(state = TileState.POPPED) }
            
            val stage = currentStage ?: return
            val memoryConfig = if (stage.classType == GameClass.HYBRID) (stage.config as StageConfig.Hybrid).memoryConfig 
                               else stage.config as StageConfig.Memory
            _progress.value = userSequenceIndex.toFloat() / memoryConfig.sequenceCount

            if (userSequenceIndex >= memorySequence.size) {
                endGame(true)
            }
        } else {
            handleMiss()
            endGame(false)
        }
    }

    private fun handleCoinHit(tileId: Int) {
        _score.value += 10 + (_combo.value * 2)
        _combo.value++
        
        val stage = currentStage ?: return
        val config = stage.config
        if (config is StageConfig.Reflex && _combo.value >= config.feverThreshold) {
            if (_gameState.value != GameStateV2.FEVER) {
                _gameState.value = GameStateV2.FEVER
                onEffect(GameEffectV2.FeverStarted)
            }
        }
        
        onEffect(GameEffectV2.Success(tileId = tileId, combo = _combo.value))
    }

    private fun handleMiss() {
        if (shieldActive) {
            shieldActive = false
            onEffect(GameEffectV2.Miss) // Still play effect but no penalty
            return
        }
        if (_gameState.value == GameStateV2.FEVER) {
            _gameState.value = GameStateV2.ACTION
            onEffect(GameEffectV2.FeverEnded)
        }
        _combo.value = 0
        _lives.value--
        onEffect(GameEffectV2.Miss)
        if (_lives.value <= 0) {
            endGame(false)
        }
    }

    private fun handleBombHit(tileId: Int) {
        if (shieldActive) {
            shieldActive = false
            onEffect(GameEffectV2.Explosion(tileId))
            return
        }
        _combo.value = 0
        _lives.value--
        onEffect(GameEffectV2.Explosion(tileId))
        if (_lives.value <= 0) {
            endGame(false)
        }
    }

    private fun endGame(cleared: Boolean) {
        spawnJob?.cancel()
        _gameState.value = GameStateV2.FALLOUT
        
        val stage = currentStage ?: return
        val accuracy = if (userSequenceIndex + _combo.value > 0) 1.0f else 0f // Simple logic for now
        
        // Calculate Stars
        var stars = 0
        if (cleared) {
            stars = 1
            stage.goals.forEach { goal ->
                when (goal.type) {
                    GoalType.ACCURACY -> if (accuracy >= goal.targetValue) stars = maxOf(stars, goal.starIndex)
                    GoalType.COMBO -> if (_combo.value >= goal.targetValue) stars = maxOf(stars, goal.starIndex)
                    else -> {}
                }
            }
        }

        scope.launch {
            delay(1500)
            _gameState.value = GameStateV2.VAULT
            val xp = if (activeBoost == Boost.DOUBLE_XP) stage.rewards.xp * 2 else stage.rewards.xp
            onEffect(GameEffectV2.VaultOpened(stars, xp, stage.rewards.coins))
        }
    }

    private fun updateTile(id: Int, update: (TileV2) -> TileV2) {
        _tiles.update { list ->
            list.map { if (it.id == id) update(it) else it }
        }
    }

    fun pause() {
        if (_gameState.value == GameStateV2.ACTION) {
            _gameState.value = GameStateV2.PAUSED
            spawnJob?.cancel()
        }
    }

    fun resume() {
        if (_gameState.value == GameStateV2.PAUSED) {
            val stage = currentStage ?: return
            if (stage.classType == GameClass.REFLEX) {
                scope.launch {
                    startReflexMode(stage.config as StageConfig.Reflex)
                }
            }
        }
    }
}
