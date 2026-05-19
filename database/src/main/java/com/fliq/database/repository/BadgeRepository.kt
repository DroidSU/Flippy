package com.fliq.database.repository

import com.fliq.database.BadgeEntity
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>
    suspend fun saveBadge(badgeId: String, userId: String)
    suspend fun getPendingBadges(): List<BadgeEntity>
    suspend fun markBadgesAsBackedUp(badgeIds: List<String>)
    suspend fun syncBadgesFromServer(badges: List<BadgeEntity>)
}
