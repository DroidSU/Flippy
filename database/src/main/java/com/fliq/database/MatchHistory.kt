package com.fliq.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fliq.core.ConstantsManager

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
    val isBackedUp: Boolean = false,
    @ColumnInfo(defaultValue = "Anonymous")
    val username: String = "",
    @ColumnInfo(defaultValue = "1")
    val avatarId : Int = 1
)

fun MatchHistory.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "playerId" to playerId,
        "score" to score,
        "difficulty" to difficulty,
        "gameDuration" to gameDuration,
        "timestamp" to timestamp,
        "correctTaps" to correctTaps,
        "totalTaps" to totalTaps,
        "totalReflexTime" to totalReflexTime,
        "perfectStreak" to perfectStreak,
        "isBackedUp" to isBackedUp,
        "username" to username,
        "avatarId" to avatarId
    )
}
