package com.fliq.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AppUIState
import com.fliq.common.NetworkRepository
import com.fliq.common.Result
import com.fliq.common.repository.ProfileRepository
import com.fliq.game_engine.repository.GamePreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CalibrationState {
    IDLE, ACTIVE, FINISHED
}

data class CalibrationScreenState(
    val currentState: CalibrationState = CalibrationState.IDLE,
    val currentTrial: Int = 0,
    val totalTrials: Int = 10,
    val lastOffset: Long = 0L,
    val trials: List<Long> = emptyList(),
    val averageOffset: Long = 0L
)

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val profileRepository: ProfileRepository,
    private val preferencesRepository: GamePreferencesRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _calibrationState = MutableStateFlow(CalibrationScreenState())
    val calibrationState: StateFlow<CalibrationScreenState> = _calibrationState.asStateFlow()

    fun startCalibration() {
        _calibrationState.update { 
            it.copy(
                currentState = CalibrationState.ACTIVE,
                currentTrial = 0,
                lastOffset = 0L,
                trials = emptyList()
            )
        }
    }

    fun recordTrial(offset: Long) {
        val currentState = _calibrationState.value
        if (currentState.currentState != CalibrationState.ACTIVE) return

        val newTrials = currentState.trials + offset
        val nextTrial = currentState.currentTrial + 1
        
        if (nextTrial >= currentState.totalTrials) {
            val average = newTrials.filter { it in -200..300 }.average().toLong().coerceAtLeast(0L)
            _calibrationState.update {
                it.copy(
                    currentState = CalibrationState.FINISHED,
                    currentTrial = nextTrial,
                    trials = newTrials,
                    lastOffset = offset,
                    averageOffset = average
                )
            }
        } else {
            _calibrationState.update {
                it.copy(
                    currentTrial = nextTrial,
                    trials = newTrials,
                    lastOffset = offset
                )
            }
        }
    }

    fun saveLatencyOffset(offsetMs: Long) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }
            
            when (val result = networkRepository.updateLatencyOffset(offsetMs)) {
                is Result.Success -> {
                    val currentData = profileRepository.getUserDataSync(userId)
                    if (currentData != null) {
                        profileRepository.saveUserData(currentData.copy(latencyOffset = offsetMs))
                    }
                    _uiState.update { AppUIState.Success }

                    preferencesRepository.setUserCalibrated(true)
                }
                is Result.Failure -> {
                    _uiState.update { AppUIState.Error(result.message) }
                }
            }
        }
    }
    
    fun resetState() {
        _uiState.update { AppUIState.Idle }
    }
}
