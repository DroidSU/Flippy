package com.sujoy.leaderboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.Difficulty
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

    private var _leaderboard: List<LeaderboardModel> = emptyList()

    private val _filteredLeaderboard = MutableStateFlow<List<LeaderboardModel>>(emptyList())
    val filteredLeaderboard = _filteredLeaderboard.asStateFlow()


    private val _selectedDifficulty: MutableStateFlow<String> =
        MutableStateFlow(Difficulty.NORMAL.label)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    init {
        getLeaderboardData()
    }

    private fun getLeaderboardData() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            leaderboardRepository.getLeaderBoard().collect {
                _leaderboard = it
                _filteredLeaderboard.value =
                    _leaderboard.filter { leaderboardModel -> leaderboardModel.difficulty == selectedDifficulty.value }
                _uiState.value = AppUIState.Success
            }
        }
    }

    fun filterWithDifficulty(difficulty: String) {
        _filteredLeaderboard.value = _leaderboard.filter { it.difficulty == difficulty }
    }

}
