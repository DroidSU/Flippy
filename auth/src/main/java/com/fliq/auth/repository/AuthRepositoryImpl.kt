package com.fliq.auth.repository

import com.fliq.common.NetworkRepository
import com.fliq.common.Result
import com.fliq.common.repository.ProfileRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val networkRepository: NetworkRepository,
    private val profileRepository: ProfileRepository
) : AuthRepository {

    override fun signInWithCredentials(credential: AuthCredential): Flow<Result<Unit>> = flow {
        try {
            val authResult = auth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid
            
            if (userId != null) {
                // After successful sign in, fetch user profile from Firebase and sync to SharedPreferences
                networkRepository.fetchUserData(userId).collect { result ->
                    if (result is Result.Success) {
                        val userData = result.data
                        if (userData != null) {
                            profileRepository.saveUserData(userData)
                        }
                    }
                }
            }

            emit(Result.Success(Unit))
        } catch (e: FirebaseAuthException) {
            emit(Result.Failure(e.message ?: "Authentication failed"))
        }
    }.catch { e ->
        emit(Result.Failure(e.message ?: "Unexpected error occurred"))
    }
}
