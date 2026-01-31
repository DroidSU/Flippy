package com.sujoy.flippy.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.flippy.core.theme.FlippyTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                SplashScreen {
                    val auth = FirebaseAuth.getInstance()
                    // Check if user is actually signed in via Firebase
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
