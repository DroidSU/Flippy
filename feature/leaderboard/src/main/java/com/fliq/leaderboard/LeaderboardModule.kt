package com.fliq.leaderboard

import com.fliq.leaderboard.repository.LeaderboardRepository
import com.fliq.leaderboard.repository.LeaderboardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LeaderboardModule {

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(impl: LeaderboardRepositoryImpl): LeaderboardRepository
}
