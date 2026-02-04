package com.sujoy.flippy.game_engine

import com.sujoy.flippy.game_engine.repository.GamePreferencesRepository
import com.sujoy.flippy.game_engine.repository.GamePreferencesRepositoryImpl
import com.sujoy.flippy.game_engine.repository.MatchRepository
import com.sujoy.flippy.game_engine.repository.MatchRepositoryImpl
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.repository.SoundRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GameEngineModule {

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

    @Binds
    @Singleton
    abstract fun bindSoundRepository(impl: SoundRepositoryImpl): SoundRepository

    @Binds
    @Singleton
    abstract fun bindGamePreferencesRepository(impl: GamePreferencesRepositoryImpl): GamePreferencesRepository
}
