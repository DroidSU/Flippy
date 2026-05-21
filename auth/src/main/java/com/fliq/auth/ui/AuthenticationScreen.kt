package com.fliq.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fliq.common.AppUIState
import com.fliq.core.models.UserData

@Composable
fun AuthenticationScreen(
    uiState: AppUIState,
    userData: UserData?,
    onGoogleSignIn: () -> Unit,
    onAuthSuccess: () -> Unit,
    onErrorShown: (String) -> Unit,
    onSaveUser: (String, Int) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onAvatarChanged: (Int) -> Unit,
    showProfileDialog: Boolean,
) {

    LaunchedEffect(uiState) {
        if (uiState is AppUIState.Success) {
            onAuthSuccess()
        }
        else{
            if (uiState is AppUIState.Error) {
                onErrorShown(uiState.message)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showProfileDialog) {
            ProfileSetupScreen(
                username = userData?.username ?: "",
                avatarId = userData?.avatarId ?: 1,
                isLoading = uiState is AppUIState.Loading,
                onUsernameChanged = onUsernameChanged,
                onAvatarChanged = onAvatarChanged,
                onSave = {
                    userData?.let {
                        onSaveUser(it.username, it.avatarId)
                    }
                }
            )
        } else {
            LoginOptionsView(
                isLoading = uiState is AppUIState.Loading,
                onGoogleSignIn = onGoogleSignIn
            )
        }
    }
}
