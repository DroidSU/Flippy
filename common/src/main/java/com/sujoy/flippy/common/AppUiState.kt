package com.sujoy.flippy.common

sealed interface AppUIState {
    data object Loading : AppUIState
    data object Success : AppUIState
    data class Error(val message: String) : AppUIState
    data object Idle : AppUIState
}
