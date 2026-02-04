package com.sujoy.flippy.auth.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.sujoy.flippy.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

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
}
