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
import com.sujoy.flippy.R
import com.sujoy.flippy.components.auth.AuthenticationScreen
import com.sujoy.flippy.repositories.game.SoundRepository
import com.sujoy.flippy.repositories.game.SoundRepositoryImpl
import com.sujoy.flippy.ui.theme.FlippyTheme
import com.sujoy.flippy.vm.AuthViewModel
import com.sujoy.flippy.vm.ViewModelFactory

private const val TAG = "AuthenticationActivity"

class AuthenticationActivity : ComponentActivity() {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    // Single instance of SoundRepository for all sounds
    private lateinit var soundRepository: SoundRepository

    private val viewModel: AuthViewModel by viewModels { ViewModelFactory(soundRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundRepository = SoundRepositoryImpl(this)

        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                val uiState by viewModel.uiState.collectAsState()

                AuthenticationScreen(
                    uiState = uiState,
                    onAuthSuccess = {
                        Toast.makeText(this, "Welcome to Flippy!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onGoogleSignIn = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    },
                    onPhoneSignIn = { phoneNumber -> viewModel.sendOtp(this, phoneNumber) },
                    onGuestSignIn = {
//                        viewModel.signInAsGuest()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onVerifyOtp = viewModel::verifyOtp,
                    onResetAuthFlow = viewModel::resetAuthFlow,
                    onErrorShown = viewModel::errorShown
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
                Log.d(TAG, "Google Sign-In successful, getting credential.")
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

