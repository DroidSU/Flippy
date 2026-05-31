package com.fliq.views.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.repository.ProfileRepository
import com.fliq.database.repository.StageRepository
import com.fliq.game_engine.models.v2.Boost
import com.fliq.game_engine.models.v2.GameEffectV2
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.repository.SoundRepository
import com.fliq.game_engine.v2.GameEngineV2
import com.fliq.game_engine.v2.StageManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModelV2 @Inject constructor(
    private val stageRepository: StageRepository,
    private val profileRepository: ProfileRepository,
    private val soundRepository: SoundRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _effects = MutableSharedFlow<GameEffectV2>()
    val effects = _effects.asSharedFlow()

    private val engine = GameEngineV2(
        scope = viewModelScope,
        onEffect = { effect ->
            viewModelScope.launch { 
                _effects.emit(effect)
                handleSoundEffect(effect)
                if (effect is GameEffectV2.VaultOpened) {
                    saveResults(effect)
                }
            }
        },
    )

    private fun handleSoundEffect(effect: GameEffectV2) {
        when (effect) {
            is GameEffectV2.Success -> soundRepository.playCoinTapSound()
            is GameEffectV2.Miss -> soundRepository.playBombSound()
            is GameEffectV2.Explosion -> soundRepository.playBombSound()
            is GameEffectV2.VaultOpened -> if (effect.stars > 0) soundRepository.playBonusSound() else soundRepository.playGameOverSound()
            is GameEffectV2.FeverStarted -> soundRepository.playBonusSound()
            else -> {}
        }
    }

    private fun saveResults(effect: GameEffectV2.VaultOpened) {
        val userId = auth.currentUser?.uid ?: return
        val stage = _currentStage.value ?: return
        val stageId = stage.id
        
        viewModelScope.launch {
            // 1. Save Stage Progress
            stageRepository.saveStageProgress(
                com.fliq.database.StageProgressEntity(
                    stageId = stageId,
                    userId = userId,
                    starsEarned = effect.stars,
                    bestScore = engine.score.value,
                    bestTime = 0, // TODO: Track time
                    isUnlocked = true,
                    isBackedUp = false
                )
            )

            // 2. Update User Data (XP & Coins)
            val currentData = profileRepository.getUserDataSync(userId)
            if (currentData != null) {
                profileRepository.saveUserData(
                    currentData.copy(
                        xp = currentData.xp + effect.xp,
                        coins = currentData.coins + effect.coins,
                        // Update currentStage if this was the latest one
                        currentStage = if (effect.stars > 0) {
                            // Extract stage number from ID (e.g., w1_s1 -> 1)
                            val currentNum = stageId.substringAfter("_s").toIntOrNull() ?: 1
                            maxOf(currentData.currentStage, currentNum + 1)
                        } else currentData.currentStage
                    )
                )
            }
        }
    }
    
    val gameState = engine.gameState
    val tiles = engine.tiles
    val score = engine.score
    val combo = engine.combo
    val lives = engine.lives
    val progress = engine.progress
    
    private val _selectedBoost = MutableStateFlow<Boost?>(null)
    val selectedBoost = _selectedBoost.asStateFlow()

    fun selectBoost(boost: Boost?) {
        _selectedBoost.value = if (_selectedBoost.value == boost) null else boost
    }

    private val _currentStage = MutableStateFlow<GameStage?>(null)
    val currentStage = _currentStage.asStateFlow()

    fun loadStage(stageId: String) {
        val stage = StageManager.getStage(stageId) ?: return
        _currentStage.value = stage
        _selectedBoost.value = null
        engine.initStage(stage)
    }

    fun startStage() {
        engine.initStage(_currentStage.value!!, _selectedBoost.value)
        engine.start()
    }

    fun onTileTapped(tileId: Int) {
        engine.onTileTapped(tileId)
    }

    fun onTileEntered(tileId: Int) {
        engine.onTileEntered(tileId)
    }

    fun pause() = engine.pause()
    fun resume() = engine.resume()
}
