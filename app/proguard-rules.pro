# Firebase and Play Services rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep data models used by Firebase and Room
-keepclassmembers class com.sujoy.flippy.core.models.** { *; }
-keepclassmembers class com.sujoy.flippy.database.** { *; }
-keep class com.sujoy.flippy.core.models.** { *; }
-keep class com.sujoy.flippy.database.** { *; }
-keep class com.sujoy.flippy.common.LeaderboardModel { *; }

# Room specific rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Lottie rules
-keep class com.airbnb.lottie.** { *; }

# Hilt rules
-keep class dagger.hilt.** { *; }
-keep class com.sujoy.flippy.**_HiltComponents* { *; }
-keep class com.sujoy.flippy.Hilt_* { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {}

# Preserve line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
