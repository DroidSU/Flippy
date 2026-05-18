package com.sujoy.flippy.views

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
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.core.settings.SettingsRepository
import com.sujoy.flippy.core.theme.FliqTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "AuthenticationActivity"

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

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
            FliqTheme(settingsRepository = settingsRepository) {
                val uiState by viewModel.uiState.collectAsState(AppUIState.Idle)
                val userData by viewModel.userData.collectAsState()
                val showEditDialog by viewModel.showEditDialog.collectAsState()

                AuthenticationScreen(
                    uiState = uiState,
                    userData = userData,
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
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                    },
                    onSaveUser = { username, avatarId ->
                        viewModel.saveUserProfile(username, avatarId)
                    },
                    onUsernameChanged = {
                        viewModel.onUsernameChanged(it)
                    },
                    onAvatarChanged = {
                        viewModel.onAvatarIdChanged(it)
                    },
                    showProfileDialog = showEditDialog
                )
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
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
            Log.w(TAG, "Google Sign-In flow error. ${result.data.toString()}")
        }
    }
}
