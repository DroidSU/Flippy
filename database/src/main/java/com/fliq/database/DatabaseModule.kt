package com.fliq.database

import android.content.Context
import androidx.room.Room
import com.fliq.core.ConstantsManager
import com.fliq.database.repository.BadgeRepository
import com.fliq.database.repository.BadgeRepositoryImpl
import com.fliq.database.repository.MatchRepository
import com.fliq.database.repository.MatchRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindBadgeRepository(impl: BadgeRepositoryImpl): BadgeRepository

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
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6,
                AppDatabase.MIGRATION_6_7,
                AppDatabase.MIGRATION_7_8,
                AppDatabase.MIGRATION_8_9,
                AppDatabase.MIGRATION_9_10,
                AppDatabase.MIGRATION_10_11,
                AppDatabase.MIGRATION_11_12
            )
                .fallbackToDestructiveMigration(true)
            .build()
        }

        @Provides
        fun provideMatchDao(appDatabase: AppDatabase): MatchDAO {
            return appDatabase.matchDao()
        }

        @Provides
        fun provideUserDao(appDatabase: AppDatabase): UserDAO {
            return appDatabase.userDao()
        }

        @Provides
        fun provideBadgeDao(appDatabase: AppDatabase): BadgeDAO {
            return appDatabase.badgeDao()
        }
    }
}
