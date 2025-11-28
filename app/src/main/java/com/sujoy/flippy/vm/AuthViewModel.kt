package com.sujoy.flippy.vm

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.models.AuthUiState
import com.sujoy.flippy.repositories.auth.AuthRepository
import com.sujoy.flippy.utils.AuthResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"
private const val RESEND_TIMEOUT = 60

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private var resendTimerJob: Job? = null

    /**
     * Signs in the user using a given credential (from Google or Phone).
     * Updates the UI state based on the outcome.
     */
    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.signInWithCredentials(credential).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Authentication successful.")
                        _uiState.update {
                            it.copy(isLoading = false, isAuthSuccessful = true)
                        }
                    }

                    is AuthResult.Failure -> {
                        Log.w(TAG, "Authentication error: ${result.message}")
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                }
            }
        }
    }

    /**
     * Kicks off the phone number verification process by sending an OTP.
     */
    fun sendOtp(activity: Activity, phoneNumber: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "Phone verification completed automatically.")
                signInWithCredential(credential = credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "Phone verification failed.", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to send OTP: ${e.message}") }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "OTP code sent successfully.")
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

        // Ensure phone number has country code
        val formattedPhoneNumber = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
        repository.sendVerificationCode(activity, formattedPhoneNumber, callbacks)
    }

    /**
     * Verifies the OTP code entered by the user.
     */
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
            for (i in RESEND_TIMEOUT downTo 1) {
                _uiState.update { it.copy(resendTimer = i) }
                delay(1000)
            }
            _uiState.update { it.copy(resendTimer = 0) }
        }
    }

    /**
     * Resets the auth flow to go back to the phone number entry screen.
     */
    fun resetAuthFlow() {
        resendTimerJob?.cancel()
        _uiState.update { AuthUiState() } // Reset to initial state
    }

    /**
     * To be called when the error message has been shown and should be cleared.
     */
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        resendTimerJob?.cancel() // Ensure timer is stopped when ViewModel is cleared
    }
}
