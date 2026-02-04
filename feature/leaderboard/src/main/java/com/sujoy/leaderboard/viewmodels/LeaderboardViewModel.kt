package com.sujoy.leaderboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.LeaderboardModel
import com.sujoy.leaderboard.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val leaderboard: MutableStateFlow<List<LeaderboardModel>> = MutableStateFlow(emptyList())
    val leaderboardState = leaderboard.asStateFlow()

    init {
        getLeaderboardData()
    }

    private fun getLeaderboardData() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            leaderboardRepository.getLeaderBoard().collect {
                leaderboard.value = it
                _uiState.value = AppUIState.Success
            }
        }
    }

}
