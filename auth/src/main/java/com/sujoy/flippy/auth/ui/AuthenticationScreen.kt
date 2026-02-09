package com.sujoy.flippy.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.core.models.UserData
import com.sujoy.flippy.profile.components.EditDialog

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
        LoginOptionsView(
            isLoading = uiState is AppUIState.Loading,
            onGoogleSignIn = onGoogleSignIn
        )

        if (showProfileDialog) {
            EditDialog(
                username = userData?.username ?: "",
                avatarId = userData?.avatarId ?: 1,
                isLoading = uiState is AppUIState.Loading,
                onUserNameChanged = {
                    onUsernameChanged(it)
                },
                onAvatarChanged = {
                    onAvatarChanged(it)
                },
                onSave = { u, a ->
                    onSaveUser(u, a)
                },
                onDismiss = {
                    // During initial sign-in, we might not want to allow dismiss
                }
            )
        }
    }
}
