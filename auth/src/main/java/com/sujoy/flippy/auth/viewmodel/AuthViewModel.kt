package com.sujoy.flippy.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.sujoy.flippy.auth.repository.AuthRepository
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()


    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.update { AppUIState.Loading }

            repository.signInWithCredentials(credential).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { AppUIState.Success }
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

    fun resetState() {
        _uiState.update { AppUIState.Idle }
    }

    fun errorShown(errorMessage : String) {
        _uiState.update { AppUIState.Error(errorMessage) }
    }
}
