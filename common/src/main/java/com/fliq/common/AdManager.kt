package com.fliq.common

import android.app.Activity

interface AdManager {
    fun loadRewardedAd(onAdLoaded: () -> Unit = {}, onAdFailed: () -> Unit = {})
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit
    )
    fun isAdLoaded(): Boolean
}
