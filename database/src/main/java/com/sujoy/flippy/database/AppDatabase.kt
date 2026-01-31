package com.sujoy.flippy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sujoy.flippy.common.ConstantsManager

@Database(entities = [MatchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    ConstantsManager.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
