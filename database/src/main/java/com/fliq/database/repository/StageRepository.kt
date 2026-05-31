package com.fliq.database.repository

import com.fliq.database.StageProgressEntity
import kotlinx.coroutines.flow.Flow

interface StageRepository {
    suspend fun saveStageProgress(progress: StageProgressEntity)
    fun getAllStageProgress(userId: String): Flow<List<StageProgressEntity>>
    suspend fun getStageProgress(stageId: String, userId: String): StageProgressEntity?
    suspend fun getPendingStages(): List<StageProgressEntity>
    suspend fun markStagesAsBackedUp(stageIds: List<String>, userId: String)
}
