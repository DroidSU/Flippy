package com.fliq.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresPermission
import com.fliq.core.R
import java.util.Locale

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
                9 -> R.drawable.user_avatar_9
                10 -> R.drawable.user_avatar_10
                11 -> R.drawable.user_avatar_11
                12 -> R.drawable.user_avatar_12
                else -> null
            }
        }

        fun generateUniqueUsername(): String {
            val prefixes = listOf(
                "Cyber", "Turbo", "Mega", "Phantom", "Glitch", "Ultra", "Retro", "Neon",
                "Zero", "Hyper", "Zen", "Nova", "Vortex", "Astro", "Sonic", "Alpha",
                "Sigma", "Meta", "Giga", "Proto", "Radiant", "Primal", "Vivid", "Stellar",
                "Cosmic", "Solar", "Lunar", "Rapid", "Silent", "Wild", "Iron", "Mystic",
                "Frost", "Shadow", "Fire", "Epic", "Cool", "Swift", "Fliq", "Flash",
                "Aura", "Blaze", "Volt", "Pulse", "Quantum", "Nano", "Flux", "Aero", "Nitro"
            )
            val nouns = listOf(
                "Combo", "Pixel", "Reflex", "Ghost", "Blade", "Spark", "Vibe", "Pulse",
                "Wave", "Echo", "Racer", "Titan", "Wolf", "Dragon", "Phoenix", "Legend",
                "Hunter", "Warrior", "Cyborg", "Rogue", "Archer", "Master", "Beast", "Spirit",
                "Soul", "Heart", "Mind", "Bolt", "Orbit", "Mech", "Grid", "Flux",
                "Drift", "Shift", "Edge", "Zenith", "Apex", "Void", "Arcade", "Sync", "Link"
            )
            val suffixes = listOf(
                "Pro", "Ace", "Flux", "Zen", "Mode", "Bot", "One", "Sky", "Run", "Dash",
                "Core", "Loop", "Zone", "Prime", "Ultra", "X", "Max", "Go", "Play", "V"
            )
            val connectors = listOf("@", "&", "!")

            val prefix = prefixes.random()
            val noun = nouns.random()
            val suffix = suffixes.random()
            val connector = connectors.random()

            val templates = listOf(
                { "$prefix$noun" },
                { "$noun$suffix" },
                { "$prefix$suffix" },
                { "$prefix$noun" },
                { "$noun$suffix" },
                { "$prefix$suffix" },
                { "$prefix$connector$noun" },
                { "$noun$connector$suffix" },
                { "$prefix$noun!" },
                { "$noun$connector$prefix" }
            )

            var username = templates.random().invoke()

            // Ensure length doesn't exceed 12 characters
            if (username.length > 12) {
                username = username.take(12)
            }

            return username
        }

        fun getAppVersionName(context: Context): String {
            return try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(context.packageName, 0)
                }
                packageInfo.versionName ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}
