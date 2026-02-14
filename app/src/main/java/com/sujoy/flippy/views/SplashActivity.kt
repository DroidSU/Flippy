package com.sujoy.flippy.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flippy.game2.ui.NewGameActivity
import com.sujoy.flippy.core.settings.SettingsRepository
import com.sujoy.flippy.core.theme.FlippyTheme
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
            FlippyTheme(settingsRepository = settingsRepository) {
                SplashScreen {
//                    val auth = FirebaseAuth.getInstance()
//                    val targetActivity = if (auth.currentUser != null) {
//                        MainActivity::class.java
//                    } else {
//                        AuthenticationActivity::class.java
//                    }
//                    startActivity(Intent(this, targetActivity))
                    startActivity(Intent(this, NewGameActivity::class.java))
                    finish()
                }
            }
        }
    }
}
