package com.sujoy.flippy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sujoy.flippy.common.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_NAME_MATCH_HISTORY)
data class MatchHistory(
    @PrimaryKey val id: String,
    val playerId: String,
    val score: Int,
    val difficulty: String,
    val gameDuration: Long,
    val timestamp: Long,
)