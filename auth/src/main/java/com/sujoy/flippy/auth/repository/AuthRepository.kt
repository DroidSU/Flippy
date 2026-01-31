package com.sujoy.flippy.auth.repository

import com.google.firebase.auth.AuthCredential
import com.sujoy.flippy.common.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithCredentials(credential: AuthCredential): Flow<Result<Unit>>
}
