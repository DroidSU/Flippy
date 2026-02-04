package com.sujoy.flippy.common

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.format.DateUtils
import androidx.annotation.RequiresPermission
import com.sujoy.flippy.core.R
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
                else -> null
            }
        }
    }
}
