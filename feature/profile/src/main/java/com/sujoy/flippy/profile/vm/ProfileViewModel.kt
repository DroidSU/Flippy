package com.sujoy.flippy.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.profile.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState: StateFlow<AppUIState> = _uiState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _avatarId = MutableStateFlow(1)
    val avatarId: StateFlow<Int> = _avatarId.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _totalMatchesPlayed = MutableStateFlow(0)
    val totalMatchesPlayed: StateFlow<Int> = _totalMatchesPlayed.asStateFlow()

    private val _highestScore = MutableStateFlow(0)
    val highestScore: StateFlow<Int> = _highestScore.asStateFlow()

    private val _longestRound = MutableStateFlow(0L)
    val longestRound: StateFlow<Long> = _longestRound.asStateFlow()

    private val _accuracyRate = MutableStateFlow(0.0)
    val accuracyRate: StateFlow<Double> = _accuracyRate.asStateFlow()

    private val _reflexAverage = MutableStateFlow(0L)
    val reflexAverage: StateFlow<Long> = _reflexAverage.asStateFlow()


    init {
        loadProfile()
    }

    private fun loadProfile() {
        val savedUsername = repository.getUsername()
        val savedAvatarId = repository.getAvatarId()

        _username.value = savedUsername
        _avatarId.value = savedAvatarId

        loadMatchData()
    }

    private fun loadMatchData() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getMatchHistory().collect { matchHistories ->

                    if(matchHistories.isNotEmpty()){
                        _totalMatchesPlayed.value = matchHistories.size
                        _highestScore.value = matchHistories.maxByOrNull { it.score }?.score ?: 0
                        _longestRound.value = matchHistories.maxByOrNull { it -> it.gameDuration }?.gameDuration ?: 0L

                        val sumOfCorrectTaps = matchHistories.sumOf { it.correctTaps }
                        val sumOfTotalTaps = matchHistories.sumOf { it.totalTaps }

                        if(sumOfTotalTaps > 0) {
                            _accuracyRate.value = (sumOfCorrectTaps.toDouble() / sumOfTotalTaps.toDouble()) * 100
                        }

                        val sumOfReflexTimes = matchHistories.sumOf { it.totalReflexTime }
                        if(sumOfCorrectTaps > 0){
                            _reflexAverage.value = sumOfReflexTimes / sumOfCorrectTaps
                        }
                    }

                    _uiState.value = AppUIState.Success
                }
            }
            catch (ex : Exception) {
                _uiState.value = AppUIState.Error(ex.message ?: "Unknown Error")
            }
        }
    }

    fun saveProfile(username: String, avatarId: Int) {
        repository.saveProfile(username, avatarId)
        _username.value = username
        _avatarId.value = avatarId
        viewModelScope.launch(Dispatchers.IO) {
            if(networkRepository.isInternetAvailable()) {
                networkRepository.storeUserData(username, avatarId)
            }
        }
        _uiState.value = AppUIState.Success
    }

    fun onEdit() {
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _isEditing.value = false
    }
}
