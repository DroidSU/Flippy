package com.fliq.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fliq.common.repository.ProfileRepository
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.fliq.game_engine.repository.GamePreferencesRepository
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                SplashScreen {
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
