package com.fliq.views.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliq.common.repository.ProfileRepository
import com.fliq.database.repository.StageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QuestMapViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val profileRepository: ProfileRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val userId = auth.currentUser?.uid ?: ""

    val userData = profileRepository.getUserData(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val stageProgress = stageRepository.getAllStageProgress(userId)
        .map { list -> list.associate { it.stageId to it.starsEarned } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
