package com.fliq.database.repository

import com.fliq.database.BadgeDAO
import com.fliq.database.BadgeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepositoryImpl @Inject constructor(
    private val badgeDao: BadgeDAO
) : BadgeRepository {
    override fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>> {
        return badgeDao.getBadgesForUser(userId)
    }

    override suspend fun getBadgesForUserSync(userId: String): List<BadgeEntity> {
        return badgeDao.getBadgesForUserSync(userId)
    }

    override suspend fun saveBadge(badgeId: String, userId: String, isBackedUp: Boolean) {
        val existingBadges = badgeDao.getBadgesForUserSync(userId)
        if (existingBadges.none { it.badgeId == badgeId }) {
            badgeDao.insertBadge(
                BadgeEntity(
                    badgeId = badgeId,
                    userId = userId,
                    unlockTimestamp = System.currentTimeMillis(),
                    isBackedUp = isBackedUp
                )
            )
        }
    }

    override suspend fun getPendingBadges(): List<BadgeEntity> {
        return badgeDao.getPendingBadges()
    }

    override suspend fun markBadgesAsBackedUp(badgeIds: List<String>, userId: String) {
        badgeDao.markAsBackedUp(badgeIds, userId)
    }

    override suspend fun syncBadgesFromServer(badges: List<BadgeEntity>) {
        badgeDao.insertBadges(badges.map { it.copy(isBackedUp = true) })
    }
}
