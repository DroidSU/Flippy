package com.sujoy.flippy.models

// Data class to hold all UI state for the AuthenticationScreen
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthSuccessful: Boolean = false,

    val isOtpSent: Boolean = false,
    val verificationId: String? = null,
    val resendTimer: Int = 0
)
