# --- General Android & Project Rules ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Firebase Rules ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# --- Data Models (Firestore/Room) ---
# Keep all data models to prevent shrinking of fields needed for serialization
-keep class com.fliq.core.models.** { *; }
-keep class com.fliq.database.models.** { *; }
-keep class com.fliq.common.models.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- AdMob Rules ---
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-keep class com.fliq.common.BuildConfig { *; }
-keep class com.fliq.BuildConfig { *; }
-keepattributes *Annotation*

# For Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep AdManager implementation
-keep class com.fliq.common.AdManagerImpl { *; }
-keepclassmembers class com.fliq.common.AdManagerImpl { *; }

# Keep AdMob Callbacks
-keep class * extends com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback { *; }
-keep class * extends com.google.android.gms.ads.FullScreenContentCallback { *; }
-keep class * extends com.google.android.gms.ads.rewarded.RewardedAdLoadCallback { *; }

# --- Hilt & Dagger ---
-keep class dagger.hilt.** { *; }
-keep class com.fliq.**_HiltComponents* { *; }
-keep class com.fliq.Hilt_* { *; }
-keep class * { @dagger.hilt.android.lifecycle.HiltViewModel <methods>; }
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep class * { @javax.inject.Inject <init>(...); }
-keep class * { @javax.inject.Singleton *; }

# Keep Hilt generated classes for Application
-keep class com.fliq.CustomApplication { *; }
-keep class com.fliq.Hilt_CustomApplication { *; }

# --- Lottie ---
-keep class com.airbnb.lottie.** { *; }

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {}

# --- Jetpack Compose ---
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.ui.platform.AndroidComposeView
