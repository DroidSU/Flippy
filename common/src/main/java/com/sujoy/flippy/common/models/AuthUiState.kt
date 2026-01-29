package com.sujoy.flippy.common.models

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthSuccessful: Boolean = false,
    val isOtpSent: Boolean = false,
    val verificationId: String? = null,
    val resendTimer: Int = 0
)
