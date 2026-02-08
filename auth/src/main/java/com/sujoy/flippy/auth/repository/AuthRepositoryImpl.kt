package com.sujoy.flippy.auth.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.sujoy.flippy.common.NetworkRepository
import com.sujoy.flippy.common.Result
import com.sujoy.flippy.profile.repository.ProfileRepository
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
                            profileRepository.saveProfile(userData.username, userData.avatarId)
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
