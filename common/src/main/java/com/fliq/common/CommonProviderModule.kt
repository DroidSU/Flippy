package com.fliq.common

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonProviderModule {


    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        val url = BuildConfig.FIREBASE_DATABASE_URL
        return if (url.isEmpty()) {
            FirebaseDatabase.getInstance().reference
        } else {
            FirebaseDatabase.getInstance(url).reference
        }
    }
}
