package com.sujoy.leaderboard.viewmodels

import androidx.lifecycle.ViewModel
import com.sujoy.flippy.common.AppUIState
import com.sujoy.leaderboard.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardViewModel(private val leaderboardRepository: LeaderboardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()


}