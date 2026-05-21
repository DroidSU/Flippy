package com.fliq.leaderboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AppUIState
import com.fliq.common.LeaderboardModel
import com.fliq.game_engine.models.Challenge
import com.fliq.leaderboard.repository.LeaderboardRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardModel>>(emptyList())
    val leaderboard = _leaderboard.asStateFlow()

    private val _selectedChallenge = MutableStateFlow(Challenge.SPEED_RUN)
    val selectedChallenge = _selectedChallenge.asStateFlow()

    val currentUserId: String get() = auth.currentUser?.uid ?: ""

    init {
        getLeaderboardData()
    }

    fun selectChallenge(challenge: Challenge) {
        _selectedChallenge.value = challenge
        getLeaderboardData()
    }

    private fun getLeaderboardData() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            leaderboardRepository.getLeaderBoard(_selectedChallenge.value.name).collect { data ->
                _leaderboard.value = data
                _uiState.update { AppUIState.Success }
            }
        }
    }
}
