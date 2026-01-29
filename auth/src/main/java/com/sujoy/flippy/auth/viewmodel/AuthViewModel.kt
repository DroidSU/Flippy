package com.sujoy.flippy.auth.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.auth.repository.AuthRepository
import com.sujoy.flippy.common.ConstantsManager
import com.sujoy.flippy.common.Result
import com.sujoy.flippy.common.models.AuthUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private var resendTimerJob: Job? = null

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.signInWithCredentials(credential).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, isAuthSuccessful = true)
                        }
                    }
                    is Result.Failure -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                }
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.guestLogin().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, isAuthSuccessful = true)
                        }
                    }
                    is Result.Failure -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                }
            }
        }
    }

    fun sendOtp(activity: Activity, phoneNumber: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential = credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to send OTP: ${e.message}") }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isOtpSent = true,
                        verificationId = verificationId
                    )
                }
                startResendTimer()
            }
        }

        val formattedPhoneNumber = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
        repository.sendVerificationCode(activity, formattedPhoneNumber, callbacks)
    }

    fun verifyOtp(otpCode: String) {
        val verificationId = _uiState.value.verificationId
        if (verificationId == null) {
            _uiState.update { it.copy(error = "Cannot verify OTP without a verification ID.") }
            return
        }

        try {
            val credential = repository.getPhoneAuthCredential(verificationId, otpCode)
            signInWithCredential(credential)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Invalid OTP code. Please try again.") }
        }
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            for (i in ConstantsManager.RESEND_TIMEOUT downTo 1) {
                _uiState.update { it.copy(resendTimer = i) }
                delay(1000)
            }
            _uiState.update { it.copy(resendTimer = 0) }
        }
    }

    fun resetAuthFlow() {
        resendTimerJob?.cancel()
        _uiState.update { AuthUiState() }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        resendTimerJob?.cancel()
    }
}
