package com.fliq.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_progress")
data class StageProgressEntity(
    @PrimaryKey val stageId: String,
    val userId: String,
    val starsEarned: Int,       // 0 to 3
    val bestScore: Int,
    val bestTime: Long,
    val isUnlocked: Boolean = false,
    val isBackedUp: Boolean = false
)
