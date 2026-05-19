package com.fliq.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fliq.core.settings.SettingsRepository
import com.fliq.core.theme.FliqTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FliqTheme(settingsRepository = settingsRepository) {
                SplashScreen {
                    val auth = FirebaseAuth.getInstance()
                    val targetActivity = if (auth.currentUser != null) {
                        MainActivity::class.java
                    } else {
                        AuthenticationActivity::class.java
                    }
                    startActivity(Intent(this, targetActivity))
                    finish()
                }
            }
        }
    }
}
