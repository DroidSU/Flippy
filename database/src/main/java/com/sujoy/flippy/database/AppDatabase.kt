package com.sujoy.flippy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sujoy.flippy.core.ConstantsManager

@Database(entities = [MatchHistory::class, UserEntity::class], version = 7)
abstract class AppDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDAO
    abstract fun userDao(): UserDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `correctTaps` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `totalTaps` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `totalReflexTime` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `perfectStreak` INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `isBackedUp` INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `Match History` RENAME TO `match_history`")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `username` TEXT NOT NULL DEFAULT 'Anonymous'")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `avatarId` INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_data` (
                        `userId` TEXT NOT NULL, 
                        `username` TEXT NOT NULL, 
                        `avatarId` INTEGER NOT NULL, 
                        `totalMatches` INTEGER NOT NULL, 
                        `highestScore` INTEGER NOT NULL, 
                        `longestRound` INTEGER NOT NULL, 
                        `totalCorrectTaps` INTEGER NOT NULL, 
                        `totalTaps` INTEGER NOT NULL, 
                        `totalReflexTime` INTEGER NOT NULL, 
                        `bestPerfectStreak` INTEGER NOT NULL, 
                        PRIMARY KEY(`userId`)
                    )
                """.trimIndent())
            }
        }
    }
}
