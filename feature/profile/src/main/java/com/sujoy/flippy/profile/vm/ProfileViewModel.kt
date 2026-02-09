package com.sujoy.flippy.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.common.Result
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
    private val networkRepository: NetworkRepository,
    private val auth: FirebaseAuth
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

        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                networkRepository.fetchUserData(userId).collect { result ->
                    if (result is Result.Success) {
                        val userData = result.data
                        if (userData != null) {
                            // Sync remote to local if they differ
                            if (userData.username != savedUsername || userData.avatarId != savedAvatarId) {
                                _username.value = userData.username
                                _avatarId.value = userData.avatarId
                                repository.saveProfile(userData.username, userData.avatarId)
                            }
                        }
                    }
                }
            }
        }

        loadMatchData()
    }

    private fun loadMatchData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getMatchHistory().collect { matchHistories ->

                    if (matchHistories.isNotEmpty()) {
                        _totalMatchesPlayed.value = matchHistories.size
                        _highestScore.value = matchHistories.maxByOrNull { it.score }?.score ?: 0
                        _longestRound.value =
                            matchHistories.maxByOrNull { it.gameDuration }?.gameDuration ?: 0L

                        val sumOfCorrectTaps = matchHistories.sumOf { it.correctTaps }
                        val sumOfTotalTaps = matchHistories.sumOf { it.totalTaps }

                        if (sumOfTotalTaps > 0) {
                            _accuracyRate.value =
                                (sumOfCorrectTaps.toDouble() / sumOfTotalTaps.toDouble()) * 100
                        }

                        val sumOfReflexTimes = matchHistories.sumOf { it.totalReflexTime }
                        if (sumOfCorrectTaps > 0) {
                            _reflexAverage.value = sumOfReflexTimes / sumOfCorrectTaps
                        }
                    }
                }
            } catch (ex: Exception) {
                // Handle match history load error
            }
        }
    }

    fun onUsernameChanged(username: String) {
        _username.value = username
    }

    fun onAvatarIdChanged(avatarId: Int) {
        _avatarId.value = avatarId
    }

    fun saveProfile(username: String, avatarId: Int) {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch {
            val currentSavedUsername = repository.getUsername()

            // Only check for uniqueness if the username has actually changed
            if (username.lowercase() != currentSavedUsername.lowercase()) {
                if (networkRepository.isUsernameExisting(username)) {
                    _uiState.value = AppUIState.Error("Username already exists. Please choose another one.")
                    return@launch
                }
                else{
                    // Save to Remote, including the old username to be cleared
                    val result = networkRepository.saveUserData(username, avatarId, currentSavedUsername)

                    when (result) {
                        is Result.Success -> {
                            // Save to Local
                            repository.saveProfile(username, avatarId)
                            _username.value = username
                            _avatarId.value = avatarId
                            _isEditing.value = false
                            _uiState.value = AppUIState.Success
                        }
                        is Result.Failure -> {
                            _uiState.value = AppUIState.Error(result.message)
                        }
                    }
                }
            }
            else{
                _uiState.value = AppUIState.Error("Choose a different username.")
                return@launch
            }
        }
    }

    fun onEdit() {
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _isEditing.value = false
    }

    fun resetState() {
        _uiState.value = AppUIState.Idle
    }
}
