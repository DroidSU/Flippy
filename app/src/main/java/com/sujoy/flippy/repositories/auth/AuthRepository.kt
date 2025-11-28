package com.sujoy.flippy.repositories.auth

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.utils.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signInWithCredentials(credential: AuthCredential): Flow<AuthResult>

    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    /**
     * Creates a credential from the verification ID and OTP code.
     */
    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
}