package com.fliq.database.repository

import com.fliq.database.StageDAO
import com.fliq.database.StageProgressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StageRepositoryImpl @Inject constructor(
    private val stageDAO: StageDAO
) : StageRepository {
    override suspend fun saveStageProgress(progress: StageProgressEntity) {
        stageDAO.insertStageProgress(progress)
    }

    override fun getAllStageProgress(userId: String): Flow<List<StageProgressEntity>> {
        return stageDAO.getAllStageProgress(userId)
    }

    override suspend fun getStageProgress(stageId: String, userId: String): StageProgressEntity? {
        return stageDAO.getStageProgress(stageId, userId)
    }

    override suspend fun getPendingStages(): List<StageProgressEntity> {
        return stageDAO.getPendingStages()
    }

    override suspend fun markStagesAsBackedUp(stageIds: List<String>, userId: String) {
        stageDAO.markStagesAsBackedUp(stageIds, userId)
    }
}
