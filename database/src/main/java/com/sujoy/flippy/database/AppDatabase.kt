package com.sujoy.flippy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sujoy.flippy.core.ConstantsManager

@Database(entities = [MatchHistory::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDAO

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
                // Since we changed the constant value, we must use the old literal name here to find the table
                db.execSQL("ALTER TABLE `Match History` RENAME TO `match_history`")
            }
        }
    }
}
