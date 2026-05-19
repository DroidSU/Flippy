package com.fliq.auth.repository

import com.fliq.common.Result
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithCredentials(credential: AuthCredential): Flow<Result<Unit>>
}
