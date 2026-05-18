package com.sujoy.flippy.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.Badge
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.common.Result
import com.sujoy.flippy.common.repository.ProfileRepository
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.database.repository.BadgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val badgeRepository: BadgeRepository,
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

    private val _unlockedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val unlockedBadges: StateFlow<List<Badge>> = _unlockedBadges.asStateFlow()


    init {
        loadProfile()
    }

    private fun loadProfile() {
        val savedUsername = profileRepository.getUsername()
        val savedAvatarId = profileRepository.getAvatarId()

        _username.value = savedUsername
        _avatarId.value = savedAvatarId

        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                // Observe local user data
                profileRepository.getUserData(userId).collect { localData ->
                    if (localData != null) {
                        _username.value = localData.username
                        _avatarId.value = localData.avatarId
                        _totalMatchesPlayed.value = localData.totalMatches
                        _highestScore.value = localData.highestScore
                        _longestRound.value = localData.longestRound
                        
                        if (localData.totalTaps > 0) {
                            _accuracyRate.value = (localData.totalCorrectTaps.toDouble() / localData.totalTaps.toDouble()) * 100
                        }
                        
                        if (localData.totalCorrectTaps > 0) {
                            _reflexAverage.value = localData.totalReflexTime / localData.totalCorrectTaps
                        }
                    }
                }
            }

            viewModelScope.launch {
                badgeRepository.getBadgesForUser(userId).collect { localBadges ->
                    _unlockedBadges.value = localBadges.mapNotNull { Badge.fromId(it.badgeId) }
                }
            }

            viewModelScope.launch {
                networkRepository.fetchUserData(userId).collect { result ->
                    if (result is Result.Success) {
                        val userData = result.data
                        if (userData != null) {
                            // Sync remote to local
                            profileRepository.saveUserData(userData)
                        }
                    }
                }
            }

            viewModelScope.launch {
                networkRepository.fetchBadges(userId).collect { result ->
                    if (result is Result.Success) {
                        badgeRepository.syncBadgesFromServer(result.data)
                    }
                }
            }
        }
    }

    fun onAvatarIdChanged(avatarId: Int) {
        _avatarId.value = avatarId
    }

    fun saveProfile(avatarId: Int) {
        _uiState.value = AppUIState.Loading
        viewModelScope.launch {
            val username = _username.value
            val userId = auth.currentUser?.uid ?: return@launch
            
            val result = networkRepository.updateUserName(username, avatarId)

            when (result) {
                is Result.Success -> {
                    // Get current stats to not lose them
                    val currentData = profileRepository.getUserDataSync(userId) ?: UserData(
                        userId = userId,
                        username = username,
                        avatarId = avatarId
                    )
                    val updatedData = currentData.copy(avatarId = avatarId)

                    // Save to Local using saveUserData
                    profileRepository.saveUserData(updatedData)
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

    fun onEdit() {
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _username.value = profileRepository.getUsername()
        _avatarId.value = profileRepository.getAvatarId()
        _isEditing.value = false
    }

    fun resetState() {
        _uiState.value = AppUIState.Idle
    }
}
