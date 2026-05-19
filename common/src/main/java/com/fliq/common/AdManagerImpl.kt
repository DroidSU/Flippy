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
        if (rewardedInterstitialAd != null || isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        
        /* 
        // Loading Rewarded Ad
        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdManager", "Rewarded Ad failed to load: ${adError.message}")
                rewardedAd = null
                isLoading = false
                onAdFailed()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("AdManager", "Rewarded Ad was loaded.")
                rewardedAd = ad
                isLoading = false
                onAdLoaded()
            }
        })
        */

        // Loading Rewarded Interstitial Ad
        RewardedInterstitialAd.load(context, adUnitId, adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdManager", "Rewarded Interstitial failed to load: ${adError.message}")
                rewardedInterstitialAd = null
                isLoading = false
                onAdFailed()
            }

            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                Log.d("AdManager", "Rewarded Interstitial was loaded.")
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
        /*
        // Showing Rewarded Ad
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    onAdClosed()
                    loadRewardedAd({}, {})
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    onAdClosed()
                    loadRewardedAd({}, {})
                }
            }
            ad.show(activity) { onRewardEarned() }
        } ?: run {
            onAdClosed()
            loadRewardedAd({}, {})
        }
        */

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
                    Log.d("AdManager", "Ad failed to show: ${adError.message}")
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
            Log.d("AdManager", "The rewarded interstitial ad wasn't ready yet.")
            onAdClosed()
            loadRewardedAd({}, {})
        }
    }

    override fun isAdLoaded(): Boolean = rewardedInterstitialAd != null
}
