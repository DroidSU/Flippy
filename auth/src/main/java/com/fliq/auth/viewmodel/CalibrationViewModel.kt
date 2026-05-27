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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val profileRepository: ProfileRepository,
    private val preferencesRepository: GamePreferencesRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

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
