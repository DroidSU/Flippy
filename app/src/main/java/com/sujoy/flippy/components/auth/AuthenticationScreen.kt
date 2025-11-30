package com.sujoy.flippy.components.auth

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sujoy.flippy.R
import com.sujoy.flippy.models.AuthUiState
import com.sujoy.flippy.ui.theme.FlippyTheme


@Composable
fun AuthenticationScreen(
    uiState: AuthUiState,
    onAuthSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit,
    onGuestSignIn: () -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResetAuthFlow: () -> Unit,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current

    // --- Handle Side Effects ---
    LaunchedEffect(uiState.isAuthSuccessful) {
        if (uiState.isAuthSuccessful) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onErrorShown()
        }
    }

    // A beautiful gradient background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
                .systemBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WelcomeHeader()

                // Animated transition between Login and OTP views
                Crossfade(
                    targetState = uiState.isOtpSent,
                    label = "AuthScreenAnimation",
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) { isOtpScreen ->
                    if (isOtpScreen) {
                        OtpEntryView(
                            isLoading = uiState.isLoading,
                            resendTimer = uiState.resendTimer,
                            onVerifyOtp = onVerifyOtp,
                            onBack = onResetAuthFlow
                        )
                    } else {
                        LoginOptionsView(
                            isLoading = uiState.isLoading,
                            onGoogleSignIn = onGoogleSignIn,
                            onPhoneSignIn = onPhoneSignIn,
                            onGuestSignIn = onGuestSignIn
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // --- Full screen loading overlay ---
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader() {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_welcome) // Make sure you have this Lottie file
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        speed = 0.8f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        LottieAnimation(
            composition = lottieComposition,
            progress = { lottieProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Welcome to Flippy!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Let's get you signed in to start the fun.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    FlippyTheme {
        AuthenticationScreen(
            uiState = AuthUiState(isLoading = false, isOtpSent = false),
            onAuthSuccess = {},
            onGoogleSignIn = {},
            onPhoneSignIn = {},
            onGuestSignIn = {},
            onVerifyOtp = {},
            onResetAuthFlow = {},
            onErrorShown = {}
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun OtpScreenPreview() {
//    FlippyTheme {
//        AuthenticationScreen(
//            uiState = AuthUiState(isLoading = false, isOtpSent = true, resendTimer = 45),
//            onAuthSuccess = {},
//            onGoogleSignIn = {},
//            onPhoneSignIn = {},
//            onVerifyOtp = {},
//            onResetAuthFlow = {},
//            onErrorShown = {}
//        )
//    }
//}