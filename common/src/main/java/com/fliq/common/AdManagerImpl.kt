package com.fliq.common

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AdManager {

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    
    private var isLoading = false

    private val adUnitId = BuildConfig.ADMOB_REWARDED_INTERSTITIAL_ID

    override fun loadRewardedAd(onAdLoaded: () -> Unit, onAdFailed: () -> Unit) {
        if (rewardedInterstitialAd != null) {
            Log.d("AdManager", "Ad already loaded.")
            onAdLoaded()
            return
        }
        if (isLoading) {
            Log.d("AdManager", "Ad is already loading.")
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        Log.d("AdManager", "Loading Rewarded Interstitial Ad with ID: $adUnitId")
        
        // Loading Rewarded Interstitial Ad
        RewardedInterstitialAd.load(context, adUnitId, adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdManager", "Rewarded Interstitial failed to load: ${adError.message} (Code: ${adError.code})")
                rewardedInterstitialAd = null
                isLoading = false
                onAdFailed()
            }

            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                Log.d("AdManager", "Rewarded Interstitial was loaded successfully.")
                rewardedInterstitialAd = ad
                isLoading = false
                onAdLoaded()
            }
        })
    }

    override fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit
    ) {
        // Showing Rewarded Interstitial Ad
        rewardedInterstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdManager", "Ad was dismissed.")
                    rewardedInterstitialAd = null
                    onAdClosed()
                    loadRewardedAd({}, {}) // Preload next one
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdManager", "Ad failed to show: ${adError.message} (Code: ${adError.code})")
                    rewardedInterstitialAd = null
                    onAdClosed()
                    loadRewardedAd({}, {})
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d("AdManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } ?: run {
            Log.w("AdManager", "The rewarded interstitial ad wasn't ready yet.")
            onAdClosed()
            loadRewardedAd({}, {})
        }
    }

    override fun isAdLoaded(): Boolean = rewardedInterstitialAd != null
}
