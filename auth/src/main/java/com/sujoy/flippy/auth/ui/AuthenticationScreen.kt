package com.sujoy.flippy.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.core.theme.FlippyTheme

@Composable
fun AuthenticationScreen(
    uiState: AppUIState,
    onGoogleSignIn: () -> Unit,
    onAuthSuccess: () -> Unit,
    onErrorShown: (String) -> Unit
) {

    LaunchedEffect(uiState) {
        if (uiState is AppUIState.Success) {
            onAuthSuccess()
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
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    FlippyTheme {
        AuthenticationScreen(
            uiState = AppUIState.Idle,
            onGoogleSignIn = {},
            onAuthSuccess = {},
            onErrorShown = {},
        )
    }
}