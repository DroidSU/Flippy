package com.fliq.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fliq.core.ConstantsManager

@Database(entities = [MatchHistory::class, UserEntity::class, BadgeEntity::class], version = 14)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDAO
    abstract fun userDao(): UserDAO
    abstract fun badgeDao(): BadgeDAO

    companion object {
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_data` ADD COLUMN `latencyOffset` INTEGER")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_data` ADD COLUMN `badges` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `challengeName` TEXT NOT NULL DEFAULT 'UNKNOWN'")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `${ConstantsManager.TABLE_NAME_MATCH_HISTORY}` ADD COLUMN `levelReached` INTEGER NOT NULL DEFAULT 1")
            }
        }

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

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `${ConstantsManager.TABLE_NAME_BADGES}` (
                        `badgeId` TEXT NOT NULL, 
                        `userId` TEXT NOT NULL, 
                        `unlockTimestamp` INTEGER NOT NULL, 
                        `isBackedUp` INTEGER NOT NULL DEFAULT 0, 
                        PRIMARY KEY(`badgeId`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // To change Primary Key in SQLite, we must recreate the table
                db.execSQL("CREATE TABLE IF NOT EXISTS `badges_new` (`badgeId` TEXT NOT NULL, `userId` TEXT NOT NULL, `unlockTimestamp` INTEGER NOT NULL, `isBackedUp` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`badgeId`, `userId`))")
                db.execSQL("INSERT INTO `badges_new` (`badgeId`, `userId`, `unlockTimestamp`, `isBackedUp`) SELECT `badgeId`, `userId`, `unlockTimestamp`, `isBackedUp` FROM `${ConstantsManager.TABLE_NAME_BADGES}`")
                db.execSQL("DROP TABLE `${ConstantsManager.TABLE_NAME_BADGES}`")
                db.execSQL("ALTER TABLE `badges_new` RENAME TO `${ConstantsManager.TABLE_NAME_BADGES}`")
            }
        }
    }
}
