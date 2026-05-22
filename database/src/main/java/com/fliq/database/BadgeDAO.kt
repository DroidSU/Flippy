package com.fliq.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badges WHERE userId = :userId")
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE userId = :userId")
    suspend fun getBadgesForUserSync(userId: String): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE isBackedUp = 0")
    suspend fun getPendingBadges(): List<BadgeEntity>

    @Query("UPDATE badges SET isBackedUp = 1 WHERE badgeId IN (:badgeIds) AND userId = :userId")
    suspend fun markAsBackedUp(badgeIds: List<String>, userId: String)

    @Query("DELETE FROM badges")
    suspend fun clearAll()
}
