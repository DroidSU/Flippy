package com.sujoy.flippy.auth.repository

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.common.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithCredentials(credential: AuthCredential): Flow<Result<Unit>>
    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
    fun guestLogin(): Flow<Result<Unit>>
}
