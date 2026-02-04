package com.sujoy.flippy.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.sujoy.flippy.BuildConfig
import com.sujoy.flippy.auth.ui.AuthenticationScreen
import com.sujoy.flippy.auth.viewmodel.AuthViewModel
import com.sujoy.flippy.core.theme.FlippyTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AuthenticationActivity"

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                val uiState by viewModel.uiState.collectAsState()

                AuthenticationScreen(
                    uiState = uiState,
                    onAuthSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onGoogleSignIn = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    },
                    onErrorShown = { message ->
                        viewModel.errorShown(message)
                    }
                )
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signInWithCredential(credential)
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign-In failed.", e)
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Log.w(TAG, "Google Sign-In flow was cancelled by user.")
        }
    }
}
