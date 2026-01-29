package com.sujoy.flippy.auth.repository

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.flippy.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun signInWithCredentials(credential: AuthCredential): Flow<Result<Unit>> = flow {
        try {
            auth.signInWithCredential(credential).await()
            emit(Result.Success(Unit))
        } catch (e: FirebaseAuthException) {
            emit(Result.Failure(e.message ?: "Authentication failed"))
        }
    }.catch { e ->
        emit(Result.Failure(e.message ?: "Unexpected error occurred"))
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

    override fun guestLogin(): Flow<Result<Unit>> = flow {
        try {
            auth.signInAnonymously().await()
            emit(Result.Success(Unit))
        } catch (e: FirebaseAuthException) {
            emit(Result.Failure(e.message ?: "Guest login failed"))
        }
    }.catch { e ->
        emit(Result.Failure(e.message ?: "Unexpected error occurred"))
    }
}
