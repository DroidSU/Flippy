package com.sujoy.flippy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sujoy.flippy.core.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_NAME_MATCH_HISTORY)
data class MatchHistory(
    @PrimaryKey val id: String = "",
    val playerId: String = "",
    val score: Int = 0,
    val difficulty: String = "",
    val gameDuration: Long = 0,
    val timestamp: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val correctTaps: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val totalTaps: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val totalReflexTime: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val perfectStreak: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val isBackedUp: Boolean = false
)