package com.fliq.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStageProgress(progress: StageProgressEntity)

    @Query("SELECT * FROM stage_progress WHERE userId = :userId")
    fun getAllStageProgress(userId: String): Flow<List<StageProgressEntity>>

    @Query("SELECT * FROM stage_progress WHERE stageId = :stageId AND userId = :userId")
    suspend fun getStageProgress(stageId: String, userId: String): StageProgressEntity?

    @Query("UPDATE stage_progress SET isBackedUp = 1 WHERE stageId IN (:stageIds) AND userId = :userId")
    suspend fun markStagesAsBackedUp(stageIds: List<String>, userId: String)

    @Query("SELECT * FROM stage_progress WHERE isBackedUp = 0")
    suspend fun getPendingStages(): List<StageProgressEntity>
}
