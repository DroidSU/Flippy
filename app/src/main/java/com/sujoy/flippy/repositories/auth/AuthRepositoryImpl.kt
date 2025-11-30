package com.sujoy.flippy.repositories.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.utils.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun signInWithCredentials(credential: AuthCredential): Flow<AuthResult> = flow {
        try {
            auth.signInWithCredential(credential).await()
            Log.d(TAG, "Firebase sign-in with credential successful.")
            emit(AuthResult.Success)
        } catch (e: FirebaseAuthException) {
            Log.w(TAG, "Firebase sign-in failed: ${e.errorCode}", e)
            emit(AuthResult.Failure(e.message ?: "An unknown authentication error occurred."))
        }
    }.catch { e ->
        Log.e(TAG, "A non-Firebase exception occurred during sign-in.", e)
        emit(AuthResult.Failure(e.message ?: "An unexpected error occurred."))
    }

    override fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, otpCode)
    }

    override fun guestLogin(): Flow<AuthResult> = flow {
        try {
            auth.signInAnonymously().await()
            Log.d(TAG, "Firebase anonymous sign-in successful.")
            emit(AuthResult.Success)
        } catch (e: FirebaseAuthException) {
            Log.w(TAG, "Firebase anonymous sign-in failed: ${e.errorCode}", e)
            emit(AuthResult.Failure(e.message ?: "An unknown authentication error occurred."))
        }
    }.catch { e ->
        Log.e(TAG, "A non-Firebase exception occurred during anonymous sign-in.", e)
        emit(AuthResult.Failure(e.message ?: "An unexpected error occurred."))
    }
}