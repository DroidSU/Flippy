package com.fliq.leaderboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.AppUIState
import com.fliq.common.Difficulty
import com.fliq.common.LeaderboardModel
import com.fliq.database.MatchHistory
import com.fliq.leaderboard.repository.LeaderboardRepository
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

    private val _leaderboard = MutableStateFlow<List<LeaderboardModel>>(emptyList())
    val leaderboard = _leaderboard.asStateFlow()

    private val _myScores = MutableStateFlow<List<MatchHistory>>(emptyList())
    val myScores = _myScores.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(1)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    private val _selectedDifficulty: MutableStateFlow<String> =
        MutableStateFlow(Difficulty.NORMAL.label)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    init {
        if(_selectedTabIndex.value == 1){
            getLeaderboardData()
        }
        else {
            getMyScores()
        }
    }

    private fun getLeaderboardData() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            leaderboardRepository.getLeaderBoard(selectedDifficulty.value).collect {
                _leaderboard.value = it
                _uiState.value = AppUIState.Success
            }
        }
    }

    fun filterWithDifficulty(difficulty: String) {
        _selectedDifficulty.value = difficulty
        getLeaderboardData()
    }

    fun getMyScores() {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                leaderboardRepository.getMyScores().collect {
                    _myScores.value = it
                    _uiState.value = AppUIState.Success
                }
            }
            catch(ex : Exception) {
                _uiState.value = AppUIState.Error(ex.message ?: "Something went wrong")
            }
        }
    }

    fun onSwitchTab(index: Int) {
        _selectedTabIndex.value = index

        if(index == 0) {
            getMyScores()
        }
        else {
            getLeaderboardData()
        }
    }
}
