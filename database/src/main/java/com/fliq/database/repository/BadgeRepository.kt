package com.fliq.database.repository

import com.fliq.database.BadgeEntity
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>
    suspend fun getBadgesForUserSync(userId: String): List<BadgeEntity>
    suspend fun saveBadge(badgeId: String, userId: String, isBackedUp: Boolean = false)
    suspend fun getPendingBadges(): List<BadgeEntity>
    suspend fun markBadgesAsBackedUp(badgeIds: List<String>, userId: String)
    suspend fun syncBadgesFromServer(badges: List<BadgeEntity>)
}
