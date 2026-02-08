package com.sujoy.flippy.database

import android.content.Context
import androidx.room.Room
import com.sujoy.flippy.core.ConstantsManager
import com.sujoy.flippy.database.repository.MatchRepository
import com.sujoy.flippy.database.repository.MatchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                ConstantsManager.DATABASE_NAME
            )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4
            )
            .build()
        }

        @Provides
        fun provideMatchDao(appDatabase: AppDatabase): MatchDAO {
            return appDatabase.matchDao()
        }
    }
}
