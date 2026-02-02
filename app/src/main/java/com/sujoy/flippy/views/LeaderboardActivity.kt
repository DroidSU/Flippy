package com.sujoy.flippy.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.leaderboard.views.LeaderboardScreen

class LeaderboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippyTheme {
                LeaderboardScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}