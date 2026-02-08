package com.sujoy.flippy.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.auth.repository.AuthRepository
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.common.Result
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository,
    private val auth: FirebaseAuth,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }

            authRepository.signInWithCredentials(credential).collect { result ->
                when (result) {
                    is Result.Success -> {
                        fetchUserData()
                    }

                    is Result.Failure -> {
                        _uiState.update {
                            AppUIState.Error(result.message)
                        }
                    }
                }
            }
        }
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }
            networkRepository.fetchUserData(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _userData.value = result.data
                        fetchMatchHistorySync(userId)
                    }

                    is Result.Failure -> {
                        // Proceed to success so user can set profile if fetch fails
                        _uiState.update { AppUIState.Success }
                    }
                }
            }
        }
    }

    private fun fetchMatchHistorySync(userId: String) {
        viewModelScope.launch {
            networkRepository.fetchMatchHistory(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val matches = result.data
                        if (matches.isNotEmpty()) {
                            val backedUpMatches = matches.map { it.copy(isBackedUp = true) }
                            matchRepository.saveMatches(backedUpMatches)
                        }
                        _uiState.update { AppUIState.Success }
                    }
                    is Result.Failure -> {
                        // Even if match history fails, we allow user to proceed
                        _uiState.update { AppUIState.Success }
                    }
                }
            }
        }
    }

    fun saveUserProfile(username: String, avatarId: Int) {
        if (username.isBlank()) {
            _uiState.update { AppUIState.Error("Username cannot be empty") }
            return
        }
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { AppUIState.Error("User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }
            val userDataToSave = UserData(userId = userId, username = username, avatarId = avatarId)
            when (val result =
                networkRepository.saveUserData(userDataToSave.username, userDataToSave.avatarId)) {
                is Result.Success -> {
                    _userData.value = userDataToSave
                    _uiState.update { AppUIState.Success }
                }

                is Result.Failure -> {
                    _uiState.update { AppUIState.Error(result.message) }
                }
            }
        }
    }

    fun onUsernameChanged(username: String) {
        _userData.update { it?.copy(username = username) }
    }

    fun onAvatarIdChanged(avatarId: Int) {
        _userData.update { it?.copy(avatarId = avatarId) }
    }


    fun resetState() {
        _uiState.update { AppUIState.Idle }
    }

    fun errorShown(errorMessage: String) {
        _uiState.update { AppUIState.Error(errorMessage) }
    }
}
