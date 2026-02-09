package com.sujoy.flippy.common

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.format.DateUtils
import androidx.annotation.RequiresPermission
import com.sujoy.flippy.core.R
import com.sujoy.flippy.core.models.UserData
import java.util.Locale
import kotlin.random.Random

class UtilityMethods {
    companion object {
        fun formatTime(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun getRelativeTime(timestamp: Long): String {
            return DateUtils.getRelativeTimeSpanString(
                timestamp,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
        
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            } && activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        fun getAvatarResource(id: Int): Int? {
            return when (id) {
                1 -> R.drawable.user_avatar_1
                2 -> R.drawable.user_avatar_2
                3 -> R.drawable.user_avatar_3
                4 -> R.drawable.user_avatar_4
                5 -> R.drawable.user_avatar_5
                6 -> R.drawable.user_avatar_6
                7 -> R.drawable.user_avatar_7
                8 -> R.drawable.user_avatar_8
                else -> null
            }
        }

        fun UserData.toMap(): Map<String, Any?> {
            return mapOf(
                "userId" to userId,
                "username" to username,
                "avatarId" to avatarId
            )
        }

        fun generateUniqueUsername(): String {
            val adjectives = listOf(
                "Swift", "Flippy", "Mega", "Cool", "Epic", "Turbo", "Ghost", "Neon",
                "Fire", "Shadow", "Elite", "Hyper", "Frost", "Mystic", "Iron", "Storm",
                "Wild", "Silent", "Rapid", "Gilded", "Crimson", "Azure", "Sonic", "Zen",
                "Vortex", "Cosmic", "Solar", "Lunar", "Radiant", "Lucky", "Vivid", "Noble",
                "Primal", "Ancient", "Clever", "Quick", "Zesty", "Flashy", "Dynamic", "Aura",
                "Blazing", "Freezing", "Nimble", "Volcanic", "Celestial", "Galactic", "Stellar", "Snappy"
            )
            val nouns = listOf(
                "Player", "Gamer", "Wizard", "Knight", "Ninja", "Falcon", "Ghost", "Racer",
                "Titan", "Wolf", "Dragon", "Phoenix", "Legend", "Hunter", "Warrior", "Cyborg",
                "Rogue", "Paladin", "Archer", "Bard", "Monk", "Druid", "Master", "Beast",
                "Spirit", "Soul", "Heart", "Mind", "Pulse", "Echo", "Wave", "Spark",
                "Blaze", "Frost", "Reflex", "Wind", "Rain", "Thunder", "Cloud", "Sky",
                "Bolt", "Moon", "Sun", "Star", "Nova", "Comet", "Orbit", "Astro", "Mech"
            )

            val random = Random.Default
            val adjective = adjectives[random.nextInt(adjectives.size)]
            val noun = nouns[random.nextInt(nouns.size)]
            val number = random.nextInt(100, 999)

            // Randomly decide format
            return when (random.nextInt(3)) {
                0 -> "$adjective$noun$number"
                1 -> "${adjective}_$noun"
                else -> {
                    // Leet-like transformation
                    val leetUsername = "$adjective$noun"
                        .replace("a", "4")
                        .replace("e", "3")
                        .replace("i", "1")
                        .replace("o", "0")
                        .replace("s", "5")
                    "$leetUsername$number"
                }
            }
        }
    }
}
