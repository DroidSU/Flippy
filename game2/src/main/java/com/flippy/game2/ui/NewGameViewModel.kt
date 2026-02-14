package com.flippy.game2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flippy.game2.data.NewGameUiEvent
import com.sujoy.flippy.core.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class TargetCircle(
    val id: Long,
    val xPercent: Float,
    val yPercent: Float,
    val radiusDp: Float = 45f
)

@HiltViewModel
class NewGameViewModel @Inject constructor(
    val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _title = MutableStateFlow("Reaction Test")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("Get ready...")
    val description = _description.asStateFlow()

    private val _activeTarget = MutableStateFlow<TargetCircle?>(null)
    val activeTarget = _activeTarget.asStateFlow()

    private val _events = MutableSharedFlow<NewGameUiEvent>()
    val events = _events.asSharedFlow()

    private val timestamps = listOf(3, 5, 7, 10, 11, 13, 15, 20)
    
    init {
        startGame()
    }

    private fun startGame() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var nextIndex = 0
            
            while (nextIndex < timestamps.size) {
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000f
                if (elapsedSeconds >= timestamps[nextIndex]) {
                    showNewTarget()
                    nextIndex++
                    _description.value = "Tap target $nextIndex of ${timestamps.size}"
                }
                delay(50) // High frequency check for smoother timing
            }
            delay(2000)
            _activeTarget.value = null
            _description.value = "Well done! Game Complete."
        }
    }

    private fun showNewTarget() {
        _activeTarget.value = TargetCircle(
            id = System.currentTimeMillis(),
            xPercent = Random.nextFloat().coerceIn(0.15f, 0.85f),
            yPercent = Random.nextFloat().coerceIn(0.2f, 0.8f)
        )
    }

    fun onCanvasTap(x: Float, y: Float, screenWidth: Float, screenHeight: Float, density: Float) {
        val target = _activeTarget.value ?: return
        
        val targetX = target.xPercent * screenWidth
        val targetY = target.yPercent * screenHeight
        val radiusPx = target.radiusDp * density
        
        val distance = sqrt((x - targetX).pow(2) + (y - targetY).pow(2))
        
        if (distance <= radiusPx) {
            viewModelScope.launch {
                _events.emit(NewGameUiEvent.ShowRipple(x, y, System.currentTimeMillis()))
                _activeTarget.value = null
            }
        }
    }
}
