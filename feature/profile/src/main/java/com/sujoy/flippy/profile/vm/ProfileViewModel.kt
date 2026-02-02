package com.sujoy.flippy.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.database.MatchHistory
import com.sujoy.flippy.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val playerId: String,
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState: StateFlow<AppUIState> = _uiState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _avatarId = MutableStateFlow(0)
    val avatarId: StateFlow<Int> = _avatarId.asStateFlow()

    private val _highestScoreMatch = MutableStateFlow<MatchHistory?>(null)
    val highestScoreMatch: StateFlow<MatchHistory?> = _highestScoreMatch.asStateFlow()

    private val _matchHistory = MutableStateFlow<List<MatchHistory>>(emptyList())
    val matchHistory: StateFlow<List<MatchHistory>> = _matchHistory.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val savedUsername = repository.getUsername()
        val savedAvatarId = repository.getAvatarId()

        _username.value = savedUsername
        _avatarId.value = savedAvatarId

        if (savedUsername.isNotEmpty()) {
            loadMatchData(playerId)
        }
    }

    private fun loadMatchData(playerId: String) {
        viewModelScope.launch {
            repository.getTopScores(playerId).collectLatest { scores ->
                _highestScoreMatch.value = scores.firstOrNull()
            }
        }

        viewModelScope.launch {
            val history = repository.getMatchHistory(playerId)
            _matchHistory.value = history
        }
    }

    fun saveProfile(username: String, avatarId: Int) {
        repository.saveProfile(username, avatarId)
        _username.value = username
        _avatarId.value = avatarId
        _uiState.value = AppUIState.Success
    }

    fun onEdit() {
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _isEditing.value = false
    }
}
