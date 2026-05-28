package com.fliq.views

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.fliq.common.repository.ProfileRepository
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.game_engine.repository.GamePreferencesRepository
import com.fliq.game_engine.repository.SoundRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var preferencesRepository: GamePreferencesRepository

    @Inject
    lateinit var soundRepository: SoundRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Permission result handled by OS and settings toggle */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            FliqTheme(settingsRepository = settingsRepository) {
                SplashScreen {
                    soundRepository.startBackgroundMusic()
                    val auth = FirebaseAuth.getInstance()
                    if (auth.currentUser != null) {
                        if (preferencesRepository.isUserCalibrated()) {
                            val targetActivity = MainActivity::class.java

                            startActivity(Intent(this, targetActivity))
                            finish()
                        } else {
                            val targetActivity = CalibrationActivity::class.java

                            startActivity(Intent(this, targetActivity))
                            finish()
                        }
                    } else {
                        val targetActivity = AuthenticationActivity::class.java

                        startActivity(Intent(this, targetActivity))
                        finish()
                    }
                }
            }
        }
    }
}
